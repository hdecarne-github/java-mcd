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

/**
 *
 */
public enum X86Symbol {

	RM8("r/m8"),

	RM16("r/m16"),

	RM32("r/m32"),

	RM64("r/m64"),

	R8("r8"),

	R16("r16"),

	R32("r32"),

	R64("r64"),

	IMM8("imm8"),

	IMM16("imm16"),

	IMM32("imm32"),

	IMM64("imm64"),

	REL8("rel8"),

	REL16("rel16"),

	REL32("rel32"),

	M("m"),

	M16("m16"),

	M32("m32"),

	M64("m64"),

	MOFFS8("moffs8"),

	MOFFS16("moffs16"),

	MOFFS32("moffs32"),

	MOFFS64("moffs64"),

	PTR16("ptr16:16"),

	PTR32("ptr16:32"),

	SREG("Sreg"),

	AX("AX"),

	EAX("EAX"),

	RAX("RAX");

	private final String symbol;

	private X86Symbol(String symbol) {
		this.symbol = symbol;
	}

	public String symbol() {
		return this.symbol;
	}

}
