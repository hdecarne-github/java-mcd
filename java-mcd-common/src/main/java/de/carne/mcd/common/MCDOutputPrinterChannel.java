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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.channels.ClosedChannelException;

/**
 * {@linkplain MCDOutputChannel} implementation which emits the decoded to data to a {@linkplain PrintWriter} or
 * {@linkplain PrintStream}.
 */
public class MCDOutputPrinterChannel implements MCDOutputChannel {

	private final PrintWriter pw;
	private final boolean autoClose;
	private boolean closed = false;

	/**
	 * Constructs a new {@linkplain MCDOutputChannel} instance.
	 *
	 * @param pw the {@linkplain PrintWriter} to emit the decoded data to.
	 * @param autoClose whether to automatically close the {@linkplain PrintWriter} when this channel is closed.
	 */
	public MCDOutputPrinterChannel(PrintWriter pw, boolean autoClose) {
		this.pw = pw;
		this.autoClose = autoClose;
	}

	/**
	 * Constructs a new {@linkplain MCDOutputChannel} instance.
	 *
	 * @param ps the {@linkplain PrintStream} to emit the decoded data to.
	 * @param autoClose whether to automatically close the {@linkplain PrintWriter} when this channel is closed.
	 */
	public MCDOutputPrinterChannel(PrintStream ps, boolean autoClose) {
		this(new PrintWriter(ps), autoClose);
	}

	@Override
	public boolean isOpen() {
		return !this.closed;
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
		if (this.autoClose) {
			this.pw.close();
		} else {
			this.pw.flush();
		}
	}

	@Override
	public MCDOutputChannel println() throws IOException {
		ensureNotClosed();
		this.pw.println();
		return this;
	}

	@Override
	public MCDOutputChannel print(String text) throws IOException {
		ensureNotClosed();
		this.pw.print(text);
		return this;
	}

	@Override
	public MCDOutputChannel println(String text) throws IOException {
		ensureNotClosed();
		this.pw.println(text);
		return this;
	}

	@Override
	public MCDOutputChannel printValue(String value) throws IOException {
		return print(value);
	}

	@Override
	public MCDOutputChannel printlnValue(String value) throws IOException {
		return println(value);
	}

	@Override
	public MCDOutputChannel printComment(String comment) throws IOException {
		return print(comment);
	}

	@Override
	public MCDOutputChannel printlnComment(String comment) throws IOException {
		return println(comment);
	}

	@Override
	public MCDOutputChannel printKeyword(String keyword) throws IOException {
		return print(keyword);
	}

	@Override
	public MCDOutputChannel printlnKeyword(String keyword) throws IOException {
		return println(keyword);
	}

	@Override
	public MCDOutputChannel printOperator(String operator) throws IOException {
		return print(operator);
	}

	@Override
	public MCDOutputChannel printlnOperator(String operator) throws IOException {
		return println(operator);
	}

	@Override
	public MCDOutputChannel printLabel(String label) throws IOException {
		return print(label);
	}

	@Override
	public MCDOutputChannel printlnLabel(String label) throws IOException {
		return println(label);
	}

	private void ensureNotClosed() throws IOException {
		if (this.closed) {
			throw new ClosedChannelException();
		}
	}

}
