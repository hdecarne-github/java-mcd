/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.x86decoder;

import java.io.IOException;

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * Functional interface for a single decode step.
 */
@FunctionalInterface
public interface Decoder {

	/**
	 * Performs the decode step.
	 *
	 * @param decoderState the current decoder state.
	 * @param in the {@linkplain MCDInputBuffer} instance to decode from.
	 * @param out the {@linkplain MCDOutputBuffer} instance to decode to.
	 * @throws IOException if an I/O error occurs.
	 */
	void decode(X86DecoderState decoderState, MCDInputBuffer in, MCDOutputBuffer out) throws IOException;

}
