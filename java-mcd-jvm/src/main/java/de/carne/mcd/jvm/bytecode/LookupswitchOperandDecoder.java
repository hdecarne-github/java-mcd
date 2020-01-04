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
package de.carne.mcd.jvm.bytecode;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;
import de.carne.text.HexFormat;

/**
 *
 */
public class LookupswitchOperandDecoder implements OperandDecoder {

	@Override
	public char type() {
		return 'l';
	}

	@Override
	public String name() {
		return getClass().getSimpleName();
	}

	@Override
	public void decode(int pc, MCDDecodeBuffer buffer, MCDOutput out) throws IOException {
		int basePc = pc + 1;

		buffer.skip(((basePc + 0x3) & ~0x3) - Integer.toUnsignedLong(basePc));

		int lsDefault = buffer.decodeI32();
		int lsNPairs = buffer.decodeI32();
		ByteBuffer pairs = buffer.decodeI32Array(lsNPairs * 2);

		out.printComment("//");
		while (pairs.hasRemaining()) {
			int match = pairs.getInt();
			int offset = pairs.getInt();

			out.printComment(" ").printComment(Integer.toString(match)).printComment(":")
					.printComment(HexFormat.LOWER_CASE.format((short) (pc + offset)));
		}
		out.printComment(" default:").printComment(HexFormat.LOWER_CASE.format((short) (pc + lsDefault)));
	}

}
