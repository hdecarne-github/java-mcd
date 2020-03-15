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
package de.carne.mcd.common.io;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Buffered {@linkplain MCDOutput} wrapper.
 */
public class MCDOutputBuffer implements MCDOutput, MCDBuffer {

	private final MCDOutput out;
	private final Deque<Entry> buffer = new LinkedList<>();
	private boolean autoCommit = true;

	/**
	 * Constructs a new {@linkplain MCDOutputBuffer} instance.
	 *
	 * @param out the {@linkplain MCDOutput} to emit the decoded data to.
	 */
	public MCDOutputBuffer(MCDOutput out) {
		this.out = out;
	}

	@Override
	public boolean setAutoCommit(boolean autoCommit) {
		boolean previousAutoCommit = this.autoCommit;

		this.autoCommit = autoCommit;
		return previousAutoCommit;
	}

	@Override
	public void discard() {
		this.buffer.clear();
	}

	@Override
	public void commit() throws IOException {
		Entry entry;

		while ((entry = this.buffer.pollFirst()) != null) {
			entry.run(this.out);
		}
	}

	@Override
	public MCDOutput increaseIndent() throws IOException {
		this.buffer.add(MCDOutput::increaseIndent);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput decreaseIndent() throws IOException {
		this.buffer.add(MCDOutput::decreaseIndent);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput println() throws IOException {
		this.buffer.add(MCDOutput::println);
		return this;
	}

	@Override
	public MCDOutput print(String text) throws IOException {
		this.buffer.add(out0 -> out0.print(text));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput println(String text) throws IOException {
		this.buffer.add(out0 -> out0.println(text));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printValue(String value) throws IOException {
		this.buffer.add(out0 -> out0.printValue(value));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnValue(String value) throws IOException {
		this.buffer.add(out0 -> out0.printlnValue(value));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printComment(String comment) throws IOException {
		this.buffer.add(out0 -> out0.printComment(comment));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnComment(String comment) throws IOException {
		this.buffer.add(out0 -> out0.printlnComment(comment));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printKeyword(String keyword) throws IOException {
		this.buffer.add(out0 -> out0.printKeyword(keyword));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnKeyword(String keyword) throws IOException {
		this.buffer.add(out0 -> out0.printlnKeyword(keyword));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printOperator(String operator) throws IOException {
		this.buffer.add(out0 -> out0.printOperator(operator));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnOperator(String operator) throws IOException {
		this.buffer.add(out0 -> out0.printlnOperator(operator));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printLabel(String label) throws IOException {
		this.buffer.add(out0 -> out0.printLabel(label));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnLabel(String label) throws IOException {
		this.buffer.add(out0 -> out0.printlnLabel(label));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printError(String error) throws IOException {
		this.buffer.add(out0 -> out0.printError(error));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnError(String error) throws IOException {
		this.buffer.add(out0 -> out0.printlnError(error));
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@FunctionalInterface
	private interface Entry {

		void run(MCDOutput out) throws IOException;

	}

}
