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
package de.carne.mcd.jvm.bootstrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import de.carne.mcd.common.Opcode;
import de.carne.util.Strings;

final class Reference {

	private static final String SEPARATOR1 = ";";
	private static final String SEPARATOR2 = ",";

	private static final String NO_VALUE = "-";

	private final String mnomic;
	private Opcode opcode;
	private List<String> arguments;
	private String operandStackIn;
	private String operandStackOut;
	private List<String> extraFields;

	Reference(String mnomic, Opcode opcode, List<String> arguments, String operandStackIn,
			String operandStackOut) {
		this(mnomic, opcode, arguments, operandStackIn, operandStackOut, Collections.emptyList());
	}

	private Reference(String mnomic, Opcode opcode, List<String> arguments, String operandStackIn,
			String operandStackOut, List<String> extraFields) {
		this.mnomic = mnomic;
		this.opcode = opcode;
		this.arguments = Collections.unmodifiableList(arguments);
		this.operandStackIn = operandStackIn;
		this.operandStackOut = operandStackOut;
		this.extraFields = Collections.unmodifiableList(extraFields);
	}

	public void update(Reference reference) {
		this.opcode = reference.opcode;
		this.arguments = reference.arguments;
		this.operandStackIn = reference.operandStackIn;
		this.operandStackOut = reference.operandStackOut;
	}

	public String mnomic() {
		return this.mnomic;
	}

	public Opcode opcode() {
		return this.opcode;
	}

	public static Reference fromLine(String line) throws IOException {
		StringTokenizer tokens = new StringTokenizer(line, SEPARATOR1);
		String instruction;
		String opcodeString;
		String argumentsString;
		String operandStackIn;
		String operandStackOut;
		List<String> extraFields = new ArrayList<>();

		try {
			instruction = decode(tokens.nextToken()).trim();
			opcodeString = tokens.nextToken().trim();
			argumentsString = decode(tokens.nextToken()).trim();
			operandStackIn = decode(tokens.nextToken()).trim();
			operandStackOut = decode(tokens.nextToken()).trim();
		} catch (NoSuchElementException e) {
			throw new IOException("Invalid instruction reference line: \"" + Strings.encode(line) + "\"", e);
		}
		while (tokens.hasMoreElements()) {
			extraFields.add(decode(tokens.nextToken()).trim());
		}

		Opcode opcode = Opcode.wrap(Opcode.parse(opcodeString));
		List<String> arguments = decodeArguments(argumentsString);

		return new Reference(instruction, opcode, arguments, operandStackIn, operandStackOut, extraFields);
	}

	public String toLine() {
		StringBuilder line = new StringBuilder();

		line.append(encode(this.mnomic)).append(SEPARATOR1);
		line.append(this.opcode).append(SEPARATOR1);
		line.append(encode(Strings.join(this.arguments, SEPARATOR2))).append(SEPARATOR1);
		line.append(encode(this.operandStackIn)).append(SEPARATOR1);
		line.append(encode(this.operandStackOut));
		for (String extraField : this.extraFields) {
			line.append(SEPARATOR1).append(encode(extraField));
		}
		return line.toString();
	}

	private static String encode(String value) {
		return (Strings.notEmpty(value) ? Strings.encode(value) : NO_VALUE);
	}

	private static String decode(String value) {
		return (NO_VALUE.equals(value) ? "" : Strings.decode(value));
	}

	private static List<String> decodeArguments(String argumentsString) {
		List<String> arguments = new ArrayList<>();
		StringTokenizer tokens = new StringTokenizer(argumentsString, SEPARATOR2);

		while (tokens.hasMoreElements()) {
			arguments.add(tokens.nextToken().trim());
		}
		return arguments;
	}

	@Override
	public String toString() {
		return this.mnomic + ":" + this.opcode;
	}

}
