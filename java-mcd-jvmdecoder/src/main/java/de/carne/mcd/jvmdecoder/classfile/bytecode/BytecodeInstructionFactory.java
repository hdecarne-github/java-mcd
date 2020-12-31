/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.jvmdecoder.classfile.bytecode;

import java.io.DataInput;
import java.io.IOException;

import de.carne.mcd.instruction.Instruction;
import de.carne.mcd.instruction.InstructionFactory;

class BytecodeInstructionFactory implements InstructionFactory {

	@Override
	public Instruction loadInstruction(DataInput in) throws IOException {
		return BytecodeInstruction.load(in);
	}

	@Override
	public Instruction getDefaultInstruction() {
		return new UnknownBytecodeInstruction();
	}

}
