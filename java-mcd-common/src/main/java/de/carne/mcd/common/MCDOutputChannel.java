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
import java.nio.channels.Channel;

/**
 * Output channel receiving the decoded data during a
 * {@linkplain MachineCodeDecoder#decode(java.nio.channels.ReadableByteChannel, MCDOutputChannel)} call..
 */
public interface MCDOutputChannel extends Channel {

	/**
	 * Increases the indent level of all following print calls:
	 * 
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel increaseIndent() throws IOException;

	/**
	 * Decreases the indent level of all following print calls:
	 * 
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel decreaseIndent() throws IOException;

	/**
	 * Prints a line break.
	 *
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel println() throws IOException;

	/**
	 * Prints a standard text.
	 *
	 * @param text the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel print(String text) throws IOException;

	/**
	 * Prints a standard text and a line break.
	 *
	 * @param text the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel println(String text) throws IOException;

	/**
	 * Prints a value text.
	 *
	 * @param value the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printValue(String value) throws IOException;

	/**
	 * Prints a value text and a line break.
	 *
	 * @param value the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printlnValue(String value) throws IOException;

	/**
	 * Prints a comment text.
	 *
	 * @param comment the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printComment(String comment) throws IOException;

	/**
	 * Prints a comment text and a line break.
	 *
	 * @param comment the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printlnComment(String comment) throws IOException;

	/**
	 * Prints a keyword text.
	 *
	 * @param keyword the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printKeyword(String keyword) throws IOException;

	/**
	 * Prints a keyword text and a line break.
	 *
	 * @param keyword the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printlnKeyword(String keyword) throws IOException;

	/**
	 * Prints an operator text.
	 *
	 * @param operator the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printOperator(String operator) throws IOException;

	/**
	 * Prints an operator text and a line break.
	 *
	 * @param operator the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printlnOperator(String operator) throws IOException;

	/**
	 * Prints a label text.
	 *
	 * @param label the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printLabel(String label) throws IOException;

	/**
	 * Prints a label text and a line break.
	 *
	 * @param label the text to print.
	 * @return this instance for chaining.
	 * @throws IOException if an I/O error occurs.
	 */
	MCDOutputChannel printlnLabel(String label) throws IOException;

}
