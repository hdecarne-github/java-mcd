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

import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class X86InstructionSignature {

	public static final Byte NO_OPCODE_EXTENSION = Byte.valueOf((byte) 0xff);

	private final String mnemonic;
	private final boolean hasModRM;
	private final List<OperandType> operands;

	/**
	 *
	 */
	public X86InstructionSignature(String mnemonic, boolean hasModRM) {
		this(mnemonic, hasModRM, Collections.emptyList());
	}

	/**
	 *
	 */
	public X86InstructionSignature(String mnemonic, boolean hasModRM, List<OperandType> operands) {
		this.mnemonic = mnemonic;
		this.hasModRM = hasModRM;
		this.operands = Collections.unmodifiableList(operands);
	}

	public String mnemonic() {
		return this.mnemonic;
	}

	public boolean hasModRM() {
		return this.hasModRM;
	}

	public List<OperandType> operands() {
		return this.operands;
	}

}
