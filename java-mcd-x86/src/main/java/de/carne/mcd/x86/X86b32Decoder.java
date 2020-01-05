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
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

import de.carne.mcd.common.Instruction;
import de.carne.mcd.common.InstructionIndex;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;
import de.carne.text.HexFormat;
import de.carne.util.Late;

/**
 * x86-32 machine code decoder.
 */
public class X86b32Decoder extends X86Decoder {

	private static final String NAME = "x86-32 instructions";

	private static final Late<InstructionIndex> X86B32_INSTRUCTION_INDEX_HOLDER = new Late<>();

	/**
	 * Constructs a new {@linkplain X86b32Decoder} instance.
	 */
	public X86b32Decoder() {
		super(NAME);
	}

	@Override
	public void decode(ReadableByteChannel in, MCDOutput out) throws IOException {
		MCDDecodeBuffer buffer = newDecodeBuffer(in);
		InstructionIndex instructionIndex = getInstructionIndex();
		Instruction instruction;
		int ip = 0;

		while ((instruction = instructionIndex.lookupNextInstruction(buffer)) != null) {
			out.printLabel(HexFormat.LOWER_CASE.format(ip)).printLabel(":").print(" ");
			instruction.decode(ip, buffer, out);
			ip = (int) buffer.getTotalRead();
		}
	}

	@SuppressWarnings("resource")
	private static InstructionIndex getInstructionIndex() throws IOException {
		InstructionIndex instructionIndex;

		synchronized (X86B32_INSTRUCTION_INDEX_HOLDER) {
			Optional<InstructionIndex> instructionIndexHolder = X86B32_INSTRUCTION_INDEX_HOLDER.getOptional();

			if (instructionIndexHolder.isPresent()) {
				instructionIndex = instructionIndexHolder.get();
			} else {
				instructionIndex = X86B32_INSTRUCTION_INDEX_HOLDER.set(X86b16InstructionIndex.open());
			}
		}
		return instructionIndex;
	}

}
