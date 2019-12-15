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
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.check.Check;

/**
 * {@linkplain MCDOutput} implementation which emits the decoded to data to a {@linkplain PrintWriter} or
 * {@linkplain PrintStream}.
 */
public class PlainMCDOutput implements MCDOutput {

	private static final String INDENT = "    ";

	private final PrintWriter pw;
	private final boolean autoClose;
	private boolean closed = false;
	private int indentLevel = 0;
	private boolean newLine = true;

	/**
	 * Constructs a new {@linkplain MCDOutput} instance.
	 *
	 * @param out the {@linkplain Writer} to emit the decoded data to.
	 * @param autoClose whether to automatically close the {@linkplain PrintWriter} when this channel is closed.
	 */
	public PlainMCDOutput(Writer out, boolean autoClose) {
		this.pw = (out instanceof PrintWriter ? (PrintWriter) out : new PrintWriter(out));
		this.autoClose = autoClose;
	}

	/**
	 * Constructs a new {@linkplain MCDOutput} instance.
	 *
	 * @param ps the {@linkplain PrintStream} to emit the decoded data to.
	 * @param autoClose whether to automatically close the {@linkplain PrintWriter} when this channel is closed.
	 */
	public PlainMCDOutput(PrintStream ps, boolean autoClose) {
		this(new PrintWriter(ps), autoClose);
	}

	/**
	 * Constructs a new {@linkplain MCDOutput} instance.
	 *
	 * @param channel the {@linkplain WritableByteChannel} to emit the decoded data to.
	 * @param autoClose whether to automatically close the {@linkplain PrintWriter} when this channel is closed.
	 */
	public PlainMCDOutput(WritableByteChannel channel, boolean autoClose) {
		this(new PrintWriter(Channels.newOutputStream(channel)), autoClose);
	}

	@Override
	public Appendable append(@Nullable CharSequence csq) throws IOException {
		this.pw.append(csq);
		return this;
	}

	@Override
	public Appendable append(@Nullable CharSequence csq, int start, int end) throws IOException {
		this.pw.append(csq, start, end);
		return this;
	}

	@Override
	public Appendable append(char c) throws IOException {
		this.pw.append(c);
		return this;
	}

	@Override
	public void flush() throws IOException {
		this.pw.flush();
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
	public @NonNull MCDOutput increaseIndent() throws IOException {
		this.indentLevel++;
		return this;
	}

	@Override
	public @NonNull MCDOutput decreaseIndent() throws IOException {
		Check.isTrue(this.indentLevel > 0);

		this.indentLevel--;
		return this;
	}

	@Override
	public MCDOutput println() throws IOException {
		ensureNotClosed();
		printIndentIfNeeded();
		this.pw.println();
		this.newLine = true;
		return this;
	}

	@Override
	public MCDOutput print(String text) throws IOException {
		ensureNotClosed();
		printIndentIfNeeded();
		this.pw.print(text);
		return this;
	}

	@Override
	public MCDOutput println(String text) throws IOException {
		ensureNotClosed();
		printIndentIfNeeded();
		this.pw.println(text);
		this.newLine = true;
		return this;
	}

	@Override
	public MCDOutput printValue(String value) throws IOException {
		return print(value);
	}

	@Override
	public MCDOutput printlnValue(String value) throws IOException {
		return println(value);
	}

	@Override
	public MCDOutput printComment(String comment) throws IOException {
		return print(comment);
	}

	@Override
	public MCDOutput printlnComment(String comment) throws IOException {
		return println(comment);
	}

	@Override
	public MCDOutput printKeyword(String keyword) throws IOException {
		return print(keyword);
	}

	@Override
	public MCDOutput printlnKeyword(String keyword) throws IOException {
		return println(keyword);
	}

	@Override
	public MCDOutput printOperator(String operator) throws IOException {
		return print(operator);
	}

	@Override
	public MCDOutput printlnOperator(String operator) throws IOException {
		return println(operator);
	}

	@Override
	public MCDOutput printLabel(String label) throws IOException {
		return print(label);
	}

	@Override
	public MCDOutput printlnLabel(String label) throws IOException {
		return println(label);
	}

	private void ensureNotClosed() throws IOException {
		if (this.closed) {
			throw new ClosedChannelException();
		}
	}

	private void printIndentIfNeeded() {
		if (this.newLine) {
			for (int indentCount = 0; indentCount < this.indentLevel; indentCount++) {
				this.pw.print(INDENT);
			}
			this.newLine = false;
		}
	}

}
