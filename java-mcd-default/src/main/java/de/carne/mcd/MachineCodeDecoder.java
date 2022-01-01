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
package de.carne.mcd;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * Base class for all kinds of machine code decoders.
 */
public abstract class MachineCodeDecoder {

	private static final ThreadLocal<@Nullable MachineCodeDecoder> ACTIVE_DECODER_HOLDER = new ThreadLocal<>();

	private final String name;
	private ByteOrder byteOrder;
	private long defaultLimit;

	protected MachineCodeDecoder(String name, ByteOrder byteOrder, long defaultLimit) {
		this.name = name;
		this.byteOrder = byteOrder;
		this.defaultLimit = defaultLimit;
	}

	/**
	 * Gets the active {@linkplain MachineCodeDecoder}.
	 *
	 * @return the {@linkplain MachineCodeDecoder} instance associated with the current decode call.
	 * @throws IllegalStateException if called outside a decode call.
	 */
	public static MachineCodeDecoder getDecoder() {
		MachineCodeDecoder activeDecoder = ACTIVE_DECODER_HOLDER.get();

		if (activeDecoder == null) {
			throw new IllegalStateException("Decoder only accessible during decode call");
		}
		return activeDecoder;
	}

	/**
	 * Gets the active {@linkplain MachineCodeDecoder}.
	 *
	 * @param <T> the actual decoder type.
	 * @param decoderType the decoder type to get.
	 * @return the {@linkplain MachineCodeDecoder} instance associated with the current decode call.
	 * @throws IllegalStateException if called outside a decode call.
	 */
	public static <T extends MachineCodeDecoder> T getDecoder(Class<T> decoderType) {
		MachineCodeDecoder activeDecoder = getDecoder();
		Class<?> activeDecoderType = activeDecoder.getClass();

		if (!decoderType.isAssignableFrom(activeDecoderType)) {
			throw new IllegalStateException("Decoder type mismatch: " + activeDecoderType.getName());
		}
		return decoderType.cast(activeDecoder);
	}

	/**
	 * Gets this {@linkplain MachineCodeDecoder} instance's name.
	 *
	 * @return this {@linkplain MachineCodeDecoder} instance's name.
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Gets this {@linkplain MachineCodeDecoder} instance's byte order.
	 *
	 * @return this {@linkplain MachineCodeDecoder} instance's byte order.
	 */
	public ByteOrder byteOrder() {
		return this.byteOrder;
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
	 * Gets this {@linkplain MachineCodeDecoder} instance's default decode limit.
	 *
	 * @return this {@linkplain MachineCodeDecoder} instance's default decode limit.
	 */
	public long defaultLimit() {
		return this.defaultLimit;
	}

	/**
	 * Sets this {@linkplain MachineCodeDecoder} instance's default decode limit.
	 *
	 * @param defaultLimit the default decode limit to use.
	 */
	public void setDefaultLimit(long defaultLimit) {
		this.defaultLimit = defaultLimit;
	}

	/**
	 * Decodes the given byte channel's data.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param out the {@linkplain MCDOutput} to decode to.
	 * @return the number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public long decode(ReadableByteChannel in, MCDOutput out) throws IOException {
		return decode(in, out, 0, Long.MAX_VALUE);
	}

	/**
	 * Decodes the given byte channel's data.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param out the {@linkplain MCDOutput} to decode to.
	 * @param offset the current decode offset.
	 * @return the number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public long decode(ReadableByteChannel in, MCDOutput out, long offset) throws IOException {
		return decode(in, out, offset, this.defaultLimit);
	}

	/**
	 * Decodes the given byte channel's data.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param out the {@linkplain MCDOutput} to decode to.
	 * @param offset the current decode offset.
	 * @param limit the number of bytes after which decoding should stop.
	 * @return the number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public long decode(ReadableByteChannel in, MCDOutput out, long offset, long limit) throws IOException {
		@Nullable MachineCodeDecoder savedDecoder = ACTIVE_DECODER_HOLDER.get();
		long decoded;

		ACTIVE_DECODER_HOLDER.set(this);
		try {
			decoded = decode0(new MCDInputBuffer(in, this.byteOrder), new MCDOutputBuffer(out), offset, limit);
		} finally {
			if (savedDecoder != null) {
				ACTIVE_DECODER_HOLDER.set(savedDecoder);
			} else {
				ACTIVE_DECODER_HOLDER.remove();
			}
		}
		return decoded;
	}

	protected abstract long decode0(MCDInputBuffer in, MCDOutputBuffer out, long offset, long limit) throws IOException;

}
