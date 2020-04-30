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
package de.carne.mcd.x86;

import java.io.DataOutput;
import java.io.IOException;

import de.carne.mcd.instruction.Instruction;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;
import de.carne.text.HexFormat;
import de.carne.util.Check;

class UnknownX86Instruction implements Instruction {

	@Override
	public void save(DataOutput out) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public void decode(long ip, InstructionOpcode opcode, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decode(opcode, out);
	}

	public static void decode(InstructionOpcode opcode, MCDOutputBuffer out) throws IOException {
		out.printKeyword("db");

		String separator = " ";

		for (byte opcodeByte : opcode.bytes()) {
			out.print(separator).printValue("0x").printValue(HexFormat.LOWER_CASE.format(opcodeByte));
			separator = ", ";
		}
		out.println();
	}

}
