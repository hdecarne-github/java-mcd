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

import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;

/**
 *
 */
public class ImplicitOperandDecoder implements OperandType {

	public static final char TAG = '*';

	private final String name;

	private ImplicitOperandDecoder(String name) {
		this.name = name;
	}

	public static ImplicitOperandDecoder fromName(String name) {
		return new ImplicitOperandDecoder(name);
	}

	@Override
	public char type() {
		return TAG;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public void decode(long ip, byte modrmByte, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		out.print(this.name);
	}

}
