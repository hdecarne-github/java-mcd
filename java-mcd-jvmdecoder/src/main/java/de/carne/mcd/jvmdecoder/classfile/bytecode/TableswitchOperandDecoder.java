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
package de.carne.mcd.jvmdecoder.classfile.bytecode;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;
import de.carne.text.HexFormat;

/**
 *
 */
public class TableswitchOperandDecoder implements OperandType {

	@Override
	public char type() {
		return 't';
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@Override
	public void decode(int pc, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		int basePc = pc + 1;

		buffer.decodeI8Array(((basePc + 0x3) & ~0x3) - basePc);

		int tsDefault = buffer.decodeI32();
		int tsLow = buffer.decodeI32();
		int tsHigh = buffer.decodeI32();
		ByteBuffer offsets = buffer.decodeI32Array(tsHigh - tsLow + 1);

		out.printComment("//");

		int index = tsLow;

		while (offsets.hasRemaining()) {
			int offset = offsets.getInt();

			out.printComment(" ").printComment(Integer.toString(index)).printComment(":")
					.printComment(HexFormat.LOWER_CASE.format((short) (pc + offset)));
			index++;
		}
		out.printComment(" default:").printComment(HexFormat.LOWER_CASE.format((short) (pc + tsDefault)));
	}

}
