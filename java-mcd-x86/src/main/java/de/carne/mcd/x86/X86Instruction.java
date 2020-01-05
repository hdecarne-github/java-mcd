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
import java.io.DataOutput;
import java.io.IOException;

import de.carne.mcd.common.Instruction;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;

/**
 * A single x86 instruction.
 */
public class X86Instruction implements Instruction {

	private final String mnemonic;

	/**
	 * Constructs a new {@linkplain X86Instruction} instance.
	 *
	 * @param mnemonic the instruction mnemonic to use.
	 */
	public X86Instruction(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	static X86Instruction load(DataInput in) throws IOException {
		String mnemonic = in.readUTF();

		return new X86Instruction(mnemonic);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeUTF(this.mnemonic);
	}

	@Override
	public void decode(int pc, MCDDecodeBuffer buffer, MCDOutput out) throws IOException {
		out.printKeyword(this.mnemonic);
		out.println();
	}

}
