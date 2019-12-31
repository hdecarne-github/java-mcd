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
package de.carne.mcd.jvm.classfile.descriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import de.carne.mcd.jvm.ClassPrinter;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarLexer;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser;

/**
 * Base class for all kinds of descriptor information.
 */
public abstract class Descriptor {

	private static final Map<String, String> INTEGRAL_TYPE_MAP = new HashMap<>();

	static {
		INTEGRAL_TYPE_MAP.put("B", ClassPrinter.S_BYTE);
		INTEGRAL_TYPE_MAP.put("C", ClassPrinter.S_CHAR);
		INTEGRAL_TYPE_MAP.put("D", ClassPrinter.S_DOUBLE);
		INTEGRAL_TYPE_MAP.put("F", ClassPrinter.S_FLOAT);
		INTEGRAL_TYPE_MAP.put("I", ClassPrinter.S_INT);
		INTEGRAL_TYPE_MAP.put("J", ClassPrinter.S_LONG);
		INTEGRAL_TYPE_MAP.put("S", ClassPrinter.S_SHORT);
		INTEGRAL_TYPE_MAP.put("V", ClassPrinter.S_VOID);
		INTEGRAL_TYPE_MAP.put("Z", ClassPrinter.S_BOOLEAN);
	}

	protected Descriptor() {
		// Nothing to do here
	}

	/**
	 * Decodes a {@linkplain FieldDescriptor}.
	 *
	 * @param descriptor the descriptor string to decode.
	 * @param classPackage the current class package (for shortening of object type names).
	 * @return the decoded {@linkplain FieldDescriptor}.
	 */
	public static FieldDescriptor decodeFieldDescriptor(String descriptor, String classPackage) {
		return new FieldDescriptor(Objects.requireNonNull(getParser(descriptor).fieldDescriptor()), classPackage);
	}

	/**
	 * Decodes a {@linkplain MethodDescriptor}.
	 *
	 * @param descriptor the descriptor string to decode.
	 * @param classPackage the current class package (for shortening of object type names).
	 * @return the decoded {@linkplain MethodDescriptor}.
	 */
	public static MethodDescriptor decodeMethodDescriptor(String descriptor, String classPackage) {
		return new MethodDescriptor(Objects.requireNonNull(getParser(descriptor).methodDescriptor()), classPackage);
	}

	private static DescriptorGrammarParser getParser(String descriptor) {
		CharStream input = CharStreams.fromString(descriptor);
		DescriptorGrammarLexer lexer = new DescriptorGrammarLexer(input);
		TokenStream tokens = new CommonTokenStream(lexer);

		return new DescriptorGrammarParser(tokens);
	}

	protected static String getIntegralType(String typeKey) {
		return Objects.requireNonNull(INTEGRAL_TYPE_MAP.get(typeKey));
	}

}
