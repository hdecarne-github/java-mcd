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
package de.carne.mcd.jvm.util;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.carne.mcd.jvm.ClassContext;
import de.carne.mcd.jvm.ClassPrinter;
import de.carne.mcd.jvm.classfile.Attribute;

/**
 * Utility class providing {@linkplain Attribute} related functions.
 */
public final class Attributes {

	private Attributes() {
		// Prevent instantiation
	}

	/**
	 * Resolves all attributes of a specific type from an attribute list.
	 *
	 * @param <T> the actual attribute type.
	 * @param attributes the attribute list to search.
	 * @param type the attribute type to resolve.
	 * @return the list of resolved attributes (may be empty).
	 */
	public static <T extends Attribute> List<T> resolveAttributes(List<Attribute> attributes, Class<T> type) {
		return attributes.stream().filter(a -> type.isAssignableFrom(a.getClass())).map(type::cast)
				.collect(Collectors.toList());
	}

	/**
	 * Resolves a single attribute type from an attribute list.
	 *
	 * @param <T> the actual attribute type.
	 * @param attributes the attribute list to search.
	 * @param type the attribute type to resolve.
	 * @return the resolved attribute.
	 * @throws IOException if the attribute type cannot be resolved or is not unique.
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
	 * Resolves a single optional attribute type from an attribute list.
	 *
	 * @param <T> the actual attribute type.
	 * @param attributes the attribute list to search.
	 * @param type the attribute type to resolve.
	 * @return the resolved optional attribute.
	 * @throws IOException if the attribute type is not unique.
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
	 * @param out the {@linkplain ClassPrinter} instance to print to.
	 * @param context the context of this print invocation.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void print(List<? extends Attribute> attributes, ClassPrinter out, ClassContext context)
			throws IOException {
		for (Attribute attribute : attributes) {
			attribute.print(out, context);
		}
	}

	/**
	 * Prints an optional attribute.
	 *
	 * @param <T> the actual attribute type.
	 * @param attributeHolder the optional attribute to print.
	 * @param out the {@linkplain ClassPrinter} instance to print to.
	 * @param context the context of this print invocation.
	 * @throws IOException if an I/O error occurs.
	 */
	public static <T extends Attribute> void print(Optional<T> attributeHolder, ClassPrinter out, ClassContext context)
			throws IOException {
		if (attributeHolder.isPresent()) {
			attributeHolder.get().print(out, context);
		}
	}

}
