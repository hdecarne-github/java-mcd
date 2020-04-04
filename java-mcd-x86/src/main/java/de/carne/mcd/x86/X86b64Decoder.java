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

import java.io.IOException;
import java.util.Optional;

import de.carne.mcd.instruction.InstructionIndex;
import de.carne.text.HexFormat;
import de.carne.util.Late;

/**
 * x86-64 machine code decoder.
 */
public class X86b64Decoder extends X86Decoder {

	/**
	 * Decoder name.
	 */
	@SuppressWarnings("squid:S1845")
	public static final String NAME = "x86-64 instructions";

	private static final Late<InstructionIndex> X86B64_INSTRUCTION_INDEX_HOLDER = new Late<>();

	/**
	 * Constructs a new {@linkplain X86b64Decoder} instance.
	 */
	public X86b64Decoder() {
		super(NAME);
	}

	@Override
	@SuppressWarnings("resource")
	protected InstructionIndex getInstructionIndex() throws IOException {
		InstructionIndex instructionIndex;

		synchronized (X86B64_INSTRUCTION_INDEX_HOLDER) {
			Optional<InstructionIndex> instructionIndexHolder = X86B64_INSTRUCTION_INDEX_HOLDER.getOptional();

			if (instructionIndexHolder.isPresent()) {
				instructionIndex = instructionIndexHolder.get();
			} else {
				instructionIndex = X86B64_INSTRUCTION_INDEX_HOLDER.set(X86b64InstructionIndex.open());
			}
		}
		return instructionIndex;
	}

	@Override
	protected String formatInstructionPointer(long ip) {
		return HexFormat.LOWER_CASE.format(ip);
	}

}
