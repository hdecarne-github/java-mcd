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
package de.carne.mcd.jvmdecoder.classfile.bytecode.bootstrap;

import de.carne.mcd.instruction.InstructionOpcode;

final class InstructionForm {

	private final String mnemonic;
	private final InstructionOpcode opcode;

	InstructionForm(String mnemonic, byte[] opcodeBytes) {
		this.mnemonic = mnemonic;
		this.opcode = InstructionOpcode.wrap(opcodeBytes);
	}

	public String mnemonic() {
		return this.mnemonic;
	}

	public InstructionOpcode opcode() {
		return this.opcode;
	}

	@Override
	public String toString() {
		return this.opcode + " " + this.mnemonic;
	}

}
