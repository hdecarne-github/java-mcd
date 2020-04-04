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
public enum ImmediateOperandType implements OperandType {

	REL8(OperandDecoders::rel8),

	REL16(OperandDecoders::rel16),

	REL32(OperandDecoders::rel32),

	IMM8(OperandDecoders::imm8),

	IMM16(OperandDecoders::imm16),

	IMM32(OperandDecoders::imm32),

	IMM64(OperandDecoders::imm64);

	private final OperandDecoder decoder;

	private ImmediateOperandType(OperandDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'i';
	}

	@Override
	public void decode(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.decoder.decode(ip, modrmByte, buffer, out);
	}

}
