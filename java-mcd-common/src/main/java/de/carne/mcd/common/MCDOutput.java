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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * Output channel receiving the decoded data during a
 * {@linkplain MachineCodeDecoder#decode(java.nio.channels.ReadableByteChannel, MCDOutput)} call..
 */
public interface MCDOutput extends Closeable, Flushable {

	/**
	 * Increases the indent level of all following print calls:
	 *
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput increaseIndent() throws IOException;

	/**
	 * Decreases the indent level of all following print calls:
	 *
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput decreaseIndent() throws IOException;

	/**
	 * Prints a line break.
	 *
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput println() throws IOException;

	/**
	 * Prints a standard text.
	 *
	 * @param text the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput print(String text) throws IOException;

	/**
	 * Prints a standard text and a line break.
	 *
	 * @param text the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput println(String text) throws IOException;

	/**
	 * Prints a value text.
	 *
	 * @param value the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printValue(String value) throws IOException;

	/**
	 * Prints a value text and a line break.
	 *
	 * @param value the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printlnValue(String value) throws IOException;

	/**
	 * Prints a comment text.
	 *
	 * @param comment the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printComment(String comment) throws IOException;

	/**
	 * Prints a comment text and a line break.
	 *
	 * @param comment the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printlnComment(String comment) throws IOException;

	/**
	 * Prints a keyword text.
	 *
	 * @param keyword the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printKeyword(String keyword) throws IOException;

	/**
	 * Prints a keyword text and a line break.
	 *
	 * @param keyword the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printlnKeyword(String keyword) throws IOException;

	/**
	 * Prints an operator text.
	 *
	 * @param operator the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printOperator(String operator) throws IOException;

	/**
	 * Prints an operator text and a line break.
	 *
	 * @param operator the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printlnOperator(String operator) throws IOException;

	/**
	 * Prints a label text.
	 *
	 * @param label the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printLabel(String label) throws IOException;

	/**
	 * Prints a label text and a line break.
	 *
	 * @param label the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printlnLabel(String label) throws IOException;

	/**
	 * Prints an error text.
	 *
	 * @param error the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printError(String error) throws IOException;

	/**
	 * Prints an error text and a line break.
	 *
	 * @param error the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutput printlnError(String error) throws IOException;

}
