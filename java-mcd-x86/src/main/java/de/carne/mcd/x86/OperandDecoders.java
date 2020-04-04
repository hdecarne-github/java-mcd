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

import org.eclipse.jdt.annotation.NonNull;

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;
import de.carne.text.HexFormat;

final class OperandDecoders {

	private OperandDecoders() {
		// Prevent instantiation
	}

	public static void imm8(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		byte value = buffer.decodeI8();

		out.printValue("0x").printValue(HexFormat.LOWER_CASE.format(value));
	}

	public static void imm16(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		short value = buffer.decodeI16();

		out.printValue("0x").printValue(HexFormat.LOWER_CASE.format(value));
	}

	public static void imm32(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		int value = buffer.decodeI32();

		out.printValue("0x").printValue(HexFormat.LOWER_CASE.format(value));
	}

	public static void imm64(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		long value = buffer.decodeI64();

		out.printValue("0x").printValue(HexFormat.LOWER_CASE.format(value));
	}

	public static void rel8(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		byte value = buffer.decodeI8();

		out.printValue(value >= 0 ? "+" : "").printValue(Byte.toString(value)).print(" ").printComment("; ")
				.printComment(Long.toHexString(ip + value));
	}

	public static void rel16(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		short value = buffer.decodeI16();

		out.printValue(value >= 0 ? "+" : "").printValue(Short.toString(value)).print(" ").printComment("; ")
				.printComment(Long.toHexString(ip + value));
	}

	public static void rel32(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		int value = buffer.decodeI32();

		out.printValue(value >= 0 ? "+" : "").printValue(Integer.toString(value)).print(" ").printComment("; ")
				.printComment(Long.toHexString(ip + value));
	}

	private static final @NonNull String[] R8_REGS = { "al", "cl", "dl", "bl", "ah", "ch", "dh", "bh" };

	public static void r8(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		out.printKeyword(R8_REGS[(modrmByte >> 3) & 0x7]);
	}

	private static final @NonNull String[] R16_REGS = { "ax", "cx", "dx", "bx", "sp", "bp", "si", "di" };

	public static void r16(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		out.printKeyword(R16_REGS[(modrmByte >> 3) & 0x7]);
	}

	private static final @NonNull String[] R32_REGS = { "eax", "ecx", "edx", "ebx", "esp", "ebp", "esi", "edi" };

	public static void r32(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		out.printKeyword(R32_REGS[(modrmByte >> 3) & 0x7]);
	}

	private static final @NonNull String[] R64_REGS = { "rax", "rcx", "rdx", "rbx", "rsp", "rbp", "rsi", "rdi" };

	public static void r64(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		out.printKeyword(R64_REGS[(modrmByte >> 3) & 0x7]);
	}

	public static void rm8b16(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		rmXb16(modrmByte, R8_REGS, buffer, out);
	}

	public static void rm16b16(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		rmXb16(modrmByte, R16_REGS, buffer, out);
	}

	public static void rm32b16(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		rmXb16(modrmByte, R32_REGS, buffer, out);
	}

	public static void rm64b16(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		rmXb16(modrmByte, R64_REGS, buffer, out);
	}

	public static void rm16b32(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		rmXb32(modrmByte, R16_REGS, buffer, out);
	}

	public static void rm32b32(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		rmXb32(modrmByte, R32_REGS, buffer, out);
	}

	public static void rm64b32(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		rmXb32(modrmByte, R64_REGS, buffer, out);
	}

	private static void rmXb16(byte modrmByte, @NonNull String[] regs, MCDInputBuffer buffer, MCDOutputBuffer out)
			throws IOException {
		int modrmKey = modrmByte & 0b11000111;

		switch (modrmKey) {
		case 0b0000000:
			out.print("[").printKeyword("bx").printOperator("+").printKeyword("si").print("]");
			break;
		case 0b0000001:
			out.print("[").printKeyword("bx").printOperator("+").printKeyword("di").print("]");
			break;
		case 0b0000010:
			out.print("[").printKeyword("bp").printOperator("+").printKeyword("si").print("]");
			break;
		case 0b0000011:
			out.print("[").printKeyword("bp").printOperator("+").printKeyword("di").print("]");
			break;
		case 0b0000100:
			out.print("[").printKeyword("si").print("]");
			break;
		case 0b0000101:
			out.print("[").printKeyword("di").print("]");
			break;
		case 0b0000110:
			out.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b0000111:
			out.print("[").printKeyword("bx").print("]");
			break;
		case 0b0100000:
			out.print("[").printKeyword("bx").printOperator("+").printKeyword("si").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100001:
			out.print("[").printKeyword("bx").printOperator("+").printKeyword("di").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100010:
			out.print("[").printKeyword("bp").printOperator("+").printKeyword("si").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100011:
			out.print("[").printKeyword("bp").printOperator("+").printKeyword("di").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100100:
			out.print("[").printKeyword("si").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100101:
			out.print("[").printKeyword("di").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100110:
			out.print("[").printKeyword("bp").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100111:
			out.print("[").printKeyword("bx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b1000000:
			out.print("[").printKeyword("bx").printOperator("+").printKeyword("si").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b1000001:
			out.print("[").printKeyword("bx").printOperator("+").printKeyword("di").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b1000010:
			out.print("[").printKeyword("bp").printOperator("+").printKeyword("si").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b1000011:
			out.print("[").printKeyword("bp").printOperator("+").printKeyword("di").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b1000100:
			out.print("[").printKeyword("si").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b1000101:
			out.print("[").printKeyword("di").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b1000110:
			out.print("[").printKeyword("bp").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		case 0b1000111:
			out.print("[").printKeyword("bx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI16()));
			break;
		default:
			out.printKeyword(regs[modrmKey & 0b00000111]);
		}
	}

	private static void rmXb32(byte modrmByte, @NonNull String[] regs, MCDInputBuffer buffer, MCDOutputBuffer out)
			throws IOException {
		int modrmKey = modrmByte & 0b11000111;

		switch (modrmKey) {
		case 0b0000000:
			out.print("[").printKeyword("eax").print("]");
			break;
		case 0b0000001:
			out.print("[").printKeyword("ecx").print("]");
			break;
		case 0b0000010:
			out.print("[").printKeyword("edx").print("]");
			break;
		case 0b0000011:
			out.print("[").printKeyword("ebx").print("]");
			break;
		case 0b0000100:
			out.print("[").printKeyword("?").print("]");
			break;
		case 0b0000101:
			out.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b0000110:
			out.print("[").printKeyword("esi").print("]");
			break;
		case 0b0000111:
			out.print("[").printKeyword("edi").print("]");
			break;
		case 0b0100000:
			out.print("[").printKeyword("eax").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100001:
			out.print("[").printKeyword("ecx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100010:
			out.print("[").printKeyword("edx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100011:
			out.print("[").printKeyword("ebx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100100:
			out.print("[").printKeyword("?").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100101:
			out.print("[").printKeyword("ebx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100110:
			out.print("[").printKeyword("esi").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b0100111:
			out.print("[").printKeyword("edi").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI8()));
			break;
		case 0b1000000:
			out.print("[").printKeyword("eax").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b1000001:
			out.print("[").printKeyword("ecx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b1000010:
			out.print("[").printKeyword("edx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b1000011:
			out.print("[").printKeyword("ebx").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b1000100:
			out.print("[").printKeyword("?").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b1000101:
			out.print("[").printKeyword("ebp").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b1000110:
			out.print("[").printKeyword("esi").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		case 0b1000111:
			out.print("[").printKeyword("edi").print("]").printOperator("+")
					.printValue(HexFormat.LOWER_CASE.format(buffer.decodeI32()));
			break;
		default:
			out.printKeyword(regs[modrmKey & 0b00000111]);
		}
	}

}
