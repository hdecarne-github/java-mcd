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

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * ModR/M operand decoder.
 */
public enum ModRMDecoder implements NamedDecoder {

	/**
	 * r8
	 */
	R8(Decoders::r8),

	/**
	 * r16
	 */
	R16(Decoders::r16),

	/**
	 * r32
	 */
	R32(Decoders::r32),

	/**
	 * r64
	 */
	R64(Decoders::r64),

	/**
	 * rm8
	 */
	RM8(Decoders::rm8),

	/**
	 * rm16
	 */
	RM16(Decoders::rm16),

	/**
	 * rm32
	 */
	RM32(Decoders::rm32),

	/**
	 * rm64
	 */
	RM64(Decoders::rm64);

	private final Decoder decoder;

	private ModRMDecoder(Decoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'm';
	}

	@Override
	public void decode(X86DecoderState decoderState, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.decoder.decode(decoderState, buffer, out);
	}

}
