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
package de.carne.mcd.common;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Base class for all kinds of machine code decoders.
 */
public abstract class MachineCodeDecoder implements MCDProperties {

	private static final ThreadLocal<@Nullable MCDDecodeContext> DECODE_CONTEXT_HOLDER = new ThreadLocal<>();

	private final String name;
	private ByteOrder byteOrder;
	private Map<String, String> properties = new HashMap<>();

	protected MachineCodeDecoder(String name, ByteOrder byteOrder) {
		this.name = name;
		this.byteOrder = byteOrder;
	}

	/**
	 * Gets the {@linkplain MCDDecodeContext} associated with the current decode call.
	 *
	 * @return the {@linkplain MCDDecodeContext} associated with the current decode call.
	 * @throws IllegalStateException if no context is currently set.
	 */
	public static MCDDecodeContext getDecodeContext() {
		MCDDecodeContext decodeContext = DECODE_CONTEXT_HOLDER.get();

		if (decodeContext == null) {
			throw new IllegalStateException("Decode context only accessible during decode");
		}
		return decodeContext;
	}

	/**
	 * Gets the {@linkplain MCDDecodeContext} associated with the current decode call.
	 *
	 * @param <T> the actual decode context type.
	 * @param decodeContextType the decode context type to return.
	 * @return the {@linkplain MCDDecodeContext} associated with the current decode call.
	 * @throws IllegalStateException if no context is currently set or if the set context type does not match.
	 */
	public static <T extends MCDDecodeContext> T getDecodeContext(Class<T> decodeContextType) {
		MCDDecodeContext decodeContext = getDecodeContext();
		Class<?> actualDecodeContextType = decodeContext.getClass();

		if (!decodeContextType.isAssignableFrom(actualDecodeContextType)) {
			throw new IllegalStateException("Decode context type mismatch: " + actualDecodeContextType.getName());
		}
		return decodeContextType.cast(decodeContext);
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
	 * Decodes the given byte channel's data.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param out the {@linkplain WritableByteChannel} to decode to.
	 * @throws IOException if an I/O error occurs.
	 */
	public void decode(ReadableByteChannel in, WritableByteChannel out) throws IOException {
		try (MCDOutput out0 = new PlainMCDOutput(out, false)) {
			doDecode(in, out0);
		}
	}

	/**
	 * Decodes the given byte channel's data.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param out the {@linkplain MCDOutput} to decode to.
	 * @throws IOException if an I/O error occurs.
	 */
	public void decode(ReadableByteChannel in, MCDOutput out) throws IOException {
		synchronized (DECODE_CONTEXT_HOLDER) {
			@Nullable MCDDecodeContext savedDecodeContext = DECODE_CONTEXT_HOLDER.get();
			MCDDecodeContext decodeContext = prepareDecode();

			DECODE_CONTEXT_HOLDER.set(decodeContext);
			try {
				doDecode(in, out);
			} finally {
				if (savedDecodeContext != null) {
					DECODE_CONTEXT_HOLDER.set(savedDecodeContext);
				} else {
					DECODE_CONTEXT_HOLDER.remove();
				}
			}
		}
	}

	protected MCDDecodeContext prepareDecode() {
		return new MCDDecodeContext(this);
	}

	protected abstract void doDecode(ReadableByteChannel in, MCDOutput out) throws IOException;

	protected MCDDecodeBuffer newDecodeBuffer(ReadableByteChannel in) {
		return new MCDDecodeBuffer(in, this.byteOrder);
	}

	@Override
	public @Nullable String getProperty(String key) {
		return this.properties.get(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return this.properties.getOrDefault(key, defaultValue);
	}

}
