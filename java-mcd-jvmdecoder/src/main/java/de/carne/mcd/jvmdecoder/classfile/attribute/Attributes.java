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
package de.carne.mcd.jvmdecoder.classfile.attribute;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;

/**
 * Utility class providing {@linkplain Attribute} related functions.
 */
public final class Attributes {

	private Attributes() {
		// Prevent instantiation
	}

	/**
	 * Resolves all attributes of a specific type.
	 *
	 * @param <T> the actual attribute type to resolve.
	 * @param attributes the attribute list to examine.
	 * @param type the attribute type to resolve.
	 * @return the resolved list of attribute objects (may be empty).
	 */
	public static <T extends Attribute> List<T> resolveAttributes(List<Attribute> attributes, Class<T> type) {
		return attributes.stream().filter(a -> type.isAssignableFrom(a.getClass())).map(type::cast)
				.collect(Collectors.toList());
	}

	/**
	 * Resolves a unique attribute.
	 *
	 * @param <T> the actual attribute type to resolve.
	 * @param attributes the attribute list to examine.
	 * @param type the attribute type to resolve.
	 * @return the resolved attribute instance.
	 * @throws IOException if the attribute does not exist or is not unique.
	 */
	public static <T extends Attribute> T resolveUniqueAttribute(List<Attribute> attributes, Class<T> type)
			throws IOException {
		List<T> resolved = resolveAttributes(attributes, type);
		int resolvedCount = resolved.size();

		if (resolvedCount != 1) {
			throw new IOException("Unexpected number of '" + type.getSimpleName() + "' attributes: " + resolvedCount);
		}
		return resolved.get(0);
	}

	/**
	 * Resolves an optional attribute.
	 *
	 * @param <T> the actual attribute type to resolve.
	 * @param attributes the attribute list to examine.
	 * @param type the attribute type to resolve.
	 * @return the resolved attribute instance.
	 * @throws IOException if the attribute is not unique.
	 */
	public static <T extends Attribute> Optional<T> resolveOptionalAttribute(List<Attribute> attributes, Class<T> type)
			throws IOException {
		List<T> resolved = resolveAttributes(attributes, type);
		int resolvedCount = resolved.size();

		if (resolvedCount > 1) {
			throw new IOException("Unexpected number of '" + type.getSimpleName() + "' attributes: " + resolvedCount);
		}
		return (resolvedCount == 0 ? Optional.empty() : Optional.of(resolved.get(0)));
	}

	/**
	 * Prints a list of attributes.
	 *
	 * @param attributes the attributes to print.
	 * @param out the {@linkplain ClassPrinter} to print to.
	 * @param context the {@linkplain ClassContext} to use for printing.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void print(List<? extends Attribute> attributes, ClassPrinter out, ClassContext context)
			throws IOException {
		for (Attribute attribute : attributes) {
			attribute.print(out, context);
		}
	}

	/**
	 * Prints an optional attributes.
	 *
	 * @param attribute the attributes to print.
	 * @param out the {@linkplain ClassPrinter} to print to.
	 * @param context the {@linkplain ClassContext} to use for printing.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void print(Optional<? extends Attribute> attribute, ClassPrinter out, ClassContext context)
			throws IOException {
		if (attribute.isPresent()) {
			attribute.get().print(out, context);
		}
	}

}
