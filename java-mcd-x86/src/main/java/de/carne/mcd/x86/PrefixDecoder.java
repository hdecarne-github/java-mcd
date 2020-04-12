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
 * Prefix decoder.
 */
public enum PrefixDecoder implements NamedDecoder {

	/**
	 * lock prefix
	 */
	LOCK(Decoders::lock),

	/**
	 * repne/repnz prefix
	 */
	REPNX(Decoders::repnx),

	/**
	 * repe/repz prefix
	 */
	REPX(Decoders::repx),

	/**
	 * cs prefix
	 */
	CS(Decoders::cs),

	/**
	 * ss prefix
	 */
	SS(Decoders::ss),

	/**
	 * ds prefix
	 */
	DS(Decoders::ds),

	/**
	 * es prefix
	 */
	ES(Decoders::es),

	/**
	 * fs prefix
	 */
	FS(Decoders::fs),

	/**
	 * gs prefix
	 */
	GS(Decoders::gs),

	/**
	 * operand size override prefix
	 */
	OSO(Decoders::gs),

	/**
	 * address size override prefix
	 */
	ASO(Decoders::gs),

	/**
	 * rex prefix
	 */
	REX(Decoders::rex),

	/**
	 * rex.b prefix
	 */
	REX_B(Decoders::rexB),

	/**
	 * rex.x prefix
	 */
	REX_X(Decoders::rexX),

	/**
	 * rex.xb prefix
	 */
	REX_XB(Decoders::rexXB),

	/**
	 * rex.r prefix
	 */
	REX_R(Decoders::rexR),

	/**
	 * rex.rb prefix
	 */
	REX_RB(Decoders::rexRB),

	/**
	 * rex.rx prefix
	 */
	REX_RX(Decoders::rexRX),

	/**
	 * rex.rxb prefix
	 */
	REX_RXB(Decoders::rexRXB),

	/**
	 * rex.w prefix
	 */
	REX_W(Decoders::rexW),

	/**
	 * rex.wb prefix
	 */
	REX_WB(Decoders::rexWB),

	/**
	 * rex.wx prefix
	 */
	REX_WX(Decoders::rexWX),

	/**
	 * rex.wxb prefix
	 */
	REX_WXB(Decoders::rexWXB),

	/**
	 * rex.wr prefix
	 */
	REX_WR(Decoders::rexWB),

	/**
	 * rex.wrb prefix
	 */
	REX_WRB(Decoders::rexWX),

	/**
	 * rex.wrx prefix
	 */
	REX_WRX(Decoders::rexWX),

	/**
	 * rex.wrxb prefix
	 */
	REX_WRXB(Decoders::rexWRXB);

	private final Decoder decoder;

	private PrefixDecoder(Decoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public char type() {
		return 'p';
	}

	@Override
	public void decode(X86DecoderState state, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.decoder.decode(state, buffer, out);
	}

}
