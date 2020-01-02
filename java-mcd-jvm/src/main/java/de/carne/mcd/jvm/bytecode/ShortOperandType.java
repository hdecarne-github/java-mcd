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

import de.carne.boot.logging.Log;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.jvm.BytecodeDecoder;
import de.carne.text.HexFormatter;

/**
 * Possible short operand types.
 */
public enum ShortOperandType implements OperandDecoder {

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
	 * Index into the run-time constant pool.
	 */
	RUNTIME_CONSTANT_INDEX((pc, operand, out) -> out.printValue("#" + Short.toUnsignedInt(operand)).print(" ")
			.printComment("// ").printComment(runtimeConstantComment(operand))),

	/**
	 * Branch target.
	 */
	BRANCH((pc, operand, out) -> out.printValue(operand >= 0 ? "+" : "").printValue(Short.toString(operand)).print(" ")
			.printComment("// ").printComment(HexFormatter.LOWER_CASE.format((short) (pc + operand))));

	private static final Log LOG = new Log();

	private final ShortOperandDecoder decoder;

	private ShortOperandType(ShortOperandDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'S';
	}

	@Override
	public void decode(int pc, MCDDecodeBuffer buffer, MCDOutput out) throws IOException {
		boolean decodeFailure = false;
		short operand = 0;

		try {
			operand = buffer.decodeI16();
		} catch (IOException e) {
			LOG.error(e, "Failed to decode short operand");

			decodeFailure = true;
		}
		if (!decodeFailure) {
			this.decoder.decode(pc, operand, out);
		} else {
			out.printError("?");
		}
	}

	private static String runtimeConstantComment(short index) {
		return BytecodeDecoder.getDecodeContext().getClassInfo().resolveRuntimeSymbol(Short.toUnsignedInt(index));
	}

}
