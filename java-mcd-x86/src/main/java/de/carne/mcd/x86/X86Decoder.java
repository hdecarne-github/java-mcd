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
import java.nio.ByteOrder;

import de.carne.boot.logging.Log;
import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.common.instruction.InstructionIndex;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;

/**
 * Common base class for all x86 machine code decoders.
 */
public abstract class X86Decoder extends MachineCodeDecoder {

	private static final Log LOG = new Log();

	protected X86Decoder(String name) {
		super(name, ByteOrder.LITTLE_ENDIAN);
	}

	@Override
	protected long decode0(MCDInputBuffer in, MCDOutputBuffer out, long offset, long limit) throws IOException {
		InstructionIndex instructionIndex = getInstructionIndex();
		InstructionIndex.LookupResult lookupResult;
		long ip = offset;
		long ipLimit = offset + limit;

		in.setAutoCommit(false);
		out.setAutoCommit(false);
		while (ip <= ipLimit && (lookupResult = instructionIndex.lookupNextInstruction(in, true)) != null) {
			String ipString = formatInstructionPointer(ip) + ":";

			out.printLabel(ipString).print(" ");
			try {
				lookupResult.decode(ip, in, out);
			} catch (IOException e) {
				String opcodeString = lookupResult.opcode().toString();

				LOG.warning(e, "Decode failure at {0} for opcode: {1}", ipString, opcodeString);

				out.printlnError(opcodeString);
			}
			in.commit();
			out.commit();
			ip = offset + in.getTotalRead();
		}
		return in.getTotalRead();
	}

	protected abstract InstructionIndex getInstructionIndex() throws IOException;

	protected abstract String formatInstructionPointer(long ip);

}
