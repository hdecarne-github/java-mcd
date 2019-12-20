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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.jvm.decode.AbstractRuntimeAnnotationsAttribute;
import de.carne.mcd.jvm.decode.Attribute;
import de.carne.mcd.jvm.decode.CodeAttribute;
import de.carne.mcd.jvm.decode.ConstantValueAttribute;
import de.carne.mcd.jvm.decode.DeprecatedAttribute;
import de.carne.mcd.jvm.decode.ExceptionsAttribute;
import de.carne.mcd.jvm.decode.SignatureAttribute;
import de.carne.mcd.jvm.decode.SourceFileAttribute;
import de.carne.mcd.jvm.decode.descriptor.Descriptor;
import de.carne.mcd.jvm.decode.descriptor.FieldDescriptor;
import de.carne.mcd.jvm.decode.descriptor.FieldTypeDescriptor;
import de.carne.mcd.jvm.decode.descriptor.MethodDescriptor;
import de.carne.mcd.jvm.util.Attributes;
import de.carne.mcd.jvm.util.ClassUtil;
import de.carne.mcd.jvm.util.PrintSeparator;
import de.carne.util.Strings;

/**
 *
 */
public abstract class ClassPrinter {

	/**
	 * "package"
	 */
	public static final String S_PACKAGE = "package";
	/**
	 * "class"
	 */
	public static final String S_CLASS = "class";
	/**
	 * "interface"
	 */
	public static final String S_INTERFACE = "interface";
	/**
	 * "enum"
	 */
	public static final String S_ENUM = "enum";
	/**
	 * "@interface"
	 */
	public static final String S_ANNOTATION = "@interface";
	/**
	 * "super"
	 */
	public static final String S_SUPER = "super";
	/**
	 * "extends"
	 */
	public static final String S_EXTENDS = "extends";
	/**
	 * "package"
	 */
	public static final String S_IMPLEMENTS = "implements";
	/**
	 * "implements"
	 */
	public static final String S_THROWS = "throws";
	/**
	 * "public"
	 */
	public static final String S_PUBLIC = "public";
	/**
	 * "private"
	 */
	public static final String S_PRIVATE = "private";
	/**
	 * "protected"
	 */
	public static final String S_PROTECTED = "protected";
	/**
	 * "static"
	 */
	public static final String S_STATIC = "static";
	/**
	 * "final"
	 */
	public static final String S_FINAL = "final";
	/**
	 * "volatile"
	 */
	public static final String S_VOLATILE = "volatile";
	/**
	 * "transient"
	 */
	public static final String S_TRANSIENT = "transient";
	/**
	 * "bridge"
	 */
	public static final String S_BRIDGE = "bridge";
	/**
	 * "varargs"
	 */
	public static final String S_VARARGS = "varargs";
	/**
	 * "package"
	 */
	public static final String S_SYNTHETIC = "synthetic";
	/**
	 * "synthetic"
	 */
	public static final String S_SYNCHRONIZED = "synchronized";
	/**
	 * "native"
	 */
	public static final String S_NATIVE = "native";
	/**
	 * "abstract"
	 */
	public static final String S_ABSTRACT = "abstract";
	/**
	 * "byte"
	 */
	public static final String S_BYTE = "byte";
	/**
	 * "char"
	 */
	public static final String S_CHAR = "char";
	/**
	 * "double"
	 */
	public static final String S_DOUBLE = "double";
	/**
	 * "float"
	 */
	public static final String S_FLOAT = "float";
	/**
	 * "int"
	 */
	public static final String S_INT = "int";
	/**
	 * "long"
	 */
	public static final String S_LONG = "long";
	/**
	 * "short"
	 */
	public static final String S_SHORT = "short";
	/**
	 * "boolean"
	 */
	public static final String S_BOOLEAN = "boolean";
	/**
	 * "void"
	 */
	public static final String S_VOID = "void";
	/**
	 * "@Deprecated"
	 */
	public static final String S_DEPRECATED = "@Deprecated";

	protected final MCDOutput out;
	protected final ClassInfo classInfo;
	protected final String classPackage;

	protected ClassPrinter(MCDOutput out, ClassInfo classInfo) {
		this.out = out;
		this.classInfo = classInfo;
		this.classPackage = this.classInfo.thisClass().getPackageName();
	}

	/**
	 * Gets a {@linkplain ClassPrinter} instance suitable for printing the submitted class information.
	 *
	 * @param out the {@linkplain MCDOutput} to print to.
	 * @param classInfo the {@linkplain ClassInfo} to print.
	 * @return a {@linkplain ClassPrinter} instance suitable for the submitted class information.
	 */
	public static ClassPrinter getInstance(MCDOutput out, ClassInfo classInfo) {
		ClassPrinter classPrinter;

		if (ClassUtil.isPackageInfo(classInfo)) {
			classPrinter = new PackageInfoClassPrinter(out, classInfo);
		} else if (ClassUtil.isModuleInfo(classInfo)) {
			classPrinter = new ModuleInfoClassPrinter(out, classInfo);
		} else if (ClassUtil.isInterface(classInfo)) {
			classPrinter = new InterfaceClassPrinter(out, classInfo);
		} else if (ClassUtil.isAnnotation(classInfo)) {
			classPrinter = new AnnotationClassPrinter(out, classInfo);
		} else if (ClassUtil.isEnum(classInfo)) {
			classPrinter = new EnumClassPrinter(out, classInfo);
		} else {
			classPrinter = new DefaultClassPrinter(out, classInfo);
		}
		return classPrinter;
	}

	/**
	 * Prints the class information.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract void print() throws IOException;

	/**
	 * Prints a line break.
	 *
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter println() throws IOException {
		this.out.println();
		return this;
	}

	/**
	 * Prints a standard text.
	 *
	 * @param text the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter print(String text) throws IOException {
		this.out.print(text);
		return this;
	}

	/**
	 * Prints a standard text and a line break.
	 *
	 * @param text the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter println(String text) throws IOException {
		this.out.println(text);
		return this;
	}

	/**
	 * Prints a value text.
	 *
	 * @param value the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printValue(String value) throws IOException {
		this.out.printValue(value);
		return this;
	}

	/**
	 * Prints a value text and a line break.
	 *
	 * @param value the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printlnValue(String value) throws IOException {
		this.out.printlnValue(value);
		return this;
	}

	/**
	 * Prints a comment text.
	 *
	 * @param comment the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printComment(String comment) throws IOException {
		this.out.printComment(comment);
		return this;
	}

	/**
	 * Prints a comment text and a line break.
	 *
	 * @param comment the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printlnComment(String comment) throws IOException {
		this.out.printlnComment(comment);
		return this;
	}

	/**
	 * Prints a keyword text.
	 *
	 * @param keyword the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printKeyword(String keyword) throws IOException {
		this.out.printKeyword(keyword);
		return this;
	}

	/**
	 * Prints a keyword text and a line break.
	 *
	 * @param keyword the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printlnKeyword(String keyword) throws IOException {
		this.out.printlnKeyword(keyword);
		return this;
	}

	/**
	 * Prints an operator text.
	 *
	 * @param operator the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printOperator(String operator) throws IOException {
		this.out.printOperator(operator);
		return this;
	}

	/**
	 * Prints an operator text and a line break.
	 *
	 * @param operator the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printlnOperator(String operator) throws IOException {
		this.out.printlnOperator(operator);
		return this;
	}

	/**
	 * Prints a label text.
	 *
	 * @param label the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printLabel(String label) throws IOException {
		this.out.printLabel(label);
		return this;
	}

	/**
	 * Prints a label text and a line break.
	 *
	 * @param label the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	public ClassPrinter printlnLabel(String label) throws IOException {
		this.out.printlnLabel(label);
		return this;
	}

	protected void printClassComment() throws IOException {
		this.out.printlnComment("/*");

		int major = this.classInfo.majorVersion();
		int minor = this.classInfo.minorVersion();

		this.out.printlnComment(" * Class file version: " + major + "." + minor);

		Optional<SourceFileAttribute> optionalSourceFileAttribute = Attributes
				.resolveOptionalAttribute(this.classInfo.attributes(), SourceFileAttribute.class);

		if (optionalSourceFileAttribute.isPresent()) {
			String sourceFile = optionalSourceFileAttribute.get().getValue();

			this.out.printlnComment(" *");
			this.out.printlnComment(" * Source file: " + sourceFile);
		}
		this.out.printlnComment(" */");
	}

	protected void printClassPackage() throws IOException {
		if (Strings.notEmpty(this.classPackage)) {
			this.out.printKeyword(S_PACKAGE).print(" ").print(this.classPackage).println(";");
		}
	}

	protected void printClassAnnotation() throws IOException {
		printAnnotations(this.classInfo.attributes(), ClassContext.CLASS);
	}

	protected void printAnnotations(List<Attribute> attributes, ClassContext context) throws IOException {
		Attributes.print(Attributes.resolveOptionalAttribute(attributes, DeprecatedAttribute.class), this, context);
		Attributes.print(Attributes.resolveAttributes(attributes, AbstractRuntimeAnnotationsAttribute.class), this,
				context);
	}

	protected void printInterfaceClassSignature() throws IOException {
		Attributes.print(Attributes.resolveOptionalAttribute(this.classInfo.attributes(), SignatureAttribute.class),
				this, ClassContext.CLASS);
		printClassAccessFLagsKeywords();
		printClassAccessFlagsComment();
		this.out.printKeyword(S_INTERFACE);
		this.out.print(" ").print(this.classInfo.thisClass().getEffectiveName(this.classPackage));
		printClassImplements();
	}

	protected void printAnnotationClassSignature() throws IOException {
		Attributes.print(Attributes.resolveOptionalAttribute(this.classInfo.attributes(), SignatureAttribute.class),
				this, ClassContext.CLASS);
		printClassAccessFLagsKeywords();
		printClassAccessFlagsComment();
		this.out.printKeyword(S_ANNOTATION);
		this.out.print(" ").print(this.classInfo.thisClass().getEffectiveName(this.classPackage));
		printClassImplements();
	}

	protected void printEnumClassSignature() throws IOException {
		Attributes.print(Attributes.resolveOptionalAttribute(this.classInfo.attributes(), SignatureAttribute.class),
				this, ClassContext.CLASS);
		printClassAccessFLagsKeywords();
		printClassAccessFlagsComment();
		this.out.printKeyword(S_ENUM);
		this.out.print(" ").print(this.classInfo.thisClass().getEffectiveName(this.classPackage));
		printClassExtends();
		printClassImplements();
	}

	protected void printDefaultClassSignature() throws IOException {
		Attributes.print(Attributes.resolveOptionalAttribute(this.classInfo.attributes(), SignatureAttribute.class),
				this, ClassContext.CLASS);
		printClassAccessFLagsKeywords();
		printClassAccessFlagsComment();
		this.out.printKeyword(S_CLASS);
		this.out.print(" ").print(this.classInfo.thisClass().getEffectiveName(this.classPackage));
		printClassExtends();
		printClassImplements();
	}

	private void printClassExtends() throws IOException {
		ClassName superClass = this.classInfo.superClass();

		if (!superClass.isObject()) {
			this.out.print(" ").printKeyword(S_EXTENDS).print(" ");
			this.out.print(superClass.getEffectiveName(this.classPackage));
		}
	}

	private void printClassImplements() throws IOException {
		List<ClassName> interfaces = this.classInfo.interfaces();

		if (!interfaces.isEmpty()) {
			boolean interfaceClass = ClassUtil.isInterface(this.classInfo.accessFlags());

			this.out.print(" ").printKeyword(interfaceClass ? S_EXTENDS : S_IMPLEMENTS).print(" ");

			PrintSeparator interfaceSeparator = new PrintSeparator();

			for (ClassName interfaceName : interfaces) {
				interfaceSeparator.print(this, ClassContext.CLASS);
				this.out.print(interfaceName.getEffectiveName(this.classPackage));
			}
		}
	}

	private static final Map<Integer, String> CLASS_ACCESS_FLAGS_KEYWORDS = new LinkedHashMap<>();

	static {
		CLASS_ACCESS_FLAGS_KEYWORDS.put(0x0001, S_PUBLIC);
		CLASS_ACCESS_FLAGS_KEYWORDS.put(0x0010, S_FINAL);
		CLASS_ACCESS_FLAGS_KEYWORDS.put(0x0400, S_ABSTRACT);
	}

	private void printClassAccessFLagsKeywords() throws IOException {
		int maskedAccessFlags = this.classInfo.accessFlags();

		if (ClassUtil.isInterface(maskedAccessFlags) || ClassUtil.isAnnotation(maskedAccessFlags)) {
			maskedAccessFlags &= ~0x0400;
		}
		printAccessFlagsKeywords(CLASS_ACCESS_FLAGS_KEYWORDS, maskedAccessFlags);
	}

	private static final Map<Integer, String> CLASS_ACCESS_FLAGS_COMMENTS = new LinkedHashMap<>();

	static {
		CLASS_ACCESS_FLAGS_COMMENTS.put(0x0020, S_SUPER);
		CLASS_ACCESS_FLAGS_COMMENTS.put(0x1000, S_SYNTHETIC);
	}

	private void printClassAccessFlagsComment() throws IOException {
		printAccessFlagsComment(CLASS_ACCESS_FLAGS_COMMENTS, this.classInfo.accessFlags());
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

	private void printFieldAccessFLagsKeywords(int accessFlags) throws IOException {
		printAccessFlagsKeywords(FIELD_ACCESS_FLAGS_KEYWORDS, accessFlags);
	}

	private static final Map<Integer, String> FIELD_ACCESS_FLAGS_COMMENTS = new LinkedHashMap<>();

	static {
		FIELD_ACCESS_FLAGS_COMMENTS.put(0x1000, S_SYNTHETIC);
		FIELD_ACCESS_FLAGS_COMMENTS.put(0x4000, S_ENUM);
	}

	private void printFieldAccessFLagsComment(int accessFlags) throws IOException {
		printAccessFlagsComment(FIELD_ACCESS_FLAGS_COMMENTS, accessFlags);
	}

	private static final Map<Integer, String> METHOD_ACCESS_FLAGS_KEYWORDS = new LinkedHashMap<>();

	static {
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0001, S_PUBLIC);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0002, S_PRIVATE);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0004, S_PROTECTED);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0008, S_STATIC);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0010, S_FINAL);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0020, S_SYNCHRONIZED);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0100, S_NATIVE);
		METHOD_ACCESS_FLAGS_KEYWORDS.put(0x0400, S_ABSTRACT);
	}

	private void printMethodAccessFLagsKeywords(int accessFlags) throws IOException {
		int maskedAccessFlags = accessFlags;
		int classAccessFlags = this.classInfo.accessFlags();

		if (ClassUtil.isInterface(classAccessFlags)) {
			maskedAccessFlags &= ~0x0400;
		}
		printAccessFlagsKeywords(METHOD_ACCESS_FLAGS_KEYWORDS, maskedAccessFlags);
	}

	private static final Map<Integer, String> METHOD_ACCESS_FLAGS_COMMENTS = new LinkedHashMap<>();

	static {
		METHOD_ACCESS_FLAGS_COMMENTS.put(0x0040, S_BRIDGE);
		METHOD_ACCESS_FLAGS_COMMENTS.put(0x0080, S_VARARGS);
		METHOD_ACCESS_FLAGS_COMMENTS.put(0x1000, S_SYNTHETIC);
	}

	private void printMethodAccessFLagsComment(int accessFlags) throws IOException {
		printAccessFlagsComment(METHOD_ACCESS_FLAGS_COMMENTS, accessFlags);
	}

	private void printAccessFlagsComment(Map<Integer, String> comments, int flags) throws IOException {
		StringBuilder buffer = new StringBuilder();

		for (Map.Entry<Integer, String> commentEntry : comments.entrySet()) {
			int flag = commentEntry.getKey().intValue();
			String keyword = commentEntry.getValue();

			if ((flags & flag) == flag) {
				buffer.append(buffer.length() == 0 ? "/* " : "|").append(keyword);
			}
		}
		if (buffer.length() > 0) {
			buffer.append(" */");
			this.out.printComment(buffer.toString()).print(" ");
		}
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

	protected void printFields() throws IOException {
		this.out.increaseIndent();
		for (FieldInfo field : this.classInfo.fields()) {
			printField(field);
		}
		this.out.decreaseIndent();
	}

	private void printField(FieldInfo field) throws IOException {
		this.out.println();
		printAnnotations(field.attributes(), ClassContext.FIELD);
		printFieldAccessFLagsKeywords(field.accessFlags());
		printFieldAccessFLagsComment(field.accessFlags());

		FieldDescriptor descriptor = Descriptor.decodeFieldDescriptor(field.descriptor(), this.classPackage);

		descriptor.print(this, ClassContext.FIELD);
		this.out.print(" ").print(field.name());

		Optional<ConstantValueAttribute> optionalValue = Attributes.resolveOptionalAttribute(field.attributes(),
				ConstantValueAttribute.class);

		if (optionalValue.isPresent()) {
			this.out.print(" ").printOperator("=").print(" ");
			optionalValue.get().print(this, ClassContext.FIELD);
		}

		this.out.println(";");
	}

	protected void printMethods() throws IOException {
		this.out.increaseIndent();
		for (MethodInfo method : this.classInfo.methods()) {
			printMethod(method);
		}
		this.out.decreaseIndent();
	}

	private void printMethod(MethodInfo method) throws IOException {
		this.out.println();
		printAnnotations(method.attributes(), ClassContext.METHOD);
		Attributes.print(Attributes.resolveOptionalAttribute(method.attributes(), SignatureAttribute.class), this,
				ClassContext.CLASS);
		printMethodAccessFLagsKeywords(method.accessFlags());
		printMethodAccessFLagsComment(method.accessFlags());

		MethodDescriptor descriptor = Descriptor.decodeMethodDescriptor(method.descriptor(), this.classPackage);

		descriptor.getReturnType().print(this, ClassContext.METHOD);
		this.out.print(" ").print(method.name()).print("(");

		PrintSeparator parameterSeparator = new PrintSeparator();

		for (FieldTypeDescriptor parameterType : descriptor.getParameterTypes()) {
			parameterSeparator.print(this, ClassContext.METHOD);
			parameterType.print(this, ClassContext.METHOD);
		}
		this.out.print(")");
		Attributes.print(Attributes.resolveOptionalAttribute(method.attributes(), ExceptionsAttribute.class), this,
				ClassContext.METHOD);

		Optional<CodeAttribute> optionalCode = Attributes.resolveOptionalAttribute(method.attributes(),
				CodeAttribute.class);

		if (optionalCode.isPresent()) {
			this.out.println(" {");
			this.out.println("}");
		} else {
			this.out.println(";");
		}
	}

	private static class PackageInfoClassPrinter extends ClassPrinter {

		PackageInfoClassPrinter(MCDOutput out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassAnnotation();
			printClassPackage();
		}

	}

	private static class ModuleInfoClassPrinter extends ClassPrinter {

		ModuleInfoClassPrinter(MCDOutput out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassAnnotation();
			printClassPackage();
		}

	}

	private static class InterfaceClassPrinter extends ClassPrinter {

		InterfaceClassPrinter(MCDOutput out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printInterfaceClassSignature();
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

	private static class AnnotationClassPrinter extends ClassPrinter {

		AnnotationClassPrinter(MCDOutput out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printAnnotationClassSignature();
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

	private static class EnumClassPrinter extends ClassPrinter {

		EnumClassPrinter(MCDOutput out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printEnumClassSignature();
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

	private static class DefaultClassPrinter extends ClassPrinter {

		DefaultClassPrinter(MCDOutput out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printDefaultClassSignature();
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

}
