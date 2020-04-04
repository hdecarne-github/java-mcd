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
package de.carne.mcd.instruction;

import java.io.DataOutput;
import java.io.IOException;

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * Interface for all kind of decodable instructions.
 */
public interface Instruction {

	/**
	 * Saves these instruction's parameters to the given {@linkplain DataOutput} for later loading via
	 * {@linkplain InstructionFactory#loadInstruction(java.io.DataInput)}.
	 *
	 * @param out the {@linkplain DataOutput} to store to.
	 * @throws IOException if an I/O error occurs.
	 */
	void save(DataOutput out) throws IOException;

	/**
	 * Decodes this instruction.
	 *
	 * @param ip the current instruction pointer.
	 * @param opcode the instruction opcode.
	 * @param in the {@linkplain MCDInputBuffer} to read any additional instruction data from.
	 * @param out the {@linkplain MCDOutputBuffer} to decode to.
	 * @throws IOException if an I/O error occurs.
	 */
	void decode(long ip, InstructionOpcode opcode, MCDInputBuffer in, MCDOutputBuffer out) throws IOException;

}
