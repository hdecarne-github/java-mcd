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
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import de.carne.boot.check.Check;
import de.carne.mcd.common.MCDOutputChannel;
import de.carne.util.Strings;

abstract class ClassPrinter {

	protected static final String S_PACKAGE = "package";
	protected static final String S_CLASS = "class";
	protected static final String S_INTERFACE = "interface";
	protected static final String S_ENUM = "enum";
	protected static final String S_EXTENDS = "extends";
	protected static final String S_IMPLEMENTS = "implements";
	protected static final String S_PUBLIC = "public";
	protected static final String S_PRIVATE = "private";
	protected static final String S_PROTECTED = "protected";
	protected static final String S_STATIC = "static";
	protected static final String S_FINAL = "final";
	protected static final String S_VOLATILE = "volatile";
	protected static final String S_TRANSIENT = "transient";
	protected static final String S_SYNCHRONIZED = "synchronized";
	protected static final String S_NATIVE = "native";
	protected static final String S_ABSTRACT = "abstract";
	protected static final String S_BYTE = "byte";
	protected static final String S_CHAR = "char";
	protected static final String S_DOUBLE = "double";
	protected static final String S_FLOAT = "float";
	protected static final String S_INT = "int";
	protected static final String S_LONG = "long";
	protected static final String S_SHORT = "short";
	protected static final String S_BOOLEAN = "boolean";
	protected static final String S_VOID = "void";

	protected final MCDOutputChannel out;
	protected final ClassInfo classInfo;
	protected final String classPackage;
	private String ident = "";

	protected ClassPrinter(MCDOutputChannel out, ClassInfo classInfo) {
		this.out = out;
		this.classInfo = classInfo;
		this.classPackage = this.classInfo.thisClass().getPackage();
	}

	public abstract void print() throws IOException;

	public void increaseIdent() {
		this.ident += "    ";
	}

	public void decreaseIdent() {
		int identLevel = this.ident.length();

		Check.assertTrue(identLevel > 0);

		this.ident = this.ident.substring(0, identLevel - 4);
	}

	public void printIdent() throws IOException {
		this.out.print(this.ident);
	}

	public void printlnClassComment() throws IOException {
		this.out.printlnComment("/*");
		this.out.printlnComment(
				" * Byte code version: " + this.classInfo.majorVersion() + "." + this.classInfo.minorVersion());

		Optional<SourceFileAttribute> sourceFile = Attributes.resolveOptionalAttribute(this.classInfo.attributes(),
				SourceFileAttribute.class);

		if (sourceFile.isPresent()) {
			this.out.printlnComment(" *");
			this.out.printlnComment(" * Source file:" + sourceFile.get().getValue());
		}
		this.out.printlnComment(" */");
	}

	public void printlnClassPackage() throws IOException {
		if (Strings.notEmpty(this.classPackage)) {
			this.out.printKeyword(S_PACKAGE).print(" ").print(this.classPackage).println(";").println();
		}
	}

	public void printlnClassAnnotations() throws IOException {
		printlnAnnotations(this.classInfo.attributes());
	}

	public void printlnAnnotations(List<Attribute> attributes) throws IOException {
		List<AbstractRuntimeAnnotationsAttribute> annotationAttributes = Attributes.resolveAttributes(attributes,
				AbstractRuntimeAnnotationsAttribute.class);

		for (AbstractRuntimeAnnotationsAttribute annotationAttribute : annotationAttributes) {
			List<Annotation> annotations = annotationAttribute.annotations();

			for (Annotation annotation : annotations) {
				annotation.print(this);
			}
		}
	}

	public void printlnAnnotation(String typeName, List<AnnotationElement> elements) throws IOException {
		printIdent();
		this.out.printLabel("@");
		this.out.printLabel(decodeTypeDescriptor(typeName, this.classPackage));

		int elementsSize = elements.size();

		if (elementsSize > 0) {
			this.out.print("(");

			boolean firstElement = true;

			for (AnnotationElement element : elements) {
				if (!firstElement) {
					this.out.print(", ");
				}
				firstElement = false;
				element.print(this);
			}
			this.out.print(")");
		}
		this.out.println();
	}

	public void printAnnotationElement(String elementName, AnnotationElementValue value) throws IOException {
		this.out.print(elementName);
		this.out.print(" ");
		this.out.printOperator("=");
		this.out.print(" ");
		value.print(this);
	}

	public void printConstantValueAnnotationElement(String value) throws IOException {
		this.out.printValue(value);
	}

	public void printArrayAnnotationElement(List<AnnotationElementValue> elementValues) throws IOException {
		int elementValuesSize = elementValues.size();

		if (elementValuesSize > 1) {
			this.out.print("{ ");
		}

		boolean firstElement = true;

		for (AnnotationElementValue elementValue : elementValues) {
			if (!firstElement) {
				this.out.print(", ");
			}
			firstElement = false;
			elementValue.print(this);
		}
		if (elementValuesSize > 1) {
			this.out.print(" }");
		}
	}

	public void printClassAnnotationElement(String typeName) throws IOException {
		this.out.print(decodeTypeDescriptor(typeName, this.classPackage) + ".class");
	}

	public void printEnumAnnotationElement(String typeName, String value) throws IOException {
		this.out.print(decodeTypeDescriptor(typeName, this.classPackage) + "." + value);
	}

	private static final Map<Integer, String> CLASS_ACCESS_FLAGS_SYMS = new LinkedHashMap<>();

	static {
		CLASS_ACCESS_FLAGS_SYMS.put(0x0001, "ACC_PUBLIC");
		CLASS_ACCESS_FLAGS_SYMS.put(0x0010, "ACC_FINAL");
		CLASS_ACCESS_FLAGS_SYMS.put(0x0020, "ACC_SUPER");
		CLASS_ACCESS_FLAGS_SYMS.put(0x0200, "ACC_INTERFACE");
		CLASS_ACCESS_FLAGS_SYMS.put(0x0400, "ACC_ABSTRACT");
		CLASS_ACCESS_FLAGS_SYMS.put(0x1000, "ACC_SYNTHETIC");
		CLASS_ACCESS_FLAGS_SYMS.put(0x2000, "ACC_ANNOTATION");
		CLASS_ACCESS_FLAGS_SYMS.put(0x4000, "ACC_ENUM");
		CLASS_ACCESS_FLAGS_SYMS.put(0x8000, "ACC_MODULE");
	}

	public void printClassAccessFlagsComment() throws IOException {
		this.out.printComment("/* " + formatAccessFlags(CLASS_ACCESS_FLAGS_SYMS, this.classInfo.accessFlags()) + " */");
	}

	private static final Map<Integer, String> CLASS_ACCESS_FLAGS_KEYWORDS = new LinkedHashMap<>();

	static {
		CLASS_ACCESS_FLAGS_KEYWORDS.put(0x0001, S_PUBLIC);
		CLASS_ACCESS_FLAGS_KEYWORDS.put(0x0010, S_FINAL);
		CLASS_ACCESS_FLAGS_KEYWORDS.put(0x0400, S_ABSTRACT);
	}

	public void printClassAccessFLagsKeywords() throws IOException {
		printAccessFlagsKeywords(CLASS_ACCESS_FLAGS_KEYWORDS, this.classInfo.accessFlags());
	}

	private static final Map<Integer, String> FIELD_ACCESS_FLAGS_KEYWORDS = new LinkedHashMap<>();

	static {
		FIELD_ACCESS_FLAGS_KEYWORDS.put(0x0001, S_PUBLIC);
		FIELD_ACCESS_FLAGS_KEYWORDS.put(0x0002, S_PRIVATE);
		FIELD_ACCESS_FLAGS_KEYWORDS.put(0x0004, S_PROTECTED);
		FIELD_ACCESS_FLAGS_KEYWORDS.put(0x0008, S_STATIC);
		FIELD_ACCESS_FLAGS_KEYWORDS.put(0x0010, S_FINAL);
		FIELD_ACCESS_FLAGS_KEYWORDS.put(0x0040, S_VOLATILE);
		FIELD_ACCESS_FLAGS_KEYWORDS.put(0x0080, S_TRANSIENT);
	}

	public void printFieldAccessFLagsKeywords(int accessFlags) throws IOException {
		printAccessFlagsKeywords(FIELD_ACCESS_FLAGS_KEYWORDS, accessFlags);
	}

	private static final Map<Integer, String> METHOD_ACCESS_FLAGS_KEYWORDS = new LinkedHashMap<>();

	static {
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0001, S_PUBLIC);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0002, S_PRIVATE);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0004, S_PROTECTED);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0008, S_PRIVATE);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0010, S_FINAL);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0020, S_SYNCHRONIZED);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0100, S_NATIVE);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0400, S_ABSTRACT);
	}

	public void printMethodAccessFLagsKeywords(int accessFlags) throws IOException {
		printAccessFlagsKeywords(METHOD_ACCESS_FLAGS_KEYWORDS, accessFlags);
	}

	private void printAccessFlagsKeywords(Map<Integer, String> keywords, int flags) throws IOException {
		for (Map.Entry<Integer, String> keywordEntry : keywords.entrySet()) {
			int flag = keywordEntry.getKey().intValue();
			String keyword = keywordEntry.getValue();

			if ((flags & flag) == flag) {
				this.out.printKeyword(keyword).print(" ");
			}
		}
	}

	public void printlnFields() throws IOException {
		increaseIdent();
		for (Field field : this.classInfo.fields()) {
			field.print(this);
		}
		decreaseIdent();
	}

	public void printlnField(int accessFlags, String descriptor, String name, List<Attribute> attributes)
			throws IOException {
		this.out.println();
		printlnAnnotations(attributes);
		printIdent();
		printFieldAccessFLagsKeywords(accessFlags);
		this.out.print(decodeTypeDescriptor(descriptor, this.classPackage)).print(" ").print(name);

		Optional<ConstantValueAttribute> valueHolder = Attributes.resolveOptionalAttribute(attributes,
				ConstantValueAttribute.class);

		if (valueHolder.isPresent()) {
			ConstantValueAttribute value = valueHolder.get();

			this.out.print(" ");
			this.out.printOperator("=");
			this.out.print(" ");
			value.print(this);
		}
		this.out.println(";");
	}

	public void printlnMethods() throws IOException {
		increaseIdent();
		for (Method method : this.classInfo.methods()) {
			method.print(this);
		}
		decreaseIdent();
	}

	public void printlnMethod(int accessFlags, String descriptor, String name, List<Attribute> attributes)
			throws IOException {
		this.out.println();
		printlnAnnotations(attributes);
		printIdent();
		printMethodAccessFLagsKeywords(accessFlags);

		Deque<String> signature = decodeDescriptor(descriptor, this.classPackage);
		String returnType = signature.removeLast();

		this.out.print(returnType).print(" ");
		this.out.print(name).print("(");

		boolean first = true;

		for (String argumentType : signature) {
			if (!first) {
				this.out.print(", ");
			} else {
				first = false;
			}
			this.out.print(argumentType);
		}
		this.out.print(")");
		this.out.println(";");
	}

	public void printValue(String value) throws IOException {
		this.out.printValue(value);
	}

	private String formatAccessFlags(Map<Integer, String> syms, int flags) {
		StringBuilder buffer = new StringBuilder();

		for (int flag = 0x0001; flag < 0x10000; flag <<= 1) {
			if ((flags & flag) == flag) {
				if (buffer.length() > 0) {
					buffer.append("|");
				}

				String flagSym = syms.get(flag);

				buffer.append(flagSym != null ? flagSym : Integer.toHexString(flag));
			}
		}
		return buffer.toString();
	}

	private String decodeTypeDescriptor(String descriptor, String packageName) throws IOException {
		Deque<String> decoded = decodeDescriptor(descriptor, packageName);

		if (decoded.size() != 1) {
			throw new IOException("Unexpected field descriptor: " + descriptor);
		}
		return decoded.getFirst();
	}

	private Deque<String> decodeDescriptor(String descriptor, String packageName) throws IOException {
		Deque<String> decoded = new LinkedList<>();
		int descriptorLength = descriptor.length();
		int position = 0;

		while (position < descriptorLength) {
			position = decodeDescriptorHelper(decoded, descriptor, position, packageName);
		}
		return decoded;
	}

	private static final Map<Character, String> DESCRIPTOR_BASE_TYPES = new HashMap<>();

	static {
		DESCRIPTOR_BASE_TYPES.put('B', S_BYTE);
		DESCRIPTOR_BASE_TYPES.put('C', S_CHAR);
		DESCRIPTOR_BASE_TYPES.put('D', S_DOUBLE);
		DESCRIPTOR_BASE_TYPES.put('F', S_FLOAT);
		DESCRIPTOR_BASE_TYPES.put('I', S_INT);
		DESCRIPTOR_BASE_TYPES.put('J', S_LONG);
		DESCRIPTOR_BASE_TYPES.put('S', S_SHORT);
		DESCRIPTOR_BASE_TYPES.put('V', S_VOID);
		DESCRIPTOR_BASE_TYPES.put('Z', S_BOOLEAN);
	}

	private int decodeDescriptorHelper(Deque<String> decoded, String descriptor, int position, String packageName)
			throws IOException {
		int nextPosition = position;
		char c = descriptor.charAt(position);
		ClassName decodedClassName;

		switch (c) {
		case '(':
		case ')':
			nextPosition += 1;
			break;
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'V':
		case 'Z':
			decoded.add(Objects.requireNonNull(DESCRIPTOR_BASE_TYPES.get(c)));
			nextPosition += 1;
			break;
		case 'L':
			decodedClassName = ClassName.fromDescriptor(descriptor, nextPosition + 1);
			decoded.add(decodedClassName.getName(packageName));
			nextPosition += decodedClassName.getName().length() + 2;
			break;
		case '[':
			nextPosition = decodeDescriptorHelper(decoded, descriptor, nextPosition + 1, packageName);
			decoded.add(decoded.removeLast() + "[]");
			break;
		default:
			throw new IOException("Unexpected descriptor: " + descriptor + "(" + position + ")");
		}
		return nextPosition;
	}

}
