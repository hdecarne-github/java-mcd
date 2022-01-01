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
package de.carne.mcd.x86decoder.bootstrap;

import de.carne.mcd.x86decoder.ImmediateDecoder;
import de.carne.mcd.x86decoder.ImplicitDecoder;
import de.carne.mcd.x86decoder.ModRMDecoder;
import de.carne.mcd.x86decoder.NamedDecoder;

enum X86Symbol {

	REL8("rel8", ImmediateDecoder.REL8),

	REL16("rel16", ImmediateDecoder.REL16),

	REL32("rel32", ImmediateDecoder.REL32),

	PTR16_16("ptr16:16", ImplicitDecoder.getInstance("?")),

	PTR16_32("ptr16:32", ImplicitDecoder.getInstance("?")),

	R8("r8", ModRMDecoder.R8),

	R16("r16", ModRMDecoder.R16),

	R32("r32", ModRMDecoder.R32),

	R64("r64", ModRMDecoder.R64),

	OPCD_R8("+r8", null),

	OPCD_R16("+r16", null),

	OPCD_R32("+r32", null),

	OPCD_R64("+r64", null),

	IMM8("imm8", ImmediateDecoder.IMM8),

	IMM16("imm16", ImmediateDecoder.IMM16),

	IMM32("imm32", ImmediateDecoder.IMM32),

	IMM64("imm64", ImmediateDecoder.IMM64),

	M("m", ImmediateDecoder.M),

	RM8("r/m8", ModRMDecoder.RM8),

	RM16("r/m16", ModRMDecoder.RM16),

	RM32("r/m32", ModRMDecoder.RM32),

	RM64("r/m64", ModRMDecoder.RM64),

	M16("m16", ImplicitDecoder.getInstance("m16")),

	M32("m32", ImplicitDecoder.getInstance("m32")),

	M64("m64", ImplicitDecoder.getInstance("m64")),

	M128("m128", ImplicitDecoder.getInstance("m128")),

	M16_16("m16:16", ImplicitDecoder.getInstance("m16:16")),

	M16_32("m16:32", ImplicitDecoder.getInstance("m16:32")),

	M16_64("m16:64", ImplicitDecoder.getInstance("m16:64")),

	MOFFS8("moffs8", ImmediateDecoder.MOFFS8),

	MOFFS16("moffs16", ImmediateDecoder.MOFFS16),

	MOFFS32("moffs32", ImmediateDecoder.MOFFS32),

	MOFFS64("moffs64", ImmediateDecoder.MOFFS64),

	SREG("Sreg", ImplicitDecoder.getInstance("?")),

	M32FP("m32fp", ImplicitDecoder.getInstance("?")),

	M64FP("m64fp", ImplicitDecoder.getInstance("?")),

	M80FP("m80fp", ImplicitDecoder.getInstance("?")),

	M16INT("m16int", ImplicitDecoder.getInstance("?")),

	M32INT("m32int", ImplicitDecoder.getInstance("?")),

	M64INT("m64int", ImplicitDecoder.getInstance("?")),

	AL("al", ImplicitDecoder.getInstance("al")),

	CL("cl", ImplicitDecoder.getInstance("cl")),

	DL("dl", ImplicitDecoder.getInstance("dl")),

	BL("bl", ImplicitDecoder.getInstance("bl")),

	AH("ah", ImplicitDecoder.getInstance("ah")),

	CH("ch", ImplicitDecoder.getInstance("ch")),

	DH("dh", ImplicitDecoder.getInstance("dh")),

	BH("bh", ImplicitDecoder.getInstance("bh")),

	AX("ax", ImplicitDecoder.getInstance("ax")),

	CX("cx", ImplicitDecoder.getInstance("cx")),

	DX("dx", ImplicitDecoder.getInstance("dx")),

	BX("bx", ImplicitDecoder.getInstance("bx")),

	SP("sp", ImplicitDecoder.getInstance("sp")),

	BP("bp", ImplicitDecoder.getInstance("bp")),

	SI("si", ImplicitDecoder.getInstance("si")),

	DI("di", ImplicitDecoder.getInstance("di")),

	EAX("eax", ImplicitDecoder.getInstance("eax")),

	ECX("ecx", ImplicitDecoder.getInstance("ecx")),

	EDX("edx", ImplicitDecoder.getInstance("edx")),

	EBX("ebx", ImplicitDecoder.getInstance("ebx")),

	ESP("esp", ImplicitDecoder.getInstance("esp")),

	EBP("ebp", ImplicitDecoder.getInstance("ebp")),

	ESI("esi", ImplicitDecoder.getInstance("esi")),

	EDI("edi", ImplicitDecoder.getInstance("edi")),

	RAX("rax", ImplicitDecoder.getInstance("rax")),

	MDI("es:[di]", ImplicitDecoder.getInstance("es:[di]")),

	MEDI("es:[edi]", ImplicitDecoder.getInstance("es:[edi]")),

	MRDI("[rdi]", ImplicitDecoder.getInstance("[rdi]")),

	MSI("ds:[si]", ImplicitDecoder.getInstance("ds:[si]")),

	MESI("ds:[esi]", ImplicitDecoder.getInstance("ds:[esi]")),

	MRSI("[rsi]", ImplicitDecoder.getInstance("[rsi]"));

	private final String symbol;
	private final NamedDecoder decoder;

	private X86Symbol(String symbol, NamedDecoder decoder) {
		this.symbol = symbol;
		this.decoder = decoder;
	}

	public String symbol() {
		return this.symbol;
	}

	public NamedDecoder decoder() {
		return this.decoder;
	}

}
