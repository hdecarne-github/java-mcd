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
package de.carne.mcd.io;

import java.io.IOException;

import de.carne.mcd.MCDOutput;

/**
 * A buffered {@linkplain MCDOutput} implementation.
 */
public class MCDOutputBuffer implements MCDOutput, MCDBuffer {

	private final MCDOutput out;
	private final MCDPrintBuffer buffer = new MCDPrintBuffer();
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
		this.buffer.printTo(this.out);
		this.buffer.clear();
	}

	@Override
	public MCDOutput increaseIndent() throws IOException {
		this.buffer.increaseIndent();
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput decreaseIndent() throws IOException {
		this.buffer.decreaseIndent();
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput println() throws IOException {
		this.buffer.println();
		return this;
	}

	@Override
	public MCDOutput print(String text) throws IOException {
		this.buffer.print(text);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput println(String text) throws IOException {
		this.buffer.println(text);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printValue(String value) throws IOException {
		this.buffer.printValue(value);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnValue(String value) throws IOException {
		this.buffer.printlnValue(value);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printComment(String comment) throws IOException {
		this.buffer.printComment(comment);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnComment(String comment) throws IOException {
		this.buffer.printlnComment(comment);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printKeyword(String keyword) throws IOException {
		this.buffer.printKeyword(keyword);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnKeyword(String keyword) throws IOException {
		this.buffer.printlnKeyword(keyword);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printOperator(String operator) throws IOException {
		this.buffer.printOperator(operator);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnOperator(String operator) throws IOException {
		this.buffer.printlnOperator(operator);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printLabel(String label) throws IOException {
		this.buffer.printLabel(label);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnLabel(String label) throws IOException {
		this.buffer.printlnLabel(label);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printError(String error) throws IOException {
		this.buffer.printError(error);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public MCDOutput printlnError(String error) throws IOException {
		this.buffer.printlnError(error);
		if (this.autoCommit) {
			commit();
		}
		return this;
	}

	@Override
	public String toString() {
		return this.buffer.toString();
	}

}
