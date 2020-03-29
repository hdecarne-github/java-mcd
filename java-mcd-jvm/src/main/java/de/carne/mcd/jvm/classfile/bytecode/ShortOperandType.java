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
package de.carne.mcd.jvm.classfile.bytecode;

import java.io.IOException;

import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;
import de.carne.text.HexFormat;

/**
 * Possible short operand types.
 */
public enum ShortOperandType implements OperandType {

	/**
	 * Immediate short value.
	 */
	IMMEDIATE_VALUE((pc, operand, out) -> out.printValue(Short.toString(operand))),

	/**
	 * Ignored value.
	 */
	IGNORE((pc, operand, out) -> { // ignore
	}),

	/**
	 * Increment byte value (for wide iinc).
	 */
	INC_CONST((pc, operand, out) -> out.print(", ").printValue(Short.toString(operand))),

	/**
	 * Index into the local variable table.
	 */
	LOCAL_VARIABLE_INDEX((pc, operand, out) -> out.printValue("local_" + Short.toUnsignedInt(operand))),

	/**
	 * Index into the run-time constant pool.
	 */
	RUNTIME_CONSTANT_INDEX((pc, operand, out) -> out.printValue("#" + Short.toUnsignedInt(operand)).print(" ")
			.printComment("// ").printComment(runtimeConstantComment(operand))),

	/**
	 * Branch target.
	 */
	BRANCH((pc, operand, out) -> out.printValue(operand >= 0 ? "+" : "").printValue(Short.toString(operand)).print(" ")
			.printComment("// ").printComment(HexFormat.LOWER_CASE.format((short) (pc + operand))));

	private final ShortOperandDecoder decoder;

	private ShortOperandType(ShortOperandDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'S';
	}

	@Override
	public void decode(int pc, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.decoder.decode(pc, buffer.decodeI16(), out);
	}

	private static String runtimeConstantComment(short index) {
		return MachineCodeDecoder.getDecoder(BytecodeDecoder.class).getClassInfo()
				.resolveRuntimeSymbol(Short.toUnsignedInt(index));
	}

}
