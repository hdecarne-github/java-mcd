/*
 * Copyright (c) 2019 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.mcd.jvm;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.carne.boot.check.Check;
import de.carne.boot.logging.Log;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.util.Late;

/**
 * Java byte code decoder.
 */
public class JvmMachineCodeDecoder extends MachineCodeDecoder {

	private static final Log LOG = new Log();

	private static final String NAME = "Java Byte Code";

	/**
	 * Constructs a new {@linkplain JvmMachineCodeDecoder} instance.
	 */
	public JvmMachineCodeDecoder() {
		super(NAME, ByteOrder.BIG_ENDIAN);
	}

	@Override
	public void decode(ReadableByteChannel in, MCDOutput out) throws IOException {
		MCDDecodeBuffer buffer = newDecodeBuffer(in);

		buffer.decodeMagic(0xcafebabe);

		DecodedClassInfo decoded = new DecodedClassInfo();

		decodeClass(decoded, buffer);
		decodeFields(decoded, buffer);
		decodeMethods(decoded, buffer);
		decodeAttributes(decoded, buffer);

		ClassPrinter classPrinter;

		if (ClassUtil.isPackageInfo(decoded)) {
			classPrinter = new PackageClassPrinter(out, decoded);
		} else if (ClassUtil.isInterface(decoded)) {
			classPrinter = new InterfaceClassPrinter(out, decoded);
		} else if (ClassUtil.isEnum(decoded)) {
			classPrinter = new EnumClassPrinter(out, decoded);
		} else {
			classPrinter = new StandardClassPrinter(out, decoded);
		}
		classPrinter.print();
	}

	private void decodeClass(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		decoded.setMinorVersion(Short.toUnsignedInt(buffer.decodeI16()));
		decoded.setMajorVersion(Short.toUnsignedInt(buffer.decodeI16()));
		decodeConstantPool(decoded, buffer);
		decoded.setAccessFlags(Short.toUnsignedInt(buffer.decodeI16()));

		int thisClass = Short.toUnsignedInt(buffer.decodeI16());

		decoded.setThisClass(ClassName.fromConstant(decoded.resolveConstant(thisClass, ClassConstant.class)));

		int superClass = Short.toUnsignedInt(buffer.decodeI16());

		decoded.setSuperClass(ClassName
				.fromConstant(superClass != 0 ? decoded.resolveConstant(superClass, ClassConstant.class) : null));

		int interfacesCount = Short.toUnsignedInt(buffer.decodeI16());
		ByteBuffer interfacesIndices = buffer.decodeI16Array(interfacesCount);
		List<ClassName> interfaces = new ArrayList<>(interfacesCount);

		while (interfacesIndices.hasRemaining()) {
			int interfaceIndex = Short.toUnsignedInt(interfacesIndices.getShort());

			interfaces.add(ClassName.fromConstant(decoded.resolveConstant(interfaceIndex, ClassConstant.class)));
		}
		decoded.setInterface(Collections.unmodifiableList(interfaces));
	}

	private void decodeConstantPool(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int count = Short.toUnsignedInt(buffer.decodeI16());

		if (count == 0) {
			throw new IOException("Invalid constant_pool_count: " + count);
		}

		int index = 1;

		while (index < count) {
			int nextIndex = index + 1;
			byte tag = buffer.decodeI8();
			Constant constant;

			switch (tag) {
			// CONSTANT_Utf8
			case Utf8Constant.TAG:
				constant = decodeUtf8Constant(decoded, buffer);
				break;
			// CONSTANT_Integer
			case IntegerConstant.TAG:
				constant = decodeIntegerConstant(decoded, buffer);
				break;
			// CONSTANT_Float
			case FloatConstant.TAG:
				constant = decodeFloatConstant(decoded, buffer);
				break;
			// CONSTANT_Long
			case LongConstant.TAG:
				constant = decodeLongConstant(decoded, buffer);
				nextIndex++;
				break;
			// CONSTANT_Double
			case DoubleConstant.TAG:
				constant = decodeDoubleConstant(decoded, buffer);
				nextIndex++;
				break;
			// CONSTANT_Class
			case ClassConstant.TAG:
				constant = decodeClassConstant(decoded, buffer);
				break;
			// CONSTANT_String
			case StringConstant.TAG:
				constant = decodeStringConstant(decoded, buffer);
				break;
			// CONSTANT_Fieldref
			case FieldRefConstant.TAG:
				constant = decodeFieldRefConstant(decoded, buffer);
				break;
			// CONSTANT_Methodref
			case MethodRefConstant.TAG:
				constant = decodeMethodRefConstant(decoded, buffer);
				break;
			// CONSTANT_InterfaceMethodref
			case InterfaceMethodRefConstant.TAG:
				constant = decodeInterfaceMethodRefConstant(decoded, buffer);
				break;
			// CONSTANT_NameAndType
			case NameAndTypeConstant.TAG:
				constant = decodeNameAndTypeConstant(decoded, buffer);
				break;
			// CONSTANT_MethodHandle
			case MethodHandleConstant.TAG:
				constant = decodeMethodHandleConstant(decoded, buffer);
				break;
			// CONSTANT_MethodType
			case MethodTypeConstant.TAG:
				constant = decodeMethodTypeConstant(decoded, buffer);
				break;
			// CONSTANT_Dynamic
			case DynamicConstant.TAG:
				constant = decodeDynamicConstant(decoded, buffer);
				break;
			// CONSTANT_InvokeDynamic
			case InvokeDynamicConstant.TAG:
				constant = decodeInvokeDynamicConstant(decoded, buffer);
				break;
			// CONSTANT_Module
			case ModuleConstant.TAG:
				constant = decodeModuleConstant(decoded, buffer);
				break;
			// CONSTANT_Package
			case PackageConstant.TAG:
				constant = decodePackageConstant(decoded, buffer);
				break;
			default:
				throw new IOException("Unrecognized constant tag: " + tag);
			}
			decoded.putConstant(index, constant);
			index = nextIndex;
		}
	}

	private Utf8Constant decodeUtf8Constant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int length = Short.toUnsignedInt(buffer.decodeI16());
		String string = StandardCharsets.UTF_8.decode(buffer.decodeI8Array(length)).toString();

		return new Utf8Constant(decoded, string);
	}

	private IntegerConstant decodeIntegerConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int value = buffer.decodeI32();

		return new IntegerConstant(decoded, value);
	}

	private FloatConstant decodeFloatConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		float value = buffer.decodeF32();

		return new FloatConstant(decoded, value);
	}

	private LongConstant decodeLongConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		long value = buffer.decodeI64();

		return new LongConstant(decoded, value);
	}

	private DoubleConstant decodeDoubleConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		double value = buffer.decodeF64();

		return new DoubleConstant(decoded, value);
	}

	private ClassConstant decodeClassConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int nameIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new ClassConstant(decoded, nameIndex);
	}

	private StringConstant decodeStringConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int stringIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new StringConstant(decoded, stringIndex);
	}

	private FieldRefConstant decodeFieldRefConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int classIndex = Short.toUnsignedInt(buffer.decodeI16());
		int nameAndTypeIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new FieldRefConstant(decoded, classIndex, nameAndTypeIndex);
	}

	private MethodRefConstant decodeMethodRefConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int classIndex = Short.toUnsignedInt(buffer.decodeI16());
		int nameAndTypeIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new MethodRefConstant(decoded, classIndex, nameAndTypeIndex);
	}

	private InterfaceMethodRefConstant decodeInterfaceMethodRefConstant(DecodedClassInfo decoded,
			MCDDecodeBuffer buffer) throws IOException {
		int classIndex = Short.toUnsignedInt(buffer.decodeI16());
		int nameAndTypeIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new InterfaceMethodRefConstant(decoded, classIndex, nameAndTypeIndex);
	}

	private NameAndTypeConstant decodeNameAndTypeConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int nameIndex = Short.toUnsignedInt(buffer.decodeI16());
		int descriptorIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new NameAndTypeConstant(decoded, nameIndex, descriptorIndex);
	}

	private MethodHandleConstant decodeMethodHandleConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int referenceKindValue = Byte.toUnsignedInt(buffer.decodeI8());
		int referenceIndex = Short.toUnsignedInt(buffer.decodeI16());
		ReferenceKind referenceKind;

		switch (referenceKindValue) {
		case 1:
			referenceKind = ReferenceKind.REF_getField;
			break;
		case 2:
			referenceKind = ReferenceKind.REF_getStatic;
			break;
		case 3:
			referenceKind = ReferenceKind.REF_putField;
			break;
		case 4:
			referenceKind = ReferenceKind.REF_putStatic;
			break;
		case 5:
			referenceKind = ReferenceKind.REF_invokeVirtual;
			break;
		case 6:
			referenceKind = ReferenceKind.REF_invokeStatic;
			break;
		case 7:
			referenceKind = ReferenceKind.REF_invokeSpecial;
			break;
		case 8:
			referenceKind = ReferenceKind.REF_newInvokeSpecial;
			break;
		case 9:
			referenceKind = ReferenceKind.REF_invokeInterface;
			break;
		default:
			throw new IOException("Unrecognized reference kind: " + referenceKindValue);
		}
		return new MethodHandleConstant(decoded, referenceKind, referenceIndex);
	}

	private MethodTypeConstant decodeMethodTypeConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int descriptorIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new MethodTypeConstant(decoded, descriptorIndex);
	}

	private DynamicConstant decodeDynamicConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int bootstrapMethodAttrIndex = Short.toUnsignedInt(buffer.decodeI16());
		int nameAndTypeIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new DynamicConstant(decoded, bootstrapMethodAttrIndex, nameAndTypeIndex);
	}

	private InvokeDynamicConstant decodeInvokeDynamicConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int bootstrapMethodAttrIndex = Short.toUnsignedInt(buffer.decodeI16());
		int nameAndTypeIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new InvokeDynamicConstant(decoded, bootstrapMethodAttrIndex, nameAndTypeIndex);
	}

	private ModuleConstant decodeModuleConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int nameIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new ModuleConstant(decoded, nameIndex);
	}

	private PackageConstant decodePackageConstant(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int nameIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new PackageConstant(decoded, nameIndex);
	}

	private void decodeFields(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int fieldsCount = Short.toUnsignedInt(buffer.decodeI16());
		List<Field> fields = new ArrayList<>(fieldsCount);

		for (int fieldsIndex = 0; fieldsIndex < fieldsCount; fieldsIndex++) {
			int fieldAccessFlags = Short.toUnsignedInt(buffer.decodeI16());
			int fieldNameIndex = Short.toUnsignedInt(buffer.decodeI16());
			int fieldDescriptorIndex = Short.toUnsignedInt(buffer.decodeI16());
			int fieldAttributesCount = Short.toUnsignedInt(buffer.decodeI16());
			List<Attribute> fieldAttributes = new LinkedList<>();

			for (int fieldAttributeIndex = 0; fieldAttributeIndex < fieldAttributesCount; fieldAttributeIndex++) {
				fieldAttributes.add(decodeAttribute(decoded, buffer));
			}
			fields.add(new Field(decoded, fieldAccessFlags, fieldNameIndex, fieldDescriptorIndex, fieldAttributes));
		}
		decoded.setFields(Collections.unmodifiableList(fields));
	}

	private void decodeMethods(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int methodsCount = Short.toUnsignedInt(buffer.decodeI16());
		List<Method> methods = new ArrayList<>(methodsCount);

		for (int methodsIndex = 0; methodsIndex < methodsCount; methodsIndex++) {
			int methodAccessFlags = Short.toUnsignedInt(buffer.decodeI16());
			int methodNameIndex = Short.toUnsignedInt(buffer.decodeI16());
			int methodDescriptorIndex = Short.toUnsignedInt(buffer.decodeI16());
			int methodAttributesCount = Short.toUnsignedInt(buffer.decodeI16());
			List<Attribute> methodAttributes = new LinkedList<>();

			for (int methodAttributeIndex = 0; methodAttributeIndex < methodAttributesCount; methodAttributeIndex++) {
				methodAttributes.add(decodeAttribute(decoded, buffer));
			}
			methods.add(
					new Method(decoded, methodAccessFlags, methodNameIndex, methodDescriptorIndex, methodAttributes));
		}
		decoded.setMethods(Collections.unmodifiableList(methods));
	}

	private void decodeAttributes(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int attributesCount = Short.toUnsignedInt(buffer.decodeI16());
		List<Attribute> attributes = new ArrayList<>(attributesCount);

		for (int attributesIndex = 0; attributesIndex < attributesCount; attributesIndex++) {
			Attribute attribute = decodeAttribute(decoded, buffer);

			attributes.add(attribute);
		}
		decoded.setAttributes(Collections.unmodifiableList(attributes));
	}

	private Attribute decodeAttribute(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int nameIndex = Short.toUnsignedInt(buffer.decodeI16());
		int length = buffer.decodeI32();
		String attributeName = decoded.resolveConstant(nameIndex, Utf8Constant.class).getValue();
		Attribute attribute;

		switch (attributeName) {
		case SourceFileAttribute.NAME:
			attribute = decodeSourceFileAttribute(decoded, buffer, nameIndex);
			break;
		case ConstantValueAttribute.NAME:
			attribute = decodeConstantValueAttribute(decoded, buffer, nameIndex);
			break;
		case DeprecatedAttribute.NAME:
			attribute = new DeprecatedAttribute(decoded, nameIndex);
			break;
		case RuntimeVisibleAnnotationsAttribute.NAME:
			attribute = decodeRuntimeVisibleAnnotationsAttribute(decoded, buffer, nameIndex);
			break;
		case RuntimeInvisibleAnnotationsAttribute.NAME:
			attribute = decodeRuntimeInvisibleAnnotationsAttribute(decoded, buffer, nameIndex);
			break;
		case SignatureAttribute.NAME:
			attribute = decodeSignatureAttribute(decoded, buffer, nameIndex);
			break;
		case ExceptionsAttribute.NAME:
			attribute = decodeExceptionsAttribute(decoded, buffer, nameIndex);
			break;
		case CodeAttribute.NAME:
			attribute = new CodeAttribute(decoded, nameIndex, buffer.slice(Integer.toUnsignedLong(length)));
			break;
		default:
			buffer.skip(length);
			attribute = new UndecodedAttribute(decoded, nameIndex, length);

			LOG.debug("Skipping attribute ''{0}''", attributeName);
		}
		return attribute;
	}

	private SourceFileAttribute decodeSourceFileAttribute(DecodedClassInfo decoded, MCDDecodeBuffer buffer,
			int nameIndex) throws IOException {
		int sourceFileIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new SourceFileAttribute(decoded, nameIndex, sourceFileIndex);
	}

	private ConstantValueAttribute decodeConstantValueAttribute(DecodedClassInfo decoded, MCDDecodeBuffer buffer,
			int nameIndex) throws IOException {
		int constantValueIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new ConstantValueAttribute(decoded, nameIndex, constantValueIndex);
	}

	private RuntimeVisibleAnnotationsAttribute decodeRuntimeVisibleAnnotationsAttribute(DecodedClassInfo decoded,
			MCDDecodeBuffer buffer, int nameIndex) throws IOException {
		List<Annotation> annotations = decodeAnnotations(decoded, buffer);

		return new RuntimeVisibleAnnotationsAttribute(decoded, nameIndex, annotations);
	}

	private RuntimeInvisibleAnnotationsAttribute decodeRuntimeInvisibleAnnotationsAttribute(DecodedClassInfo decoded,
			MCDDecodeBuffer buffer, int nameIndex) throws IOException {
		List<Annotation> annotations = decodeAnnotations(decoded, buffer);

		return new RuntimeInvisibleAnnotationsAttribute(decoded, nameIndex, annotations);
	}

	private List<Annotation> decodeAnnotations(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int count = Short.toUnsignedInt(buffer.decodeI16());
		List<Annotation> annotations = new LinkedList<>();

		for (int index = 0; index < count; index++) {
			Annotation annotation = decodeAnnotation(decoded, buffer);

			annotations.add(annotation);
		}
		return annotations;
	}

	private Annotation decodeAnnotation(DecodedClassInfo decoded, MCDDecodeBuffer buffer) throws IOException {
		int typeIndex = Short.toUnsignedInt(buffer.decodeI16());
		int elementsCount = Short.toUnsignedInt(buffer.decodeI16());
		List<AnnotationElement> elements = new ArrayList<>(elementsCount);

		for (int elementIndex = 0; elementIndex < elementsCount; elementIndex++) {
			int elementNameIndex = Short.toUnsignedInt(buffer.decodeI16());
			AnnotationElementValue elementValue = decodeAnnotationElementValue(decoded, buffer);

			elements.add(new AnnotationElement(decoded, elementNameIndex, elementValue));
		}
		return new Annotation(decoded, typeIndex, elements);
	}

	private AnnotationElementValue decodeAnnotationElementValue(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int elementTag = Byte.toUnsignedInt(buffer.decodeI8());
		AnnotationElementValue elementValue;

		switch (elementTag) {
		case ByteAnnotationElement.TAG:
			elementValue = new ByteAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case CharAnnotationElement.TAG:
			elementValue = new CharAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case DoubleAnnotationElement.TAG:
			elementValue = new DoubleAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case FloatAnnotationElement.TAG:
			elementValue = new FloatAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case IntAnnotationElement.TAG:
			elementValue = new IntAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case LongAnnotationElement.TAG:
			elementValue = new LongAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case ShortAnnotationElement.TAG:
			elementValue = new ShortAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case BooleanAnnotationElement.TAG:
			elementValue = new BooleanAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case StringAnnotationElement.TAG:
			elementValue = new StringAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case EnumAnnotationElement.TAG:
			elementValue = new EnumAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()),
					Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case ClassAnnotationElement.TAG:
			elementValue = new ClassAnnotationElement(decoded, Short.toUnsignedInt(buffer.decodeI16()));
			break;
		case AnnotationAnnotationElement.TAG:
			elementValue = new AnnotationAnnotationElement(decoded, decodeAnnotation(decoded, buffer));
			break;
		case ArrayAnnotationElement.TAG:
			elementValue = new ArrayAnnotationElement(decoded, decodeAnnotationElementValues(decoded, buffer));
			break;
		default:
			throw new IOException("Unrecognized annotation element tag: " + elementTag);
		}
		return elementValue;
	}

	private List<AnnotationElementValue> decodeAnnotationElementValues(DecodedClassInfo decoded, MCDDecodeBuffer buffer)
			throws IOException {
		int elementValuesCount = Short.toUnsignedInt(buffer.decodeI16());
		List<AnnotationElementValue> elementValues = new ArrayList<>();

		for (int elementValueIndex = 0; elementValueIndex < elementValuesCount; elementValueIndex++) {
			elementValues.add(decodeAnnotationElementValue(decoded, buffer));
		}
		return elementValues;
	}

	private SignatureAttribute decodeSignatureAttribute(DecodedClassInfo decoded, MCDDecodeBuffer buffer, int nameIndex)
			throws IOException {
		int signatureIndex = Short.toUnsignedInt(buffer.decodeI16());

		return new SignatureAttribute(decoded, nameIndex, signatureIndex);
	}

	private ExceptionsAttribute decodeExceptionsAttribute(DecodedClassInfo decoded, MCDDecodeBuffer buffer,
			int nameIndex) throws IOException {
		int exceptionsCount = Short.toUnsignedInt(buffer.decodeI16());
		ByteBuffer exceptionsBuffer = buffer.decodeI16Array(exceptionsCount);
		int[] exceptions = new int[exceptionsCount];

		for (int exceptionIndex = 0; exceptionIndex < exceptionsCount; exceptionIndex++) {
			exceptions[exceptionIndex] = Short.toUnsignedInt(exceptionsBuffer.getShort());
		}
		return new ExceptionsAttribute(decoded, nameIndex, exceptions);
	}

	private static class DecodedClassInfo implements ClassInfo {

		private int minorVersion = -1;
		private int majorVersion = -1;
		private final Map<Integer, Constant> constantPool = new HashMap<>();
		private int accessFlags = -1;
		private final Late<ClassName> thisClassNameHolder = new Late<>();
		private final Late<ClassName> superClassNameHolder = new Late<>();
		private final Late<List<ClassName>> interfacesHolder = new Late<>();
		private final Late<List<Field>> fieldsHolder = new Late<>();
		private final Late<List<Method>> methodsHolder = new Late<>();
		private final Late<List<Attribute>> attributesHolder = new Late<>();

		DecodedClassInfo() {
			// Nothing to do here
		}

		public int setMajorVersion(int majorVersion) {
			this.majorVersion = majorVersion;
			return this.majorVersion;
		}

		@Override
		public int majorVersion() {
			Check.assertTrue(this.majorVersion != -1);

			return this.majorVersion;
		}

		public int setMinorVersion(int minorVersion) {
			this.minorVersion = minorVersion;
			return this.minorVersion;
		}

		@Override
		public int minorVersion() {
			Check.assertTrue(this.minorVersion != -1);

			return this.minorVersion;
		}

		public Constant putConstant(int index, Constant constant) {
			this.constantPool.put(index, constant);
			return constant;
		}

		@Override
		public <T extends Constant> T resolveConstant(int index, Class<T> type) throws IOException {
			Constant constant = this.constantPool.get(index);

			if (constant == null) {
				throw new IOException("Invalid constant index: " + index);
			}
			if (!type.isAssignableFrom(constant.getClass())) {
				throw new IOException("Unexpected constant type: " + type);
			}
			return type.cast(constant);
		}

		public int setAccessFlags(int accessFlags) {
			this.accessFlags = accessFlags;
			return this.accessFlags;
		}

		@Override
		public int accessFlags() {
			Check.assertTrue(this.accessFlags != -1);

			return this.accessFlags;
		}

		public ClassName setThisClass(ClassName name) {
			return this.thisClassNameHolder.set(name);
		}

		@Override
		public ClassName thisClass() {
			return this.thisClassNameHolder.get();
		}

		public ClassName setSuperClass(ClassName name) {
			return this.superClassNameHolder.set(name);
		}

		@Override
		public ClassName superClass() {
			return this.superClassNameHolder.get();
		}

		public List<ClassName> setInterface(List<ClassName> interfaces) {
			return this.interfacesHolder.set(interfaces);
		}

		@Override
		public List<ClassName> interfaces() {
			return this.interfacesHolder.get();
		}

		public List<Field> setFields(List<Field> fields) {
			return this.fieldsHolder.set(fields);
		}

		@Override
		public List<Field> fields() {
			return this.fieldsHolder.get();
		}

		public List<Method> setMethods(List<Method> methods) {
			return this.methodsHolder.set(methods);
		}

		@Override
		public List<Method> methods() {
			return this.methodsHolder.get();
		}

		public List<Attribute> setAttributes(List<Attribute> attributes) {
			return this.attributesHolder.set(attributes);
		}

		@Override
		public List<Attribute> attributes() {
			return this.attributesHolder.get();
		}

	}

}
