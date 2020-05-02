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
package de.carne.mcd.x86decoder.bootstrap;

import java.util.HashMap;
import java.util.Map;

class X86b32Mode extends X86Mode {

	private static final Map<String, String> OPERAND_MAP = new HashMap<>();

	static {
		OPERAND_MAP.put("Eb", X86Symbol.RM8.symbol());
		OPERAND_MAP.put("Gb", X86Symbol.R8.symbol());
		OPERAND_MAP.put("Ib", X86Symbol.IMM8.symbol());
		OPERAND_MAP.put("Ob", X86Symbol.MOFFS8.symbol());
		OPERAND_MAP.put("Zb", X86Symbol.OPCD_R8.symbol());

		OPERAND_MAP.put("Ibs", X86Symbol.IMM8.symbol());
		OPERAND_MAP.put("Jbs", X86Symbol.REL8.symbol());

		OPERAND_MAP.put("Ibss", X86Symbol.IMM8.symbol());

		OPERAND_MAP.put("Ew", X86Symbol.RM16.symbol());
		OPERAND_MAP.put("Gw", X86Symbol.R16.symbol());
		OPERAND_MAP.put("Iw", X86Symbol.IMM16.symbol());
		OPERAND_MAP.put("Mw", X86Symbol.RM16.symbol());
		OPERAND_MAP.put("Sw", X86Symbol.SREG.symbol());

		OPERAND_MAP.put("Ev", X86Symbol.RM32.symbol());
		OPERAND_MAP.put("Gv", X86Symbol.R32.symbol());
		OPERAND_MAP.put("Zv", X86Symbol.OPCD_R32.symbol());

		OPERAND_MAP.put("Eq", X86Symbol.RM32.symbol());

		OPERAND_MAP.put("Ivs", X86Symbol.IMM32.symbol());

		OPERAND_MAP.put("Ivds", X86Symbol.IMM32.symbol());
		OPERAND_MAP.put("Jvds", X86Symbol.REL32.symbol());

		OPERAND_MAP.put("Evqp", X86Symbol.RM32.symbol());
		OPERAND_MAP.put("Gvqp", X86Symbol.R32.symbol());
		OPERAND_MAP.put("Ivqp", X86Symbol.IMM32.symbol());
		OPERAND_MAP.put("Ovqp", X86Symbol.MOFFS32.symbol());
		OPERAND_MAP.put("Zvqp", X86Symbol.OPCD_R32.symbol());

		OPERAND_MAP.put("Ma", X86Symbol.M32.symbol());

		OPERAND_MAP.put("Ap", X86Symbol.PTR16_32.symbol());
		OPERAND_MAP.put("Mp", X86Symbol.M32.symbol());

		OPERAND_MAP.put("rAX", X86Symbol.EAX.symbol());
		OPERAND_MAP.put("(ES:)[rDI]", X86Symbol.MEDI.symbol());
		OPERAND_MAP.put("(DS:)[rSI]", X86Symbol.MESI.symbol());
		OPERAND_MAP.put("(DS):[rSI]", X86Symbol.MESI.symbol());
	}

	X86b32Mode() {
		super(OPERAND_MAP);
	}

	@Override
	public boolean isAvailable(X86InstructionReferenceEntry entry) {
		return entry.isX86b32();
	}

}
