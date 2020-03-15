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
package de.carne.mcd.jvm.bytecode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;
import de.carne.mcd.jvm.BytecodeDecoder;

/**
 * Possible byte operand types.
 */
public enum ByteOperandType implements OperandType {

	/**
	 * Immediate byte value.
	 */
	IMMEDIATE_VALUE((pc, operand, out) -> out.printValue(Byte.toString(operand))),

	/**
	 * Array type value.
	 */
	ARRAY_TYPE((pc, operand, out) -> out.printValue(Byte.toString(operand)).print(" ").printComment("// ")
			.printComment(arrayTypeComment(operand))),

	/**
	 * Increment byte value (for iinc).
	 */
	INC_CONST((pc, operand, out) -> out.print(", ").printValue(Byte.toString(operand))),

	/**
	 * Dimension byte value (for multianewarray).
	 */
	DIMENSION((pc, operand, out) -> out.print(", ").printValue(Byte.toString(operand))),

	/**
	 * Index into the local variable table.
	 */
	LOCAL_VARIABLE_INDEX((pc, operand, out) -> out.printValue("local_" + Byte.toUnsignedInt(operand))),

	/**
	 * Index into the run-time constant pool.
	 */
	RUNTIME_CONSTANT_INDEX((pc, operand, out) -> out.printValue("#" + Byte.toUnsignedInt(operand)).print(" ")
			.printComment("// ").printComment(runtimeConstantComment(operand)));

	private static final Map<Byte, String> ARRAY_TYPE_COMMENTS = new HashMap<>();

	private final ByteOperandDecoder decoder;

	private ByteOperandType(ByteOperandDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'B';
	}

	@Override
	public void decode(int pc, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.decoder.decode(pc, buffer.decodeI8(), out);
	}

	static {
		ARRAY_TYPE_COMMENTS.put((byte) 4, "boolean[]");
		ARRAY_TYPE_COMMENTS.put((byte) 5, "char[]");
		ARRAY_TYPE_COMMENTS.put((byte) 6, "float[]");
		ARRAY_TYPE_COMMENTS.put((byte) 7, "couble[]");
		ARRAY_TYPE_COMMENTS.put((byte) 8, "byte[]");
		ARRAY_TYPE_COMMENTS.put((byte) 9, "short[]");
		ARRAY_TYPE_COMMENTS.put((byte) 10, "int[]");
		ARRAY_TYPE_COMMENTS.put((byte) 11, "long[]");
	}

	private static String arrayTypeComment(byte arrayType) {
		return ARRAY_TYPE_COMMENTS.getOrDefault(arrayType, "?");
	}

	private static String runtimeConstantComment(byte index) {
		return MachineCodeDecoder.getDecoder(BytecodeDecoder.class).getClassInfo()
				.resolveRuntimeSymbol(Byte.toUnsignedInt(index));
	}

}
