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
package de.carne.mcd.io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;

import de.carne.mcd.MCDOutput;
import de.carne.mcd.PlainMCDOutput;
import de.carne.util.Exceptions;
import de.carne.util.Strings;

/**
 * Utility class used for queuing {@linkplain MCDOutput} commands.
 */
public final class MCDPrintBuffer implements MCDOutput {

	private final Deque<Entry> buffer = new LinkedList<>();

	/**
	 * Checks whether this buffer instance is empty or not:
	 *
	 * @return {@code true} if this buffer instance is empty (generates no output).
	 */
	public boolean isEmtpy() {
		return this.buffer.isEmpty();
	}

	/**
	 * Clears this buffer instance.
	 */
	public void clear() {
		this.buffer.clear();
	}

	/**
	 * Prints this buffer instance's entries to the given {@linkplain MCDOutput}.
	 *
	 * @param out the {@linkplain MCDOutput} to print to.
	 * @throws IOException if an I/O error occurs.
	 */
	public void printTo(MCDOutput out) throws IOException {
		for (Entry entry : this.buffer) {
			entry.run(out);
		}
	}

	@Override
	public MCDPrintBuffer increaseIndent() throws IOException {
		this.buffer.add(MCDOutput::increaseIndent);
		return this;
	}

	@Override
	public MCDPrintBuffer decreaseIndent() throws IOException {
		this.buffer.add(MCDOutput::decreaseIndent);
		return this;
	}

	@Override
	public MCDPrintBuffer println() throws IOException {
		this.buffer.add(MCDOutput::println);
		return this;
	}

	@Override
	public MCDPrintBuffer print(String text) throws IOException {
		if (!Strings.isEmpty(text)) {
			this.buffer.add(out -> out.print(text));
		}
		return this;
	}

	@Override
	public MCDPrintBuffer println(String text) throws IOException {
		if (!Strings.isEmpty(text)) {
			this.buffer.add(out -> out.println(text));
		} else {
			this.buffer.add(MCDOutput::println);
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printValue(String value) throws IOException {
		if (!Strings.isEmpty(value)) {
			this.buffer.add(out -> out.printValue(value));
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printlnValue(String value) throws IOException {
		if (!Strings.isEmpty(value)) {
			this.buffer.add(out -> out.printlnValue(value));
		} else {
			this.buffer.add(MCDOutput::println);
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printComment(String comment) throws IOException {
		if (!Strings.isEmpty(comment)) {
			this.buffer.add(out -> out.printComment(comment));
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printlnComment(String comment) throws IOException {
		if (!Strings.isEmpty(comment)) {
			this.buffer.add(out -> out.printlnComment(comment));
		} else {
			this.buffer.add(MCDOutput::println);
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printKeyword(String keyword) throws IOException {
		if (!Strings.isEmpty(keyword)) {
			this.buffer.add(out -> out.printKeyword(keyword));
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printlnKeyword(String keyword) throws IOException {
		if (!Strings.isEmpty(keyword)) {
			this.buffer.add(out -> out.printlnKeyword(keyword));
		} else {
			this.buffer.add(MCDOutput::println);
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printOperator(String operator) throws IOException {
		if (!Strings.isEmpty(operator)) {
			this.buffer.add(out -> out.printOperator(operator));
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printlnOperator(String operator) throws IOException {
		if (!Strings.isEmpty(operator)) {
			this.buffer.add(out -> out.printlnOperator(operator));
		} else {
			this.buffer.add(MCDOutput::println);
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printLabel(String label) throws IOException {
		if (!Strings.isEmpty(label)) {
			this.buffer.add(out -> out.printLabel(label));
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printlnLabel(String label) throws IOException {
		if (!Strings.isEmpty(label)) {
			this.buffer.add(out -> out.printlnLabel(label));
		} else {
			this.buffer.add(MCDOutput::println);
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printError(String error) throws IOException {
		if (!Strings.isEmpty(error)) {
			this.buffer.add(out -> out.printError(error));
		}
		return this;
	}

	@Override
	public MCDPrintBuffer printlnError(String error) throws IOException {
		if (!Strings.isEmpty(error)) {
			this.buffer.add(out -> out.printlnError(error));
		} else {
			this.buffer.add(MCDOutput::println);
		}
		return this;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();

		try (PlainMCDOutput out = new PlainMCDOutput(stringWriter, true)) {
			printTo(out);
		} catch (IOException e) {
			Exceptions.toRuntime(e);
		}
		return stringWriter.toString();
	}

	@FunctionalInterface
	private interface Entry {

		void run(MCDOutput out) throws IOException;

	}

}
