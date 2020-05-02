/*
 * Copyright (c) 2019-2020 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.jvmdecoder.classfile.decl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.eclipse.jdt.annotation.Nullable;

import de.carne.mcd.jvmdecoder.classfile.ClassName;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.PrintBuffer;
import de.carne.mcd.jvmdecoder.classfile.PrintSeparator;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclLexer;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ArrayTypeContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ArrayTypeSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.BaseTypeContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ClassBoundContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ClassTypeSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ClassTypeSignatureSuffixContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.DescriptorTypeContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.InterfaceBoundContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.JavaTypeSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ObjectTypeContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ReferenceTypeSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ReturnTypeContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.SuperClassSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.SuperInterfaceSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ThrowsSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.TypeArgumentContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.TypeArgumentsContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.TypeParameterContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.TypeParametersContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.TypeVariableSignatureContext;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.WildcardIndicatorContext;
import de.carne.util.Check;

/**
 * Base class for all kinds of declaration decoders.
 */
public abstract class DeclDecoder {

	private static final Map<String, String> BASE_TYPE_MAP = new HashMap<>();

	static {
		BASE_TYPE_MAP.put("B", ClassPrinter.S_BYTE);
		BASE_TYPE_MAP.put("C", ClassPrinter.S_CHAR);
		BASE_TYPE_MAP.put("D", ClassPrinter.S_DOUBLE);
		BASE_TYPE_MAP.put("F", ClassPrinter.S_FLOAT);
		BASE_TYPE_MAP.put("I", ClassPrinter.S_INT);
		BASE_TYPE_MAP.put("J", ClassPrinter.S_LONG);
		BASE_TYPE_MAP.put("S", ClassPrinter.S_SHORT);
		BASE_TYPE_MAP.put("V", ClassPrinter.S_VOID);
		BASE_TYPE_MAP.put("Z", ClassPrinter.S_BOOLEAN);
	}

	protected DeclDecoder() {
		// Nothing to do here
	}

	/**
	 * Decodes a {@linkplain DecodedFieldDescriptor}.
	 *
	 * @param descriptor the descriptor string to decode.
	 * @param classPackage the current class package (for shortening of object type names).
	 * @return the decoded {@linkplain DecodedFieldDescriptor}.
	 */
	public static DecodedFieldDescriptor decodeFieldDescriptor(String descriptor, String classPackage) {
		return new DecodedFieldDescriptor(Objects.requireNonNull(getParser(descriptor).fieldDescriptor()),
				classPackage);
	}

	/**
	 * Decodes a {@linkplain DecodedMethodDescriptor}.
	 *
	 * @param descriptor the descriptor string to decode.
	 * @param classPackage the current class package (for shortening of object type names).
	 * @return the decoded {@linkplain DecodedMethodDescriptor}.
	 */
	public static DecodedMethodDescriptor decodeMethodDescriptor(String descriptor, String classPackage) {
		return new DecodedMethodDescriptor(Objects.requireNonNull(getParser(descriptor).methodDescriptor()),
				classPackage);
	}

	/**
	 * Decodes a {@linkplain DecodedClassSignature}.
	 *
	 * @param signature the signature string to decode.
	 * @param classPackage the current class package (for shortening of object type names).
	 * @return the decoded {@linkplain DecodedClassSignature}.
	 */
	public static DecodedClassSignature decodeClassSignature(String signature, String classPackage) {
		return new DecodedClassSignature(Objects.requireNonNull(getParser(signature).classSignature()), classPackage);
	}

	/**
	 * Decodes a {@linkplain DecodedMethodSignature}.
	 *
	 * @param signature the signature string to decode.
	 * @param classPackage the current class package (for shortening of object type names).
	 * @return the decoded {@linkplain DecodedMethodSignature}.
	 */
	public static DecodedMethodSignature decodeMethodSignature(String signature, String classPackage) {
		return new DecodedMethodSignature(Objects.requireNonNull(getParser(signature).methodSignature()), classPackage);
	}

	/**
	 * Decodes a {@linkplain DecodedFieldSignature}.
	 *
	 * @param signature the signature string to decode.
	 * @param classPackage the current class package (for shortening of object type names).
	 * @return the decoded {@linkplain DecodedFieldSignature}.
	 */
	public static DecodedFieldSignature decodeFieldSignature(String signature, String classPackage) {
		return new DecodedFieldSignature(Objects.requireNonNull(getParser(signature).fieldSignature()), classPackage);
	}

	protected static DeclParser getParser(String input) {
		CharStream inputStream = CharStreams.fromString(input);
		DeclLexer lexer = new DeclLexer(inputStream);
		TokenStream tokens = new CommonTokenStream(lexer);

		return new DeclParser(tokens);
	}

	protected static PrintBuffer decodeBaseType(PrintBuffer buffer, BaseTypeContext ctx) {
		buffer.append(Objects.requireNonNull(BASE_TYPE_MAP.get(ctx.getText())), PrintBuffer::printKeyword);
		return buffer;
	}

	protected static PrintBuffer decodeDescriptorType(DescriptorTypeContext ctx, String classPackage) {
		return decodeDescriptorTypeHelper(new PrintBuffer(), ctx, classPackage);
	}

	@SuppressWarnings("null")
	private static PrintBuffer decodeDescriptorTypeHelper(PrintBuffer buffer, DescriptorTypeContext ctx,
			String classPackage) {
		BaseTypeContext baseTypeCtx;
		ObjectTypeContext objectTypeCtx;
		ArrayTypeContext arrayTypeCtx;

		if ((baseTypeCtx = ctx.baseType()) != null) {
			decodeBaseType(buffer, baseTypeCtx);
		} else if ((objectTypeCtx = ctx.objectType()) != null) {
			String objectType = ClassName.effectiveName(ClassName.decode(objectTypeCtx.className().getText()),
					classPackage);

			buffer.append(objectType);
		} else if ((arrayTypeCtx = ctx.arrayType()) != null) {
			decodeDescriptorTypeHelper(buffer, arrayTypeCtx.componentType().descriptorType(), classPackage)
					.append("[]");
		} else {
			// Should never happen
			Check.fail();
		}
		return buffer;
	}

	@SuppressWarnings("null")
	protected static List<PrintBuffer> decodeTypeParameters(@Nullable TypeParametersContext ctx, String classPackage) {
		List<PrintBuffer> parameters;

		if (ctx != null) {
			parameters = new ArrayList<>();
			for (TypeParameterContext typeParameterCtx : ctx.typeParameter()) {
				parameters.add(decodeTypeParameter(typeParameterCtx, classPackage));
			}
		} else {
			parameters = Collections.emptyList();
		}
		return parameters;
	}

	@SuppressWarnings("null")
	private static PrintBuffer decodeTypeParameter(TypeParameterContext ctx, String classPackage) {
		PrintBuffer buffer = new PrintBuffer();

		buffer.append(ctx.identifier().getText());

		List<ReferenceTypeSignatureContext> referenceTypeSignatureCtxs = new ArrayList<>();
		ClassBoundContext classBoundCtx = ctx.classBound();

		if (classBoundCtx != null) {
			ReferenceTypeSignatureContext referenceTypeSignatureCtx = classBoundCtx.referenceTypeSignature();

			if (referenceTypeSignatureCtx != null && !isObject(referenceTypeSignatureCtx)) {
				referenceTypeSignatureCtxs.add(referenceTypeSignatureCtx);
			}
		}

		List<InterfaceBoundContext> interfaceBound = ctx.interfaceBound();

		if (interfaceBound != null) {
			for (InterfaceBoundContext interfaceBoundCtx : interfaceBound) {
				referenceTypeSignatureCtxs.add(interfaceBoundCtx.referenceTypeSignature());
			}
		}

		if (!referenceTypeSignatureCtxs.isEmpty()) {
			buffer.append(" " + ClassPrinter.S_EXTENDS + " ");
		}

		PrintSeparator printSeparator = new PrintSeparator(" & ");

		for (ReferenceTypeSignatureContext referenceTypeSignatureCtx : referenceTypeSignatureCtxs) {
			buffer.append(printSeparator.next());
			decodeReferenceTypeSignature(buffer, referenceTypeSignatureCtx, classPackage);
		}
		return buffer;
	}

	@SuppressWarnings("null")
	private static PrintBuffer decodeReferenceTypeSignature(PrintBuffer buffer, ReferenceTypeSignatureContext ctx,
			String classPackage) {
		ClassTypeSignatureContext classTypeSignatureCtx;
		TypeVariableSignatureContext typeVariableSignatureCtx;
		ArrayTypeSignatureContext arrayTypeSignatureCtx;

		if ((classTypeSignatureCtx = ctx.classTypeSignature()) != null) {
			decodeClassTypeSignature(buffer, classTypeSignatureCtx, classPackage);
		} else if ((typeVariableSignatureCtx = ctx.typeVariableSignature()) != null) {
			buffer.append(typeVariableSignatureCtx.identifier().getText());
		} else if ((arrayTypeSignatureCtx = ctx.arrayTypeSignature()) != null) {
			decodeArrayTypeSignature(buffer, arrayTypeSignatureCtx, classPackage);
		} else {
			// Should never happen
			Check.fail();
		}
		return buffer;
	}

	@SuppressWarnings("null")
	private static PrintBuffer decodeClassTypeSignature(PrintBuffer buffer, ClassTypeSignatureContext ctx,
			String classPackage) {
		String classTypeName = ClassName.effectiveName(ClassName.decode(ctx.classTypeName().getText()), classPackage);

		buffer.append(classTypeName);
		decodeTypeArguments(buffer, ctx.typeArguments(), classPackage);

		List<ClassTypeSignatureSuffixContext> classTypeSignatureSuffix = ctx.classTypeSignatureSuffix();

		if (classTypeSignatureSuffix != null) {
			for (ClassTypeSignatureSuffixContext classTypeSignatureSuffixCtx : classTypeSignatureSuffix) {
				buffer.append("." + classTypeSignatureSuffixCtx.identifier().getText());
				decodeTypeArguments(buffer, classTypeSignatureSuffixCtx.typeArguments(), classPackage);
			}
		}
		return buffer;
	}

	@SuppressWarnings("java:S3776")
	private static PrintBuffer decodeTypeArguments(PrintBuffer buffer, @Nullable TypeArgumentsContext ctx,
			String classPackage) {
		if (ctx != null) {
			buffer.append("<");

			PrintSeparator separator = new PrintSeparator();

			for (TypeArgumentContext typeArgumentCtx : ctx.typeArgument()) {
				buffer.append(separator.next());

				ReferenceTypeSignatureContext referenceTypeSignatureCtx = typeArgumentCtx.referenceTypeSignature();

				if (referenceTypeSignatureCtx != null) {
					WildcardIndicatorContext wildcardIndicatorCtx = typeArgumentCtx.wildcardIndicator();

					if (wildcardIndicatorCtx != null) {
						buffer.append("? ");
						buffer.append(("+".equals(wildcardIndicatorCtx.getText()) ? ClassPrinter.S_EXTENDS
								: ClassPrinter.S_SUPER) + " ", PrintBuffer::printKeyword);
					}
					decodeReferenceTypeSignature(buffer, referenceTypeSignatureCtx, classPackage);
				} else {
					buffer.append("?");
				}
			}
			buffer.append(">");
		}
		return buffer;
	}

	@SuppressWarnings("null")
	private static PrintBuffer decodeArrayTypeSignature(PrintBuffer buffer, ArrayTypeSignatureContext ctx,
			String classPackage) {
		return decodeJavaTypeSignature(buffer, ctx.javaTypeSignature(), classPackage).append("[]");
	}

	private static PrintBuffer decodeJavaTypeSignature(PrintBuffer buffer, JavaTypeSignatureContext ctx,
			String classPackage) {
		ReferenceTypeSignatureContext referenceTypeSignatureCtx;
		BaseTypeContext baseTypeCtx;

		if ((referenceTypeSignatureCtx = ctx.referenceTypeSignature()) != null) {
			decodeReferenceTypeSignature(buffer, referenceTypeSignatureCtx, classPackage);
		} else if ((baseTypeCtx = ctx.baseType()) != null) {
			decodeBaseType(buffer, baseTypeCtx);
		} else {
			// Should never happen
			Check.fail();
		}
		return buffer;
	}

	@SuppressWarnings("null")
	protected static PrintBuffer decodeSuperClassSignature(SuperClassSignatureContext ctx, String classPackage) {
		PrintBuffer buffer = new PrintBuffer();
		ReferenceTypeSignatureContext referenceTypeSignatureCtx = ctx.referenceTypeSignature();

		if (!isObject(referenceTypeSignatureCtx)) {
			decodeReferenceTypeSignature(buffer, referenceTypeSignatureCtx, classPackage);
		}
		return buffer;
	}

	@SuppressWarnings("null")
	protected static List<PrintBuffer> decodeSuperInterfaceSignatures(
			@Nullable List<SuperInterfaceSignatureContext> superInterfaceCtxs, String classPackage) {
		List<PrintBuffer> superInterfaces;

		if (superInterfaceCtxs != null) {
			superInterfaces = new ArrayList<>();
			for (SuperInterfaceSignatureContext superInterfaceCtx : superInterfaceCtxs) {
				superInterfaces.add(decodeReferenceTypeSignature(new PrintBuffer(),
						superInterfaceCtx.referenceTypeSignature(), classPackage));
			}
		} else {
			superInterfaces = Collections.emptyList();
		}
		return superInterfaces;
	}

	protected static PrintBuffer decodeReferenceTypeSignature(ReferenceTypeSignatureContext ctx, String classPackage) {
		return decodeReferenceTypeSignature(new PrintBuffer(), ctx, classPackage);
	}

	@SuppressWarnings("null")
	protected static PrintBuffer decodeReturnType(ReturnTypeContext ctx, String classPackage) {
		return decodeJavaTypeSignature(new PrintBuffer(), ctx.javaTypeSignature(), classPackage);
	}

	protected static List<PrintBuffer> decodeJavaTypeSignatures(
			@Nullable List<JavaTypeSignatureContext> javaTypeSignatureCtxs, String classPackage) {
		List<PrintBuffer> javaTypes;

		if (javaTypeSignatureCtxs != null) {
			javaTypes = new ArrayList<>();
			for (JavaTypeSignatureContext javaTypeSignatureCtx : javaTypeSignatureCtxs) {
				javaTypes.add(decodeJavaTypeSignature(new PrintBuffer(), javaTypeSignatureCtx, classPackage));
			}
		} else {
			javaTypes = Collections.emptyList();
		}
		return javaTypes;
	}

	@SuppressWarnings("null")
	protected static List<PrintBuffer> decodeThrowsSignature(@Nullable List<ThrowsSignatureContext> throwsSignatureCtxs,
			String classPackage) {
		List<PrintBuffer> throwsTypes;

		if (throwsSignatureCtxs != null) {
			throwsTypes = new ArrayList<>();
			for (ThrowsSignatureContext throwsSignatureCtx : throwsSignatureCtxs) {
				ClassTypeSignatureContext classTypeSignatureCtx;
				TypeVariableSignatureContext typeVariableSignatureCtx;

				if ((classTypeSignatureCtx = throwsSignatureCtx.classTypeSignature()) != null) {
					throwsTypes.add(decodeClassTypeSignature(new PrintBuffer(), classTypeSignatureCtx, classPackage));
				} else if ((typeVariableSignatureCtx = throwsSignatureCtx.typeVariableSignature()) != null) {
					throwsTypes.add(new PrintBuffer().append(typeVariableSignatureCtx.identifier().getText()));
				}
			}
		} else {
			throwsTypes = Collections.emptyList();
		}
		return throwsTypes;
	}

	private static boolean isObject(ReferenceTypeSignatureContext ctx) {
		return "Ljava/lang/Object;".equals(ctx.getText());
	}

}
