/*
 * Copyright (c) 2019 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.common;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Base class for all kinds of machine code decoders.
 */
public abstract class MachineCodeDecoder {

	private final String name;
	private ByteOrder byteOrder = ByteOrder.nativeOrder();

	protected MachineCodeDecoder(String name, ByteOrder byteOrder) {
		this.name = name;
		this.byteOrder = byteOrder;
	}

	/**
	 * Gets this decoder's name.
	 *
	 * @return this decoder's name.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Sets this {@linkplain MachineCodeDecoder} instance's byte order.
	 *
	 * @param byteOrder the {@linkplain ByteOrder} to set.
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * Decodes the given byte channel data.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param out the {@linkplain MCDOutput} to decode to.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract void decode(ReadableByteChannel in, MCDOutput out) throws IOException;

	/**
	 * Decodes the given byte channel data.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param out the {@linkplain WritableByteChannel} to decode to.
	 * @throws IOException if an I/O error occurs.
	 */
	public void decode(ReadableByteChannel in, WritableByteChannel out) throws IOException {
		try (MCDOutput out0 = new PlainMCDOutput(out, false)) {
			decode(in, out0);
		}
	}

	protected MCDDecodeBuffer newDecodeBuffer(ReadableByteChannel in) {
		return new MCDDecodeBuffer(in, this.byteOrder);
	}

}
