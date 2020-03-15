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

import java.io.DataInput;
import java.io.IOException;

import de.carne.mcd.common.instruction.Instruction;
import de.carne.mcd.common.instruction.InstructionFactory;

class X86InstructionFactory implements InstructionFactory {

	@Override
	public Instruction loadInstruction(DataInput in) throws IOException {
		return X86Instruction.load(in);
	}

	@Override
	public Instruction getDefaultInstruction(byte[] opcode, int offset, int length) throws IOException {
		return new UnknownX86Instruction(opcode, offset, length);
	}

}
