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
package de.carne.mcd.jvm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class Attributes {

	private Attributes() {
		// Prevent instantiation
	}

	public static <T extends Attribute> List<T> resolveAttributes(List<Attribute> attributes, Class<T> type) {
		return attributes.stream().filter(a -> type.isAssignableFrom(a.getClass())).map(type::cast)
				.collect(Collectors.toList());
	}

	public static <T extends Attribute> T resolveUniqueAttribute(List<Attribute> attributes, Class<T> type)
			throws IOException {
		List<T> resolved = resolveAttributes(attributes, type);
		int resolvedCount = resolved.size();

		if (resolvedCount != 1) {
			throw new IOException(
					"Unexpected number of '" + type.getSimpleName() + "' attributes: " + resolvedCount);
		}
		return resolved.get(0);
	}

	public static <T extends Attribute> Optional<T> resolveOptionalAttribute(List<Attribute> attributes, Class<T> type)
			throws IOException {
		List<T> resolved = resolveAttributes(attributes, type);
		int resolvedCount = resolved.size();

		if (resolvedCount > 1) {
			throw new IOException(
					"Unexpected number of '" + type.getSimpleName() + "' attributes: " + resolvedCount);
		}
		return (resolvedCount == 0 ? Optional.empty() : Optional.of(resolved.get(0)));
	}

}
