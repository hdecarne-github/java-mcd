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
package de.carne.mcd.x86.bootstrap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import de.carne.mcd.common.Instruction;
import de.carne.mcd.common.Opcode;
import de.carne.mcd.common.bootstrap.InstructionReferenceEntry;
import de.carne.mcd.x86.X86Instruction;
import de.carne.util.Strings;

@SuppressWarnings("squid:S2160")
class X86InstructionReferenceEntry extends InstructionReferenceEntry {

	private boolean x86b16 = true;
	private boolean x86b32 = true;
	private boolean x86b64 = true;

	X86InstructionReferenceEntry(Opcode opcode, String mnemonic, String signature) {
		super(opcode, mnemonic, (Strings.notEmpty(signature) ? Arrays.asList(signature) : Collections.emptyList()));
	}

	X86InstructionReferenceEntry(InstructionReferenceEntry entryData) {
		super(entryData);
	}

	@Override
	public Instruction toInstruction() throws IOException {
		return new X86Instruction(mnemonic());
	}

	public boolean isX86b16() {
		return this.x86b16;
	}

	public void disableX86b16() {
		this.x86b16 = false;
	}

	public boolean isX86b32() {
		return this.x86b32;
	}

	public void disableX86b32() {
		this.x86b32 = false;
	}

	public boolean isX86b64() {
		return this.x86b64;
	}

	public void disableX86b64() {
		this.x86b64 = false;
	}

}
