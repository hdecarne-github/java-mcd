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
package de.carne.mcd.jvmdecoder.classfile.bytecode;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import de.carne.mcd.instruction.InstructionIndex;

/**
 * Helper class providing access to the bytecode instruction index.
 */
public final class BytecodeInstructionIndex {

	private BytecodeInstructionIndex() {
		// Prevent instantiation
	}

	/**
	 * Opens the bytecode instruction index.
	 *
	 * @return the bytecode instruction index.
	 * @throws IOException if an I/O error occurs while opening the index.
	 */
	public static InstructionIndex open() throws IOException {
		URL instructionIndexUrl = Objects.requireNonNull(
				BytecodeInstructionIndex.class.getResource(BytecodeInstructionIndex.class.getSimpleName() + ".bin"));

		return InstructionIndex.open(new BytecodeInstructionFactory(), instructionIndexUrl);
	}

}
