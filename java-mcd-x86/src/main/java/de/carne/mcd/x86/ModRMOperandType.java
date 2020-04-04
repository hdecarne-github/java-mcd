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

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 *
 */
public enum ModRMOperandType implements OperandType {

	R8(OperandDecoders::r8),

	R16(OperandDecoders::r16),

	R32(OperandDecoders::r32),

	R64(OperandDecoders::r64),

	RM8(OperandDecoders::rm8b16),

	RM16(OperandDecoders::rm16b16),

	RM32(OperandDecoders::rm32b16),

	RM64(OperandDecoders::rm64b16);

	private final OperandDecoder decoder;

	private ModRMOperandType(OperandDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'm';
	}

	@Override
	public void decode(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.decoder.decode(ip, modrmByte, buffer, out);
	}

}
