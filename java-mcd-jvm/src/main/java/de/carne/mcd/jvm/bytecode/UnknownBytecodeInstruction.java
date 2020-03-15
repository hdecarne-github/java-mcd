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

import java.io.DataOutput;
import java.io.IOException;

import de.carne.boot.check.Check;
import de.carne.mcd.common.instruction.Instruction;
import de.carne.mcd.common.instruction.InstructionOpcode;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;

class UnknownBytecodeInstruction implements Instruction {

	private final String opcodeString;

	UnknownBytecodeInstruction(byte[] opcode, int offset, int length) {
		this.opcodeString = InstructionOpcode.toString(opcode, offset, length);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public void decode(long ip, InstructionOpcode opcode, MCDInputBuffer buffer, MCDOutputBuffer out)
			throws IOException {
		out.printlnError(this.opcodeString);
	}

}
