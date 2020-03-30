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
package de.carne.mcd.common.instruction;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Interface for instruction creation.
 */
public interface InstructionFactory {

	/**
	 * Loads and setups an {@linkplain Instruction} instance from a data stream previously written via
	 * {@linkplain Instruction#save(java.io.DataOutput)}.
	 *
	 * @param in the {@linkplain DataInputStream} to load from.
	 * @return the loaded {@linkplain Instruction} instance.
	 * @throws IOException if an I/O error occurs.
	 */
	Instruction loadInstruction(DataInput in) throws IOException;

	/**
	 * Creates a default {@linkplain Instruction} instance for handling opcodes not assigned to any specific
	 * {@linkplain Instruction} instance.
	 *
	 * @return the created default {@linkplain Instruction} instance.
	 */
	Instruction getDefaultInstruction();

}
