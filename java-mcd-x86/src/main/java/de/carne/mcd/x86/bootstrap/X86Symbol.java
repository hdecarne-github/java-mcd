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
package de.carne.mcd.x86.bootstrap;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.mcd.x86.ImmediateOperandType;
import de.carne.mcd.x86.ImplicitOperandDecoder;
import de.carne.mcd.x86.ModRMOperandType;
import de.carne.mcd.x86.OperandType;

enum X86Symbol {

	REL8("rel8", ImmediateOperandType.REL8),

	REL16("rel16", ImmediateOperandType.REL16),

	REL32("rel32", ImmediateOperandType.REL16),

	PTR16_16("ptr16:16", ImplicitOperandDecoder.fromName("?")),

	PTR16_32("ptr16:32", ImplicitOperandDecoder.fromName("?")),

	R8("r8", ModRMOperandType.R8),

	R16("r16", ModRMOperandType.R16),

	R32("r32", ModRMOperandType.R32),

	R64("r64", ModRMOperandType.R64),

	OPCD_R8("+r8", null),

	OPCD_R16("+r16", null),

	OPCD_R32("+r32", null),

	OPCD_R64("+r64", null),

	IMM8("imm8", ImmediateOperandType.IMM8),

	IMM16("imm16", ImmediateOperandType.IMM16),

	IMM32("imm32", ImmediateOperandType.IMM32),

	IMM64("imm64", ImmediateOperandType.IMM64),

	RM8("r/m8", ModRMOperandType.RM8),

	RM16("r/m16", ModRMOperandType.RM16),

	RM32("r/m32", ModRMOperandType.RM32),

	RM64("r/m64", ModRMOperandType.RM64),

	M("m", ImplicitOperandDecoder.fromName("?")),

	M16("m16", ImplicitOperandDecoder.fromName("?")),

	M32("m32", ImplicitOperandDecoder.fromName("?")),

	M64("m64", ImplicitOperandDecoder.fromName("?")),

	M128("m128", ImplicitOperandDecoder.fromName("?")),

	M16_16("m16:16", ImplicitOperandDecoder.fromName("?")),

	M16_32("m16:32", ImplicitOperandDecoder.fromName("?")),

	M16_64("m16:64", ImplicitOperandDecoder.fromName("?")),

	MOFFS8("moffs8", ImplicitOperandDecoder.fromName("?")),

	MOFFS16("moffs16", ImplicitOperandDecoder.fromName("?")),

	MOFFS32("moffs32", ImplicitOperandDecoder.fromName("?")),

	MOFFS64("moffs64", ImplicitOperandDecoder.fromName("?")),

	SREG("Sreg", ImplicitOperandDecoder.fromName("?")),

	M32FP("m32fp", ImplicitOperandDecoder.fromName("?")),

	M64FP("m64fp", ImplicitOperandDecoder.fromName("?")),

	M80FP("m80fp", ImplicitOperandDecoder.fromName("?")),

	M16INT("m16int", ImplicitOperandDecoder.fromName("?")),

	M32INT("m32int", ImplicitOperandDecoder.fromName("?")),

	M64INT("m64int", ImplicitOperandDecoder.fromName("?")),

	AL("al", ImplicitOperandDecoder.fromName("al")),

	CL("cl", ImplicitOperandDecoder.fromName("cl")),

	DL("dl", ImplicitOperandDecoder.fromName("dl")),

	BL("bl", ImplicitOperandDecoder.fromName("bl")),

	AH("ah", ImplicitOperandDecoder.fromName("ah")),

	CH("ch", ImplicitOperandDecoder.fromName("ch")),

	DH("dh", ImplicitOperandDecoder.fromName("dh")),

	BH("bh", ImplicitOperandDecoder.fromName("bh")),

	AX("ax", ImplicitOperandDecoder.fromName("ax")),

	CX("cx", ImplicitOperandDecoder.fromName("cx")),

	DX("dx", ImplicitOperandDecoder.fromName("dx")),

	BX("bx", ImplicitOperandDecoder.fromName("bx")),

	SP("sp", ImplicitOperandDecoder.fromName("sp")),

	BP("bp", ImplicitOperandDecoder.fromName("bp")),

	SI("si", ImplicitOperandDecoder.fromName("si")),

	DI("di", ImplicitOperandDecoder.fromName("di")),

	EAX("eax", ImplicitOperandDecoder.fromName("eax")),

	ECX("ecx", ImplicitOperandDecoder.fromName("ecx")),

	EDX("edx", ImplicitOperandDecoder.fromName("edx")),

	EBX("ebx", ImplicitOperandDecoder.fromName("ebx")),

	ESP("esp", ImplicitOperandDecoder.fromName("esp")),

	EBP("ebp", ImplicitOperandDecoder.fromName("ebp")),

	ESI("esi", ImplicitOperandDecoder.fromName("esi")),

	EDI("edi", ImplicitOperandDecoder.fromName("edi")),

	RAX("rax", ImplicitOperandDecoder.fromName("rax")),

	MDI("es:[di]", ImplicitOperandDecoder.fromName("?")),

	MEDI("es:[edi]", ImplicitOperandDecoder.fromName("?")),

	MRDI("[rdi]", ImplicitOperandDecoder.fromName("?")),

	MSI("ds:[si]", ImplicitOperandDecoder.fromName("?")),

	MESI("ds:[esi]", ImplicitOperandDecoder.fromName("?")),

	MRSI("[rsi]", ImplicitOperandDecoder.fromName("?"));

	private final String symbol;
	private final @Nullable OperandType decoder;

	private X86Symbol(String symbol, @Nullable OperandType decoder) {
		this.symbol = symbol;
		this.decoder = decoder;
	}

	public String symbol() {
		return this.symbol;
	}

	public OperandType decoder() {
		return Objects.requireNonNull(this.decoder);
	}

}
