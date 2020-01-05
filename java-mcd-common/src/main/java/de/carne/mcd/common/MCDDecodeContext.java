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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Class used to store and provide decode call specific context information.
 */
public class MCDDecodeContext implements MCDProperties {

	private final MachineCodeDecoder decoder;
	private final Map<String, String> properties = new HashMap<>();

	/**
	 * Constructs a new {@linkplain MCDDecodeContext} instance.
	 *
	 * @param decoder the {@linkplain MachineCodeDecoder} instance this instance is associated with.
	 */
	public MCDDecodeContext(MachineCodeDecoder decoder) {
		this.decoder = decoder;
	}

	/**
	 * Gets the {@linkplain MachineCodeDecoder} this instance is associated with.
	 *
	 * @return the {@linkplain MachineCodeDecoder} this instance is associated with.
	 */
	public MachineCodeDecoder getDecoder() {
		return this.decoder;
	}

	/**
	 * Gets the {@linkplain MachineCodeDecoder} this instance is associated with.
	 *
	 * @param <T> the actual decoder type.
	 * @param decoderType the decoder type to get.
	 * @return the {@linkplain MachineCodeDecoder} this instance is associated with.
	 * @throws IllegalStateException if decoder type does not match.
	 */
	public <T extends MachineCodeDecoder> T getDecoder(Class<T> decoderType) {
		Class<?> actualDecoderType = this.decoder.getClass();

		if (!decoderType.isAssignableFrom(actualDecoderType)) {
			throw new IllegalStateException("Decoder type mismatch: " + actualDecoderType.getName());
		}
		return decoderType.cast(this.decoder);
	}

	@Override
	public @Nullable String getProperty(String key) {
		String value = this.properties.get(key);

		return (value != null ? value : this.decoder.getProperty(key));
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String value = this.properties.get(key);

		return (value != null ? value : this.decoder.getProperty(key, defaultValue));
	}

}
