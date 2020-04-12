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
 * Immediate operand decoder.
 */
public enum ImmediateDecoder implements NamedDecoder {

	/**
	 * rel8
	 */
	REL8(Decoders::rel8),

	/**
	 * rel16
	 */
	REL16(Decoders::rel16),

	/**
	 * rel32
	 */
	REL32(Decoders::rel32),

	/**
	 * imm8
	 */
	IMM8(Decoders::imm8),

	/**
	 * imm16
	 */
	IMM16(Decoders::imm16),

	/**
	 * imm32
	 */
	IMM32(Decoders::imm32),

	/**
	 * imm64
	 */
	IMM64(Decoders::imm64),

	/**
	 * m
	 */
	M(Decoders::m),

	/**
	 * moffs8
	 */
	MOFFS8(Decoders::moffs8),

	/**
	 * moffs16
	 */
	MOFFS16(Decoders::moffs16),

	/**
	 * moffs32
	 */
	MOFFS32(Decoders::moffs32),

	/**
	 * moffs64
	 */
	MOFFS64(Decoders::moffs64);

	private final Decoder decoder;

	private ImmediateDecoder(Decoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'i';
	}

	@Override
	public void decode(X86DecoderState state, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.decoder.decode(state, buffer, out);
	}

}
