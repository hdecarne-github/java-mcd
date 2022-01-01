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
package de.carne.mcd.jvmdecoder.classfile;

import java.io.IOException;

/**
 * Utility class used to separate a sequence of print operations.
 */
public final class PrintSeparator implements Printable {

	private boolean first = true;
	private final String separator;

	/**
	 * Constructs a new {@linkplain PrintSeparator} instance using the comma separator.
	 */
	public PrintSeparator() {
		this(", ");
	}

	/**
	 * Constructs a new {@linkplain PrintSeparator} instance using the given separator.
	 *
	 * @param separator the separator to use.
	 */
	public PrintSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * Resets this instance to start a new sequence of print operations.
	 */
	public void reset() {
		this.first = true;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		if (!this.first) {
			if (context == ClassContext.ANNOTATION) {
				out.printLabel(this.separator);
			} else {
				out.print(this.separator);
			}
		} else {
			this.first = false;
		}
	}

	/**
	 * Gets the separator for the next print operation.
	 * 
	 * @return the separator for the next print operation.
	 */
	public String next() {
		String next;

		if (!this.first) {
			next = this.separator;
		} else {
			next = "";
			this.first = false;
		}
		return next;
	}

}
