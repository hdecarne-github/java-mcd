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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import de.carne.mcd.common.Instruction;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;

/**
 * Bytecode instruction without any parameters (void).
 */
public class BytecodeInstructionV implements Instruction {

	/**
	 * The instruction tag used to store and load this instruction type.
	 */
	public static final byte TAG = 0;

	private final String mnomic;

	/**
	 * Constructs a new {@linkplain BytecodeInstructionV} instance.
	 *
	 * @param mnomic the instruction mnomic to use.
	 */
	public BytecodeInstructionV(String mnomic) {
		this.mnomic = mnomic;
	}

	/**
	 * Loads and initializes a {@linkplain BytecodeInstructionV} instruction.
	 * <p>
	 * This function assumes that the initial instruction tag has already been read.
	 * </p>
	 *
	 * @param in the {@linkplain DataInput} to load from.
	 * @return the loaded {@linkplain BytecodeInstructionV} instruction.
	 * @throws IOException if an I/O error occurs.
	 */
	public static BytecodeInstructionV load(DataInput in) throws IOException {
		String mnomic = in.readUTF();

		return new BytecodeInstructionV(mnomic);
	}

	@Override
	public void store(DataOutput out) throws IOException {
		out.write(TAG);
		out.writeUTF(this.mnomic);
	}

	@Override
	public void decode(MCDDecodeBuffer buffer, MCDOutput out) throws IOException {
		out.printlnKeyword(this.mnomic);
	}

}
