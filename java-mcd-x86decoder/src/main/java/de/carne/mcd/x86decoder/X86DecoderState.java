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
import java.util.function.LongFunction;

import org.eclipse.jdt.annotation.NonNull;

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;
import de.carne.text.HexFormat;

/**
 * The decoder state.
 */
public abstract class X86DecoderState {

	private static final @NonNull String[] R8_REGS = { "al", "cl", "dl", "bl", "ah", "ch", "dh", "bh" };
	private static final @NonNull String[] R16_REGS = { "ax", "cx", "dx", "bx", "sp", "bp", "si", "di" };
	private static final @NonNull String[] R32_REGS = { "eax", "ecx", "edx", "ebx", "esp", "ebp", "esi", "edi" };
	private static final @NonNull String[] R64_0_REGS = { "rax", "rcx", "rdx", "rbx", "rsp", "rbp", "rsi", "rdi" };
	private static final @NonNull String[] R64_1_REGS = { "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15" };

	private final LongFunction<String> addressFormat;

	private long currentInstructionPointerBase;
	private long currentInstructionPointerOffset;
	private boolean lock;
	private boolean repnX;
	private boolean repX;
	private int segmentOverride;
	private boolean operandSizeOverride;
	private boolean addressSizeOverride;
	protected Rex rex = Rex.NOT_PRESENT;
	private ModRM modRM = ModRM.NOT_PRESENT;

	protected X86DecoderState(LongFunction<String> addressFormat) {
		this.addressFormat = addressFormat;
		reset(-1l, 0l);
	}

	static X86DecoderState x86b16() {
		return new X86DecoderState(a -> HexFormat.LOWER_CASE.format((short) a)) {

			@Override
			void m(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				m32(in, out);
			}

			@Override
			void rm8(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				rm8b16(in, out);
			}

		};
	}

	static X86DecoderState x86b32() {
		return new X86DecoderState(a -> HexFormat.LOWER_CASE.format((int) a)) {

			@Override
			void m(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				m32(in, out);
			}

			@Override
			void rm8(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				rm8b32(in, out);
			}

		};
	}

	static X86DecoderState x86b64() {
		return new X86DecoderState(HexFormat.LOWER_CASE::format) {

			@Override
			void m(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				if (this.rex.isPresent()) {
					m64(in, out);
				} else {
					m32(in, out);
				}
			}

			@Override
			void rm8(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				rm8b32(in, out);
			}

			@Override
			void r32(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				if (this.rex.isW()) {
					super.r64(in, out);
				} else {
					super.r32(in, out);
				}
			}

			@Override
			void rm32(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
				if (this.rex.isW()) {
					super.rm64(in, out);
				} else {
					super.rm32(in, out);
				}
			}

		};
	}

	@SuppressWarnings("unused")
	void r8(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.printKeyword(R8_REGS[this.modRM.regOrOpcodeIndex()]);
	}

	@SuppressWarnings("unused")
	void r16(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.printKeyword(R16_REGS[this.modRM.regOrOpcodeIndex()]);
	}

	@SuppressWarnings("unused")
	void r32(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.printKeyword(R32_REGS[this.modRM.regOrOpcodeIndex()]);
	}

	@SuppressWarnings("unused")
	void r64(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.printKeyword((this.rex.isB() ? R64_1_REGS : R64_0_REGS)[this.modRM.regOrOpcodeIndex()]);
	}

	abstract void m(MCDInputBuffer in, MCDOutputBuffer out) throws IOException;

	protected void m32(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword("ebp");
		Decoders.disp32(this, in, out);
		out.print("]");
	}

	protected void m64(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword("rip");
		Decoders.disp32(this, in, out);
		out.print("]");
	}

	abstract void rm8(MCDInputBuffer in, MCDOutputBuffer out) throws IOException;

	protected void rm8b16(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		switch (this.modRM.modRMIndex()) {
		case 0b00000:
			rmXReg("bx", "si", out);
			break;
		case 0b00001:
			rmXReg("bx", "di", out);
			break;
		case 0b00010:
			rmXReg("bp", "si", out);
			break;
		case 0b00011:
			rmXReg("bp", "di", out);
			break;
		case 0b00100:
			rmXReg("si", out);
			break;
		case 0b00101:
			rmXReg("di", out);
			break;
		case 0b00110:
			rmXDisp16(in, out);
			break;
		case 0b00111:
			rmXReg("bx", out);
			break;
		case 0b01000:
			rmXRegDisp8("bx", "si", in, out);
			break;
		case 0b01001:
			rmXRegDisp8("bx", "di", in, out);
			break;
		case 0b01010:
			rmXRegDisp8("bp", "si", in, out);
			break;
		case 0b01011:
			rmXRegDisp8("bp", "di", in, out);
			break;
		case 0b01100:
			rmXRegDisp8("si", in, out);
			break;
		case 0b01101:
			rmXRegDisp8("di", in, out);
			break;
		case 0b01110:
			rmXRegDisp8("bp", in, out);
			break;
		case 0b01111:
			rmXRegDisp8("bx", in, out);
			break;
		case 0b10000:
			rmXRegDisp16("bx", "si", in, out);
			break;
		case 0b10001:
			rmXRegDisp16("bx", "di", in, out);
			break;
		case 0b10010:
			rmXRegDisp16("bp", "si", in, out);
			break;
		case 0b10011:
			rmXRegDisp16("bp", "di", in, out);
			break;
		case 0b10100:
			rmXRegDisp16("si", in, out);
			break;
		case 0b10101:
			rmXRegDisp16("di", in, out);
			break;
		case 0b10110:
			rmXRegDisp16("bp", in, out);
			break;
		case 0b10111:
			rmXRegDisp16("bx", in, out);
			break;
		// case 0b11...:
		default:
			out.printKeyword(R8_REGS[this.modRM.rmIndex()]);
		}
	}

	protected void rm8b32(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		switch (this.modRM.modRMIndex()) {
		case 0b00000:
			rmXReg("eax", out);
			break;
		case 0b00001:
			rmXReg("ecx", out);
			break;
		case 0b00010:
			rmXReg("edx", out);
			break;
		case 0b00011:
			rmXReg("ebx", out);
			break;
		case 0b00100:
			rmXSib(R8_REGS, Decoders::disp32, false, in, out);
			break;
		case 0b00101:
			rmXDisp32(in, out);
			break;
		case 0b00110:
			rmXReg("esi", out);
			break;
		case 0b00111:
			rmXReg("edi", out);
			break;
		case 0b01000:
			rmXRegDisp8("eax", in, out);
			break;
		case 0b01001:
			rmXRegDisp8("ecx", in, out);
			break;
		case 0b01010:
			rmXRegDisp8("edx", in, out);
			break;
		case 0b01011:
			rmXRegDisp8("ebx", in, out);
			break;
		case 0b01100:
			rmXSib(R8_REGS, Decoders::disp8, true, in, out);
			break;
		case 0b01101:
			rmXRegDisp8("ebp", in, out);
			break;
		case 0b01110:
			rmXRegDisp8("esi", in, out);
			break;
		case 0b01111:
			rmXRegDisp8("edi", in, out);
			break;
		case 0b10000:
			rmXRegDisp32("eax", in, out);
			break;
		case 0b10001:
			rmXRegDisp32("ecx", in, out);
			break;
		case 0b10010:
			rmXRegDisp32("eax", in, out);
			break;
		case 0b10011:
			rmXRegDisp32("ebx", in, out);
			break;
		case 0b10100:
			rmXSib(R8_REGS, Decoders::disp32, true, in, out);
			break;
		case 0b10101:
			rmXRegDisp32("ebp", in, out);
			break;
		case 0b10110:
			rmXRegDisp32("esi", in, out);
			break;
		case 0b10111:
			rmXRegDisp32("edi", in, out);
			break;
		// case 0b11...:
		default:
			out.printKeyword(R8_REGS[this.modRM.rmIndex()]);
		}
	}

	void rm16(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		switch (this.modRM.modRMIndex()) {
		case 0b00000:
			rmXReg("bx", "si", out);
			break;
		case 0b00001:
			rmXReg("bx", "di", out);
			break;
		case 0b00010:
			rmXReg("bp", "si", out);
			break;
		case 0b00011:
			rmXReg("bp", "di", out);
			break;
		case 0b00100:
			rmXReg("si", out);
			break;
		case 0b00101:
			rmXReg("di", out);
			break;
		case 0b00110:
			rmXDisp16(in, out);
			break;
		case 0b00111:
			rmXReg("bx", out);
			break;
		case 0b01000:
			rmXRegDisp8("bx", "si", in, out);
			break;
		case 0b01001:
			rmXRegDisp8("bx", "di", in, out);
			break;
		case 0b01010:
			rmXRegDisp8("bp", "si", in, out);
			break;
		case 0b01011:
			rmXRegDisp8("bp", "di", in, out);
			break;
		case 0b01100:
			rmXRegDisp8("si", in, out);
			break;
		case 0b01101:
			rmXRegDisp8("di", in, out);
			break;
		case 0b01110:
			rmXRegDisp8("bp", in, out);
			break;
		case 0b01111:
			rmXRegDisp8("bx", in, out);
			break;
		case 0b10000:
			rmXRegDisp16("bx", "si", in, out);
			break;
		case 0b10001:
			rmXRegDisp16("bx", "di", in, out);
			break;
		case 0b10010:
			rmXRegDisp16("bp", "si", in, out);
			break;
		case 0b10011:
			rmXRegDisp16("bp", "di", in, out);
			break;
		case 0b10100:
			rmXRegDisp16("si", in, out);
			break;
		case 0b10101:
			rmXRegDisp16("di", in, out);
			break;
		case 0b10110:
			rmXRegDisp16("bp", in, out);
			break;
		case 0b10111:
			rmXRegDisp16("bx", in, out);
			break;
		// case 0b11...:
		default:
			out.printKeyword(R16_REGS[this.modRM.rmIndex()]);
		}
	}

	void rm32(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		switch (this.modRM.modRMIndex()) {
		case 0b00000:
			rmXReg("eax", out);
			break;
		case 0b00001:
			rmXReg("ecx", out);
			break;
		case 0b00010:
			rmXReg("edx", out);
			break;
		case 0b00011:
			rmXReg("ebx", out);
			break;
		case 0b00100:
			rmXSib(R32_REGS, Decoders::disp32, false, in, out);
			break;
		case 0b00101:
			rmXDisp32(in, out);
			break;
		case 0b00110:
			rmXReg("esi", out);
			break;
		case 0b00111:
			rmXReg("edi", out);
			break;
		case 0b01000:
			rmXRegDisp8("eax", in, out);
			break;
		case 0b01001:
			rmXRegDisp8("ecx", in, out);
			break;
		case 0b01010:
			rmXRegDisp8("edx", in, out);
			break;
		case 0b01011:
			rmXRegDisp8("ebx", in, out);
			break;
		case 0b01100:
			rmXSib(R32_REGS, Decoders::disp8, true, in, out);
			break;
		case 0b01101:
			rmXRegDisp8("ebp", in, out);
			break;
		case 0b01110:
			rmXRegDisp8("esi", in, out);
			break;
		case 0b01111:
			rmXRegDisp8("edi", in, out);
			break;
		case 0b10000:
			rmXRegDisp32("eax", in, out);
			break;
		case 0b10001:
			rmXRegDisp32("ecx", in, out);
			break;
		case 0b10010:
			rmXRegDisp32("eax", in, out);
			break;
		case 0b10011:
			rmXRegDisp32("ebx", in, out);
			break;
		case 0b10100:
			rmXSib(R32_REGS, Decoders::disp32, true, in, out);
			break;
		case 0b10101:
			rmXRegDisp32("ebp", in, out);
			break;
		case 0b10110:
			rmXRegDisp32("esi", in, out);
			break;
		case 0b10111:
			rmXRegDisp32("edi", in, out);
			break;
		// case 0b11...:
		default:
			out.printKeyword(R32_REGS[this.modRM.rmIndex()]);
		}
	}

	void rm64(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		@NonNull String[] regs = (this.rex.isB() ? R64_1_REGS : R64_0_REGS);

		switch (this.modRM.modRMIndex()) {
		case 0b00000:
			rmXReg(regs[0], out);
			break;
		case 0b00001:
			rmXReg(regs[1], out);
			break;
		case 0b00010:
			rmXReg(regs[2], out);
			break;
		case 0b00011:
			rmXReg(regs[3], out);
			break;
		case 0b00100:
			rmXSib(regs, Decoders::disp32, false, in, out);
			break;
		case 0b00101:
			rmXDisp32(in, out);
			break;
		case 0b00110:
			rmXReg(regs[5], out);
			break;
		case 0b00111:
			rmXReg(regs[7], out);
			break;
		case 0b01000:
			rmXRegDisp8("rax", in, out);
			break;
		case 0b01001:
			rmXRegDisp8("rcx", in, out);
			break;
		case 0b01010:
			rmXRegDisp8("rdx", in, out);
			break;
		case 0b01011:
			rmXRegDisp8("rbx", in, out);
			break;
		case 0b01100:
			rmXSib(regs, Decoders::disp8, true, in, out);
			break;
		case 0b01101:
			rmXRegDisp8("rbp", in, out);
			break;
		case 0b01110:
			rmXRegDisp8("rsi", in, out);
			break;
		case 0b01111:
			rmXRegDisp8("rdi", in, out);
			break;
		case 0b10000:
			rmXRegDisp32("rax", in, out);
			break;
		case 0b10001:
			rmXRegDisp32("rcx", in, out);
			break;
		case 0b10010:
			rmXRegDisp32("rax", in, out);
			break;
		case 0b10011:
			rmXRegDisp32("rbx", in, out);
			break;
		case 0b10100:
			rmXSib(regs, Decoders::disp32, true, in, out);
			break;
		case 0b10101:
			rmXRegDisp32("rbp", in, out);
			break;
		case 0b10110:
			rmXRegDisp32("rsi", in, out);
			break;
		case 0b10111:
			rmXRegDisp32("rdi", in, out);
			break;
		// case 0b11...:
		default:
			out.printKeyword((this.rex.isB() ? R64_1_REGS : R64_0_REGS)[this.modRM.rmIndex()]);
		}
	}

	private void rmXReg(String reg, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword(reg).print("]");
	}

	private void rmXReg(String reg1, String reg2, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword(reg1).printOperator("+").print(reg2).print("]");
	}

	private void rmXRegDisp8(String reg, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword(reg);
		Decoders.disp8(this, in, out);
		out.print("]");
	}

	private void rmXRegDisp8(String reg1, String reg2, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword(reg1).printOperator("+").printKeyword(reg2);
		Decoders.disp8(this, in, out);
		out.print("]");
	}

	private void rmXDisp16(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[");
		out.print("0x" + this.addressFormat.apply(Short.toUnsignedLong(in.decodeI16())));
		out.print("]");
	}

	private void rmXDisp32(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[");
		out.print("0x" + this.addressFormat.apply(Integer.toUnsignedLong(in.decodeI32())));
		out.print("]");
	}

	private void rmXRegDisp16(String reg, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword(reg);
		Decoders.disp16(this, in, out);
		out.print("]");
	}

	private void rmXRegDisp16(String reg1, String reg2, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword(reg1).printOperator("+").printKeyword(reg2);
		Decoders.disp16(this, in, out);
		out.print("]");
	}

	private void rmXRegDisp32(String reg, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		out.print("[").printKeyword(reg);
		Decoders.disp32(this, in, out);
		out.print("]");
	}

	private void rmXSib(@NonNull String[] regs, Decoder dispDecoder, boolean disp, MCDInputBuffer in,
			MCDOutputBuffer out) throws IOException {
		int sib = Byte.toUnsignedInt(in.decodeI8());
		int sibBase = sib & 0b111;
		int sibSS = (sib >> 6) & 0xb11;
		int sibIndex = (sib >> 3) & 0b111;

		out.print("[");
		if (disp) {
			out.printKeyword(regs[sibBase]);
			dispDecoder.decode(this, in, out);
		} else if (sibBase != 0b101) {
			out.printKeyword(regs[sibBase]);
		} else {
			dispDecoder.decode(this, in, out);
		}
		if (sibIndex != 0b100) {
			out.printOperator("+").printKeyword(regs[sibIndex]);
			switch (sibSS) {
			case 1:
				out.printOperator("*").printValue("2");
				break;
			case 2:
				out.printOperator("*").printValue("4");
				break;
			case 3:
				out.printOperator("*").printValue("8");
				break;
			default:
				// Nothing to do here
			}
		}
		out.print("]");
	}

	long reset(long instructionPointerBase, long instructionPointerOffset) {
		this.currentInstructionPointerBase = instructionPointerBase;
		this.currentInstructionPointerOffset = instructionPointerOffset;
		this.lock = false;
		this.repnX = false;
		this.repX = false;
		this.segmentOverride = -1;
		this.operandSizeOverride = false;
		this.addressSizeOverride = false;
		this.rex = Rex.NOT_PRESENT;
		this.modRM = ModRM.NOT_PRESENT;
		return currentInstructionPointer();
	}

	long currentInstructionPointer() {
		return this.currentInstructionPointerBase + this.currentInstructionPointerOffset;
	}

	long nextInstructionPointer(long instructionPointerOffset) {
		return this.currentInstructionPointerBase + instructionPointerOffset;
	}

	LongFunction<String> addressFormat() {
		return this.addressFormat;
	}

	void setLock() {
		this.lock = true;
	}

	boolean isLock() {
		return this.lock;
	}

	void setRepnX() {
		this.repnX = true;
	}

	boolean isRepnX() {
		return this.repnX;
	}

	void setRepX() {
		this.repnX = true;
	}

	boolean isRepX() {
		return this.repX;
	}

	int setSegementOverride(int segmentOverride) {
		this.segmentOverride = segmentOverride;
		return this.segmentOverride;
	}

	int getSegementOverride() {
		return this.segmentOverride;
	}

	void setOperandSizeOverride() {
		this.operandSizeOverride = true;
	}

	boolean isOperandSizeOverride() {
		return this.operandSizeOverride;
	}

	void setAddressSizeOverride() {
		this.addressSizeOverride = true;
	}

	boolean isAddressSizeOverride() {
		return this.addressSizeOverride;
	}

	Rex setRex(Rex rex) {
		this.rex = rex;
		return this.rex;
	}

	Rex rex() {
		return this.rex;
	}

	ModRM setModRM(byte modRM) {
		this.modRM = new ModRM(Byte.toUnsignedInt(modRM));
		return this.modRM;
	}

	ModRM modRM() {
		return this.modRM;
	}

}
