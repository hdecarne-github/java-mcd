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
import java.net.URL;
import java.util.Objects;

import de.carne.mcd.common.instruction.InstructionIndex;

/**
 * Helper class providing access to the x86-16 instruction index.
 */
public final class X86b16InstructionIndex {

	private X86b16InstructionIndex() {
		// Prevent instantiation
	}

	/**
	 * Opens the x86-16 instruction index.
	 *
	 * @return the x86-16 instruction index.
	 * @throws IOException if an I/O error occurs while opening the index.
	 */
	public static InstructionIndex open() throws IOException {
		URL instructionIndexUrl = Objects.requireNonNull(
				X86b16InstructionIndex.class.getResource(X86b16InstructionIndex.class.getSimpleName() + ".bin"));

		return InstructionIndex.open(new X86InstructionFactory(), instructionIndexUrl);
	}

}
