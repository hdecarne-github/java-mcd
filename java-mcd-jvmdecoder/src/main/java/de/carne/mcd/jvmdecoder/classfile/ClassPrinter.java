/*
 * Copyright (c) 2019-2022 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.jvmdecoder.classfile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.carne.mcd.io.MCDOutputBuffer;
import de.carne.mcd.jvmdecoder.classfile.attribute.Attribute;
import de.carne.mcd.jvmdecoder.classfile.attribute.Attributes;
import de.carne.mcd.jvmdecoder.classfile.attribute.CodeAttribute;
import de.carne.mcd.jvmdecoder.classfile.attribute.ConstantValueAttribute;
import de.carne.mcd.jvmdecoder.classfile.attribute.ExceptionsAttribute;
import de.carne.mcd.jvmdecoder.classfile.attribute.ModuleAttribute;
import de.carne.mcd.jvmdecoder.classfile.attribute.RuntimeAnnotationsAttribute;
import de.carne.mcd.jvmdecoder.classfile.attribute.SignatureAttribute;
import de.carne.mcd.jvmdecoder.classfile.attribute.SourceFileAttribute;
import de.carne.mcd.jvmdecoder.classfile.decl.DeclDecoder;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedClassSignature;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedFieldDescriptor;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedFieldSignature;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedMethodDescriptor;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedMethodSignature;
import de.carne.util.Strings;

/**
 * Class file printing code responsible for presenting all kind decoded class information.
 */
public abstract class ClassPrinter {

	/**
	 * "module"
	 */
	public static final String S_MODULE = "module";
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
	 * "requires"
	 */
	public static final String S_REQUIRES = "requires";
	/**
	 * "exports"
	 */
	public static final String S_EXPORTS = "exports";
	/**
	 * "opens"
	 */
	public static final String S_OPENS = "opens";
	/**
	 * "to"
	 */
	public static final String S_TO = "to";
	/**
	 * "uses"
	 */
	public static final String S_USES = "uses";
	/**
	 * "provides"
	 */
	public static final String S_PROVIDES = "provides";
	/**
	 * "with"
	 */
	public static final String S_WITH = "with";
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
	 * "synthetic"
	 */
	public static final String S_SYNTHETIC = "synthetic";
	/**
	 * "transitive"
	 */
	public static final String S_TRANSITIVE = "transitive";
	/**
	 * "open"
	 */
	public static final String S_OPEN = "open";
	/**
	 * "mandated"
	 */
	public static final String S_MANDATED = "mandated";
	/**
	 * "synchronized"
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

	protected final MCDOutputBuffer out;
	protected final ClassInfo classInfo;
	protected final String classPackage;

	protected ClassPrinter(MCDOutputBuffer out, ClassInfo classInfo) {
		this.out = out;
		this.classInfo = classInfo;
		this.classPackage = this.classInfo.thisClass().getPackageName();
	}

	/**
	 * Gets a {@linkplain ClassPrinter} instance suitable for printing the submitted class information.
	 *
	 * @param out the {@linkplain MCDOutputBuffer} to print to.
	 * @param classInfo the {@linkplain ClassInfo} to print.
	 * @return a {@linkplain ClassPrinter} instance suitable for the submitted class information.
	 */
	public static ClassPrinter getInstance(MCDOutputBuffer out, ClassInfo classInfo) {
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
	 * Gets the underlying {@linkplain MCDOutputBuffer} instance.
	 *
	 * @return the underlying {@linkplain MCDOutputBuffer} instance.
	 */
	public MCDOutputBuffer output() {
		return this.out;
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

	/**
	 * Prints flag symbols as comment.
	 *
	 * @param flagSymbols the flag symbols to use.
	 * @param flags the flags to print.
	 * @throws IOException if an I/O error occurs.
	 */
	public void printFlagsComment(Map<Integer, String> flagSymbols, int flags) throws IOException {
		StringBuilder buffer = new StringBuilder();

		for (Map.Entry<Integer, String> commentEntry : flagSymbols.entrySet()) {
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

	/**
	 * Prints flag symbols as keywords.
	 *
	 * @param flagSymbols the flag symbols to use.
	 * @param flags the flags to print.
	 * @throws IOException if an I/O error occurs.
	 */
	public void printFlagsKeyword(Map<Integer, String> flagSymbols, int flags) throws IOException {
		for (Map.Entry<Integer, String> keywordEntry : flagSymbols.entrySet()) {
			int flag = keywordEntry.getKey().intValue();
			String keyword = keywordEntry.getValue();

			if ((flags & flag) == flag) {
				this.out.printKeyword(keyword).print(" ");
			}
		}
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
		Attributes.print(Attributes.resolveAttributes(attributes, RuntimeAnnotationsAttribute.class), this, context);
	}

	protected void printModuleInfo() throws IOException {
		Attributes.resolveUniqueAttribute(this.classInfo.attributes(), ModuleAttribute.class).print(this,
				ClassContext.CLASS);
	}

	protected void printClassSignature(String classKeyword) throws IOException {
		printClassAccessFlagsKeywords();
		printClassAccessFlagsComment();
		this.out.printKeyword(classKeyword).print(" ")
				.print(this.classInfo.thisClass().getEffectiveName(this.classPackage));

		Optional<SignatureAttribute> signature = Attributes.resolveOptionalAttribute(this.classInfo.attributes(),
				SignatureAttribute.class);

		if (signature.isPresent()) {
			DecodedClassSignature decodedSignature = DeclDecoder.decodeClassSignature(signature.get().getValue(),
					this.classInfo.thisClass().getPackageName());

			printTypeParameters(decodedSignature.typeParameters(), ClassContext.CLASS);
			printClassExtends(decodedSignature.superClass());
			printClassImplements(decodedSignature.superInterfaces());
		} else {
			printClassExtends();
			printClassImplements();
		}
	}

	private void printTypeParameters(List<PrintBuffer> typeParameters, ClassContext context) throws IOException {
		if (!typeParameters.isEmpty()) {
			PrintSeparator separator = new PrintSeparator();

			this.out.print("<");
			for (PrintBuffer typeParameter : typeParameters) {
				separator.print(this, context);
				typeParameter.print(this, context);
			}
			this.out.print(">");
		}
	}

	private void printClassExtends(PrintBuffer superClass) throws IOException {
		if (!superClass.isEmpty()) {
			this.out.print(" ").printKeyword(S_EXTENDS).print(" ");
			superClass.print(this, ClassContext.CLASS);
		}
	}

	private void printClassExtends() throws IOException {
		ClassName superClass = this.classInfo.superClass();

		if (!superClass.isObject()) {
			this.out.print(" ").printKeyword(S_EXTENDS).print(" ");
			this.out.print(superClass.getEffectiveName(this.classPackage));
		}
	}

	private void printClassImplements(List<PrintBuffer> superInterfaces) throws IOException {
		if (!superInterfaces.isEmpty()) {
			boolean interfaceClass = ClassUtil.isInterface(this.classInfo.accessFlags());

			this.out.print(" ").printKeyword(interfaceClass ? S_EXTENDS : S_IMPLEMENTS).print(" ");

			PrintSeparator separator = new PrintSeparator();

			for (PrintBuffer superInterface : superInterfaces) {
				separator.print(this, ClassContext.CLASS);
				superInterface.print(this, ClassContext.CLASS);
			}
		}
	}

	private void printClassImplements() throws IOException {
		List<ClassName> interfaces = this.classInfo.interfaces();

		if (!interfaces.isEmpty()) {
			boolean interfaceClass = ClassUtil.isInterface(this.classInfo.accessFlags());

			this.out.print(" ").printKeyword(interfaceClass ? S_EXTENDS : S_IMPLEMENTS).print(" ");

			PrintSeparator separator = new PrintSeparator();

			for (ClassName interfaceName : interfaces) {
				separator.print(this, ClassContext.CLASS);
				this.out.print(interfaceName.getEffectiveName(this.classPackage));
			}
		}
	}

	private static final Map<Integer, String> CLASS_ACCESS_FLAG_KEYWORD_SYMBOLS = new LinkedHashMap<>();

	static {
		CLASS_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0001, S_PUBLIC);
		CLASS_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0010, S_FINAL);
		CLASS_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0400, S_ABSTRACT);
	}

	private void printClassAccessFlagsKeywords() throws IOException {
		int maskedAccessFlags = this.classInfo.accessFlags();

		if (ClassUtil.isInterface(maskedAccessFlags) || ClassUtil.isAnnotation(maskedAccessFlags)) {
			maskedAccessFlags &= ~0x0400;
		}
		printFlagsKeyword(CLASS_ACCESS_FLAG_KEYWORD_SYMBOLS, maskedAccessFlags);
	}

	private static final Map<Integer, String> CLASS_ACCESS_FLAG_COMMENT_SYMBOLS = new LinkedHashMap<>();

	static {
		CLASS_ACCESS_FLAG_COMMENT_SYMBOLS.put(0x0020, S_SUPER);
		CLASS_ACCESS_FLAG_COMMENT_SYMBOLS.put(0x1000, S_SYNTHETIC);
	}

	private void printClassAccessFlagsComment() throws IOException {
		printFlagsComment(CLASS_ACCESS_FLAG_COMMENT_SYMBOLS, this.classInfo.accessFlags());
	}

	private static final Map<Integer, String> FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS = new LinkedHashMap<>();

	static {
		FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0001, S_PUBLIC);
		FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0002, S_PRIVATE);
		FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0004, S_PROTECTED);
		FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0008, S_STATIC);
		FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0010, S_FINAL);
		FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0040, S_VOLATILE);
		FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0080, S_TRANSIENT);
	}

	private void printFieldAccessFLagsKeywords(int accessFlags) throws IOException {
		printFlagsKeyword(FIELD_ACCESS_FLAG_KEYWORD_SYMBOLS, accessFlags);
	}

	private static final Map<Integer, String> FIELD_ACCESS_FLAG_COMMENT_SYMBOLS = new LinkedHashMap<>();

	static {
		FIELD_ACCESS_FLAG_COMMENT_SYMBOLS.put(0x1000, S_SYNTHETIC);
		FIELD_ACCESS_FLAG_COMMENT_SYMBOLS.put(0x4000, S_ENUM);
	}

	private void printFieldAccessFlagsComment(int accessFlags) throws IOException {
		printFlagsComment(FIELD_ACCESS_FLAG_COMMENT_SYMBOLS, accessFlags);
	}

	private static final Map<Integer, String> METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS = new LinkedHashMap<>();

	static {
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0001, S_PUBLIC);
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0002, S_PRIVATE);
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0004, S_PROTECTED);
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0008, S_STATIC);
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0010, S_FINAL);
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0020, S_SYNCHRONIZED);
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0100, S_NATIVE);
		METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS.put(0x0400, S_ABSTRACT);
	}

	private void printMethodAccessFLagsKeywords(int accessFlags) throws IOException {
		int maskedAccessFlags = accessFlags;
		int classAccessFlags = this.classInfo.accessFlags();

		if (ClassUtil.isInterface(classAccessFlags)) {
			maskedAccessFlags &= ~0x0400;
		}
		printFlagsKeyword(METHOD_ACCESS_FLAG_KEYWORD_SYMBOLS, maskedAccessFlags);
	}

	private static final Map<Integer, String> METHOD_ACCESS_FLAG_COMMENT_SYMBOLS = new LinkedHashMap<>();

	static {
		METHOD_ACCESS_FLAG_COMMENT_SYMBOLS.put(0x0040, S_BRIDGE);
		METHOD_ACCESS_FLAG_COMMENT_SYMBOLS.put(0x0080, S_VARARGS);
		METHOD_ACCESS_FLAG_COMMENT_SYMBOLS.put(0x1000, S_SYNTHETIC);
	}

	private void printMethodAccessFLagsComment(int accessFlags) throws IOException {
		printFlagsComment(METHOD_ACCESS_FLAG_COMMENT_SYMBOLS, accessFlags);
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
		printFieldAccessFlagsComment(field.accessFlags());
		Optional<SignatureAttribute> signature = Attributes.resolveOptionalAttribute(field.attributes(),
				SignatureAttribute.class);

		if (signature.isPresent()) {
			DecodedFieldSignature decodedSignature = DeclDecoder.decodeFieldSignature(signature.get().getValue(),
					this.classPackage);

			decodedSignature.type().print(this, ClassContext.FIELD);
		} else {
			DecodedFieldDescriptor decodedDescriptor = DeclDecoder.decodeFieldDescriptor(field.descriptor(),
					this.classPackage);

			decodedDescriptor.type().print(this, ClassContext.FIELD);
		}
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
		printMethodAccessFLagsKeywords(method.accessFlags());
		printMethodAccessFLagsComment(method.accessFlags());

		Optional<SignatureAttribute> signature = Attributes.resolveOptionalAttribute(method.attributes(),
				SignatureAttribute.class);

		if (signature.isPresent()) {
			DecodedMethodSignature decodedSignature = DeclDecoder.decodeMethodSignature(signature.get().getValue(),
					this.classPackage);

			decodedSignature.returnType().print(this, ClassContext.METHOD);
			this.out.print(" ").print(method.name());
			printTypeParameters(decodedSignature.typeParameters(), ClassContext.METHOD);
			this.out.print("(");

			PrintSeparator separator = new PrintSeparator();

			for (PrintBuffer parameterType : decodedSignature.parameterTypes()) {
				separator.print(this, ClassContext.METHOD);
				parameterType.print(this, ClassContext.METHOD);
			}
			this.out.print(")");

			List<PrintBuffer> throwsTypes = decodedSignature.throwsTypes();

			if (!throwsTypes.isEmpty()) {
				this.out.print(" ").printKeyword(S_THROWS).print(" ");
				separator.reset();
				for (PrintBuffer throwsType : throwsTypes) {
					separator.print(this, ClassContext.METHOD);
					throwsType.print(this, ClassContext.METHOD);
				}
			} else {
				Attributes.print(Attributes.resolveOptionalAttribute(method.attributes(), ExceptionsAttribute.class),
						this, ClassContext.METHOD);
			}
		} else {
			DecodedMethodDescriptor decodedDescriptor = DeclDecoder.decodeMethodDescriptor(method.descriptor(),
					this.classPackage);

			decodedDescriptor.returnType().print(this, ClassContext.METHOD);
			this.out.print(" ").print(method.name()).print("(");

			PrintSeparator separator = new PrintSeparator();

			for (PrintBuffer parameterType : decodedDescriptor.parameterTypes()) {
				separator.print(this, ClassContext.METHOD);
				parameterType.print(this, ClassContext.METHOD);
			}
			this.out.print(")");
			Attributes.print(Attributes.resolveOptionalAttribute(method.attributes(), ExceptionsAttribute.class), this,
					ClassContext.METHOD);
		}

		Optional<CodeAttribute> optionalCode = Attributes.resolveOptionalAttribute(method.attributes(),
				CodeAttribute.class);

		if (optionalCode.isPresent()) {
			this.out.println(" {");
			this.out.increaseIndent();
			optionalCode.get().print(this, ClassContext.METHOD);
			this.out.decreaseIndent();
			this.out.println("}");
		} else {
			this.out.println(";");
		}
	}

	private static class PackageInfoClassPrinter extends ClassPrinter {

		PackageInfoClassPrinter(MCDOutputBuffer out, ClassInfo classInfo) {
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

		ModuleInfoClassPrinter(MCDOutputBuffer out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassAnnotation();
			printModuleInfo();
		}

	}

	private static class InterfaceClassPrinter extends ClassPrinter {

		InterfaceClassPrinter(MCDOutputBuffer out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printClassSignature(S_INTERFACE);
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

	private static class AnnotationClassPrinter extends ClassPrinter {

		AnnotationClassPrinter(MCDOutputBuffer out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printClassSignature(S_ANNOTATION);
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

	private static class EnumClassPrinter extends ClassPrinter {

		EnumClassPrinter(MCDOutputBuffer out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printClassSignature(S_ENUM);
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

	private static class DefaultClassPrinter extends ClassPrinter {

		DefaultClassPrinter(MCDOutputBuffer out, ClassInfo classInfo) {
			super(out, classInfo);
		}

		@Override
		public void print() throws IOException {
			printClassComment();
			printClassPackage();
			this.out.println();
			printClassAnnotation();
			printClassSignature(S_CLASS);
			this.out.println(" {");
			printFields();
			printMethods();
			this.out.println().println("}");
		}

	}

}
