/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
import java.util.Deque;
import java.util.LinkedList;

import de.carne.util.Strings;

/**
 * Utility class used for exchanging {@linkplain ClassPrinter} output.
 */
public final class PrintBuffer implements Printable {

	private final Deque<Entry> entries = new LinkedList<>();

	/**
	 * Prints token using default style.
	 *
	 * @param token the token to print.
	 * @param out the {@linkplain ClassPrinter} to print to.
	 * @param context the {@linkplain ClassContext} to use for printing.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void print(String token, ClassPrinter out, ClassContext context) throws IOException {
		if (context == ClassContext.ANNOTATION) {
			out.printLabel(token);
		} else {
			out.print(token);
		}
	}

	/**
	 * Prints token using keyword style.
	 *
	 * @param token the token to print.
	 * @param out the {@linkplain ClassPrinter} to print to.
	 * @param context the {@linkplain ClassContext} to use for printing.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void printKeyword(String token, ClassPrinter out, ClassContext context) throws IOException {
		if (context == ClassContext.ANNOTATION) {
			out.printLabel(token);
		} else {
			out.printKeyword(token);
		}
	}

	/**
	 * Checks whether this buffer instance is empty (and produces no output).
	 *
	 * @return {@code true} if this buffer instance is empty.
	 */
	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	/**
	 * Appends a token to the buffer using default style.
	 *
	 * @param token the token to add.
	 * @return this instance for chaining.
	 */
	public PrintBuffer append(String token) {
		if (Strings.notEmpty(token)) {
			this.entries.add(new Entry(token, PrintBuffer::print));
		}
		return this;
	}

	/**
	 * Appends a token to the buffer using the given style.
	 *
	 * @param token the token to add.
	 * @param printer the printer style to use.
	 * @return this instance for chaining.
	 */
	public PrintBuffer append(String token, Printer printer) {
		if (Strings.notEmpty(token)) {
			this.entries.add(new Entry(token, printer));
		}
		return this;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		for (Entry entry : this.entries) {
			entry.print(out, context);
		}
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		for (Entry entry : this.entries) {
			buffer.append(entry.toString());
		}
		return buffer.toString();
	}

	private class Entry implements Printable {

		private final String token;
		private final Printer printer;

		Entry(String token, Printer printer) {
			this.token = token;
			this.printer = printer;
		}

		@Override
		public void print(ClassPrinter out, ClassContext context) throws IOException {
			this.printer.print(this.token, out, context);
		}

		@Override
		public String toString() {
			return this.token;
		}

	}

	/**
	 * Functional interface for defining the actual print output.
	 */
	@FunctionalInterface
	public interface Printer {

		/**
		 * Prints a token.
		 *
		 * @param token the token to print.
		 * @param out the {@linkplain ClassPrinter} to print to.
		 * @param context the {@linkplain ClassContext} to use for printing.
		 * @throws IOException if an I/O error occurs.
		 */
		void print(String token, ClassPrinter out, ClassContext context) throws IOException;

	}

}
