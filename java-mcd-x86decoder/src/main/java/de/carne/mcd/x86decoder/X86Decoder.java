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
package de.carne.mcd.x86decoder;

import java.io.IOException;
import java.nio.ByteOrder;

import de.carne.mcd.MachineCodeDecoder;
import de.carne.mcd.instruction.InstructionIndex;
import de.carne.mcd.instruction.InstructionIndex.LookupResult;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;
import de.carne.util.Exceptions;

/**
 * Common base class for all x86 machine code decoders.
 */
public abstract class X86Decoder extends MachineCodeDecoder {

	private static final long DECODE_LIMIT = 0x1000;

	private final X86DecoderState decoderState;

	protected X86Decoder(String name, X86DecoderState decoderState) {
		super(name, ByteOrder.LITTLE_ENDIAN, DECODE_LIMIT);
		this.decoderState = decoderState;
	}

	/**
	 * Gets this {@linkplain X86Decoder} instance's current state.
	 *
	 * @return this {@linkplain X86Decoder} instance's current state.
	 */
	public X86DecoderState state() {
		return this.decoderState;
	}

	@Override
	protected long decode0(MCDInputBuffer in, MCDOutputBuffer out, long offset, long limit) throws IOException {
		InstructionIndex instructionIndex = getInstructionIndex();
		InstructionIndex.LookupResult lookupResult;
		long instructionPointerBase = offset - in.getTotalRead();
		long instructionPointerLimit = offset + limit;
		long instructionPointer;

		in.setAutoCommit(false);
		out.setAutoCommit(false);
		while ((instructionPointer = this.decoderState.reset(instructionPointerBase,
				in.getTotalRead())) < instructionPointerLimit
				&& (lookupResult = instructionIndex.lookupNextInstruction(in, true)) != null) {
			String ipString = this.decoderState.addressFormat().apply(instructionPointer) + ":";

			out.printLabel(ipString).print(" ");
			out.commit();
			try {
				lookupResult.decode(instructionPointer, in, out);

				LookupResult lastLookupResult = lookupResult;

				while (X86InstructionOpcodes.isPrefix(lastLookupResult.opcode())) {
					lastLookupResult = instructionIndex.lookupNextInstruction(in, true);
					if (lastLookupResult == null) {
						throw new IOException();
					}
					lastLookupResult.decode(instructionPointer, in, out);
				}
			} catch (IOException e) {
				Exceptions.ignore(e);

				InstructionOpcode unknownOpcode = lookupResult.opcode();

				in.discard(unknownOpcode.length());
				out.discard();
				UnknownX86Instruction.decode(unknownOpcode, out);
			}
			in.commit();
			out.commit();
		}
		return in.getTotalRead();
	}

	protected abstract InstructionIndex getInstructionIndex() throws IOException;

}
