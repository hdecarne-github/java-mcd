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
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.common.MachineCodeDecoder;

/**
 * x86/amd64 machine code decoder.
 */
public class X86MachineCodeDecoder extends MachineCodeDecoder {

	private static final String NAME = "X86 instructions";

	/**
	 * Constructs a new {@linkplain X86MachineCodeDecoder} instance.
	 */
	public X86MachineCodeDecoder() {
		super(NAME, ByteOrder.LITTLE_ENDIAN);
	}

	@Override
	public void decode(ReadableByteChannel in, MCDOutput out) throws IOException {
		// TODO Auto-generated method stub
	}

}
