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

import java.util.HashMap;
import java.util.Map;

import de.carne.mcd.x86.X86Symbol;

class X86b64Mode extends X86Mode {

	private static final Map<String, String> OPERAND_MAP = new HashMap<>();

	static {
		OPERAND_MAP.put("Eb", X86Symbol.RM8.symbol());
		OPERAND_MAP.put("Gb", X86Symbol.R8.symbol());
		OPERAND_MAP.put("Ib", X86Symbol.IMM8.symbol());

		OPERAND_MAP.put("Evqp", X86Symbol.RM32.symbol());
		OPERAND_MAP.put("Gvqp", X86Symbol.R32.symbol());
	}

	X86b64Mode() {
		super(OPERAND_MAP);
	}

	@Override
	public boolean isAvailable(X86InstructionReferenceEntry entry) {
		return entry.isX86b64();
	}

}
