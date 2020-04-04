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
package de.carne.mcd.jvm.classfile.bytecode;

import java.io.IOException;

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * Base interface for all kinds of operand types.
 */
public interface OperandType {

	/**
	 * Gets the operand's type (B:byte, S:short, I:int).
	 *
	 * @return the operand's type (B:byte, S:short, I:int).
	 */
	char type();

	/**
	 * Gets the operand type's name.
	 *
	 * @return the operand type's name.
	 */
	String name();

	/**
	 * Decodes the operand.
	 *
	 * @param pc the program counter of the corresponding opcode.
	 * @param buffer the {@linkplain MCDInputBuffer} to decode from.
	 * @param out the {@linkplain MCDOutputBuffer} to decode to.
	 * @throws IOException if an I/O error occurs.
	 */
	void decode(int pc, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException;

}
