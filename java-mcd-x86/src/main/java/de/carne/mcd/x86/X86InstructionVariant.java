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
 * A single x86 instruction variant.
 */
public final class X86InstructionVariant {

	/**
	 * Opcode extension value for instructions without variants.
	 */
	public static final Byte NO_OPCODE_EXTENSION = Byte.valueOf((byte) 0xff);

	private final String mnemonic;
	private final List<NamedDecoder> decoders;

	/**
	 * Constructs a new {@linkplain X86InstructionVariant} instance.
	 *
	 * @param mnemonic the variant's mnemonic.
	 */
	public X86InstructionVariant(String mnemonic) {
		this(mnemonic, Collections.emptyList());
	}

	/**
	 * Constructs a new {@linkplain X86InstructionVariant} instance.
	 *
	 * @param mnemonic the variant's mnemonic.
	 * @param decoders the variant's decoders.
	 */
	public X86InstructionVariant(String mnemonic, List<NamedDecoder> decoders) {
		this.mnemonic = mnemonic;
		this.decoders = Collections.unmodifiableList(decoders);
	}

	/**
	 * Gets this variant's mnemonic.
	 *
	 * @return this variant's mnemonic.
	 */
	public String mnemonic() {
		return this.mnemonic;
	}

	/**
	 * Gets this variant's decoders.
	 *
	 * @return this variant's decoders.
	 */
	public List<NamedDecoder> decoders() {
		return this.decoders;
	}

	/**
	 * Checks whether this variant is a prefix instruction.
	 *
	 * @return {@code true} if this variant is a prefix instruction.
	 */
	public boolean isPrefix() {
		return !this.decoders.isEmpty() && this.decoders.get(0) instanceof PrefixDecoder;
	}

	/**
	 * Checks whether this variant has a ModR/M byte.
	 *
	 * @return {@code true} if this variant has a ModR/M byte.
	 */
	public boolean hasModRM() {
		return !this.decoders.isEmpty() && this.decoders.get(0) instanceof ModRMDecoder;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(this.mnemonic);
		if (hasModRM()) {
			buffer.append(" ModR/M");
		}

		int operandIndex = 0;

		for (NamedDecoder operand : this.decoders) {
			buffer.append(operandIndex == 0 ? " " : ", ");
			if (!(operand instanceof PrefixDecoder)) {
				buffer.append(operand.name());
				operandIndex++;
			}
		}
		return buffer.toString();
	}

}
