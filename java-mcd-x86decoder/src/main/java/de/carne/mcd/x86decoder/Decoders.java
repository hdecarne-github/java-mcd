/*
 * Copyright (c) 2019-2022 Holger de Carne and contributors, All Rights Reserved.
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
import de.carne.text.HexFormat;

final class Decoders {

	private Decoders() {
		// Prevent instantiation
	}

	@SuppressWarnings("unused")
	static void lock(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setLock();
	}

	@SuppressWarnings("unused")
	static void repnx(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRepnX();
	}

	@SuppressWarnings("unused")
	static void repx(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRepX();
	}

	@SuppressWarnings("unused")
	static void cs(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setSegementOverride(0);
	}

	@SuppressWarnings("unused")
	static void ss(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setSegementOverride(1);
	}

	@SuppressWarnings("unused")
	static void ds(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setSegementOverride(2);
	}

	@SuppressWarnings("unused")
	static void es(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setSegementOverride(3);
	}

	@SuppressWarnings("unused")
	static void fs(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setSegementOverride(4);
	}

	@SuppressWarnings("unused")
	static void gs(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setSegementOverride(5);
	}

	@SuppressWarnings("unused")
	static void oso(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setOperandSizeOverride();
	}

	@SuppressWarnings("unused")
	static void aso(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setAddressSizeOverride();
	}

	@SuppressWarnings("unused")
	static void rex(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX);
	}

	@SuppressWarnings("unused")
	static void rexB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_B);
	}

	@SuppressWarnings("unused")
	static void rexX(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_X);
	}

	@SuppressWarnings("unused")
	static void rexXB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_XB);
	}

	@SuppressWarnings("unused")
	static void rexR(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_R);
	}

	@SuppressWarnings("unused")
	static void rexRB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_RB);
	}

	@SuppressWarnings("unused")
	static void rexRX(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_RX);
	}

	@SuppressWarnings("unused")
	static void rexRXB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_RXB);
	}

	@SuppressWarnings("unused")
	static void rexW(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_W);
	}

	@SuppressWarnings("unused")
	static void rexWB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_WB);
	}

	@SuppressWarnings("unused")
	static void rexWX(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_WX);
	}

	@SuppressWarnings("unused")
	static void rexWXB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_WXB);
	}

	@SuppressWarnings("unused")
	static void rexWR(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_WR);
	}

	@SuppressWarnings("unused")
	static void rexWRB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_WRB);
	}

	@SuppressWarnings("unused")
	static void rexWRX(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_WRX);
	}

	@SuppressWarnings("unused")
	static void rexWRXB(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) {
		decoderState.setRex(Rex.REX_WRXB);
	}

	@SuppressWarnings("unused")
	static void imm8(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		byte value = in.decodeI8();

		out.printValue("0x" + HexFormat.LOWER_CASE.format(value));
	}

	@SuppressWarnings("unused")
	static void imm16(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		short value = in.decodeI16();

		out.printValue("0x" + HexFormat.LOWER_CASE.format(value));
	}

	@SuppressWarnings("unused")
	static void imm32(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		int value = in.decodeI32();

		out.printValue("0x" + HexFormat.LOWER_CASE.format(value));
	}

	@SuppressWarnings("unused")
	static void imm64(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		long value = in.decodeI64();

		out.printValue("0x" + HexFormat.LOWER_CASE.format(value));
	}

	@SuppressWarnings("unused")
	static void disp8(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		byte value = in.decodeI8();

		out.printOperator(value >= 0 ? "+" : "-");
		out.printValue(Integer.toString(Math.abs(value)));
	}

	@SuppressWarnings("unused")
	static void disp16(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		short value = in.decodeI16();

		out.printOperator(value >= 0 ? "+" : "-");
		out.printValue(Integer.toString(Math.abs(value)));
	}

	@SuppressWarnings("unused")
	static void disp32(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		int value = in.decodeI32();

		out.printOperator(value >= 0 ? "+" : "-");
		out.printValue(Integer.toString(Math.abs(value)));
	}

	static void m(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.m(in, out);
	}

	static void moffs8(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		byte value = in.decodeI8();

		out.print("[").printValue("0x").printValue(Long.toHexString(value)).print("]");
	}

	static void moffs16(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		short value = in.decodeI16();

		out.print("[").printValue("0x").printValue(Long.toHexString(value)).print("]");
	}

	static void moffs32(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		int value = in.decodeI32();

		out.print("[").printValue("0x").printValue(Long.toHexString(value)).print("]");
	}

	static void moffs64(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		long value = in.decodeI64();

		out.print("[").printValue("0x").printValue(Long.toHexString(value)).print("]");
	}

	static void rel8(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		long rel = in.decodeI8();
		long nextInstructionPointer = decoderState.nextInstructionPointer(in.getTotalRead()) + rel;

		out.printValue(rel >= 0 ? "+" : "").printValue(Long.toString(rel)).print(" ").printComment("; ")
				.printComment(decoderState.addressFormat().apply(nextInstructionPointer));
	}

	static void rel16(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		long rel = in.decodeI16();
		long nextInstructionPointer = decoderState.nextInstructionPointer(in.getTotalRead()) + rel;

		out.printValue(rel >= 0 ? "+" : "").printValue(Long.toString(rel)).print(" ").printComment("; ")
				.printComment(decoderState.addressFormat().apply(nextInstructionPointer));
	}

	static void rel32(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		long rel = Integer.toUnsignedLong(in.decodeI32());
		long nextInstructionPointer = decoderState.nextInstructionPointer(in.getTotalRead()) + rel;

		out.printValue(rel >= 0 ? "+" : "").printValue(Long.toString(rel)).print(" ").printComment("; ")
				.printComment(decoderState.addressFormat().apply(nextInstructionPointer));
	}

	static void r8(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.r8(in, out);
	}

	static void r16(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.r16(in, out);
	}

	static void r32(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.r32(in, out);
	}

	static void r64(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.r64(in, out);
	}

	static void rm8(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.rm8(in, out);
	}

	static void rm16(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.rm16(in, out);
	}

	static void rm32(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.rm32(in, out);
	}

	static void rm64(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		decoderState.rm64(in, out);
	}

}
