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

/**
 * Query interface providing index meta informations.
 */
public interface InstructionIndexParameters {

	/**
	 * Gets this index's parameter dword.
	 * <p>
	 * The parameter dword encodes the individual informations accessible via {@linkplain #entryCount()},
	 * {@linkplain #opcodeBytes()} and {@linkplain #positionBytes()} as follows:<br>
	 * Bit 32-9: Number of index entries.<br>
	 * Bit 8-5: Number of bytes per opcode entry - 1<br>
	 * Bit 4-1: Number of bytes per position entry - 1
	 * </p>
	 *
	 * @return this index's parameter dword.
	 * @see #entryCount()
	 * @see #opcodeBytes()
	 * @see #positionBytes()
	 */
	default int parameters() {
		int entryCount = entryCount();

		if (entryCount < 0 || 0xffffff < entryCount) {
			throw new IllegalStateException("Invalid entry count: " + entryCount);
		}

		int opcodeBytes = opcodeBytes();

		if (opcodeBytes < 1 || 0xf < opcodeBytes) {
			throw new IllegalStateException("Invalid opcode bytes count: " + opcodeBytes);
		}

		int positionBytes = positionBytes();

		if (positionBytes < 1 || 0xf < positionBytes) {
			throw new IllegalStateException("Invalid position bytes count: " + positionBytes);
		}
		return ((entryCount & 0xffffff) << 8) | (((opcodeBytes - 1) & 0xf) << 4) | ((positionBytes - 1) & 0xf);
	}

	/**
	 * Gets the number instructions stored in this index.
	 * 
	 * @return the number instructions stored in this index.
	 */
	int entryCount();

	/**
	 * Gets the number of bytes used to encode an {@linkplain InstructionOpcode} in the index's lookup table.
	 * 
	 * @return the number of bytes used to encode an {@linkplain InstructionOpcode} in the index's lookup table.
	 */
	int opcodeBytes();

	/**
	 * Gets the number of bytes used to encode a file position in the index's lookup table.
	 * 
	 * @return the number of bytes used to encode a file position in the index's lookup table.
	 */
	int positionBytes();

}
