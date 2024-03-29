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
package de.carne.mcd.test.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.mcd.PlainMCDOutput;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * Test {@linkplain PlainMCDOutput} and {@linkplain MCDOutputBuffer} class.
 */
class MCDOutputTest {

	private static final String OUTPUT_BEGIN = ">>> begin";
	private static final String OUTPUT_END = "<<< end";
	private static final String OUTPUT_NORMAL = "normal";
	private static final String OUTPUT_VALUE = "value";
	private static final String OUTPUT_COMMENT = "comment";
	private static final String OUTPUT_KEYWORD = "keyword";
	private static final String OUTPUT_OPERATOR = "operator";
	private static final String OUTPUT_LABEL = "label";

	private static final String OUTPUT_EMPTY = "";
	private static final String OUTPUT_TOTAL = ">>> begin\n" + "normal\n" + "normal\n" + "value\n" + "value\n"
			+ "comment\n" + "comment\n" + "keyword\n" + "keyword\n" + "operator\n" + "operator\n" + "label\n"
			+ "label\n" + "    normal\n" + "    normal\n" + "    value\n" + "    value\n" + "    comment\n"
			+ "    comment\n" + "    keyword\n" + "    keyword\n" + "    operator\n" + "    operator\n" + "    label\n"
			+ "    label\n" + "<<< end\n" + "".replace("\n", System.lineSeparator());

	@Test
	void testPrintWriterOutput() throws IOException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				PlainMCDOutput out = new PlainMCDOutput(new PrintWriter(buffer), true)) {
			testOutput(buffer, out);
		}
	}

	@Test
	void testPrintStreamOutput() throws IOException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				PlainMCDOutput out = new PlainMCDOutput(new PrintStream(buffer), true)) {
			testOutput(buffer, out);
		}
	}

	@Test
	void testWritableChannelOutput() throws IOException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				PlainMCDOutput out = new PlainMCDOutput(Channels.newChannel(buffer), true)) {
			testOutput(buffer, out);
		}
	}

	private void testOutput(ByteArrayOutputStream buffer, PlainMCDOutput out) throws IOException {
		printOutput(new MCDOutputBuffer(out), false);
		out.flush();
		Assertions.assertEquals(OUTPUT_EMPTY, new String(buffer.toByteArray()));
		printOutput(new MCDOutputBuffer(out), true);
		out.flush();
		Assertions.assertEquals(OUTPUT_TOTAL, new String(buffer.toByteArray()));
	}

	private void printOutput(MCDOutputBuffer out, boolean autoCommit) throws IOException {
		out.setAutoCommit(autoCommit);
		out.println(OUTPUT_BEGIN);
		printOutputLines(out);
		out.increaseIndent();
		printOutputLines(out);
		out.decreaseIndent();
		out.println(OUTPUT_END);
	}

	private void printOutputLines(MCDOutputBuffer out) throws IOException {
		out.print(OUTPUT_NORMAL).println();
		out.println(OUTPUT_NORMAL);
		out.printValue(OUTPUT_VALUE).println();
		out.printlnValue(OUTPUT_VALUE);
		out.printComment(OUTPUT_COMMENT).println();
		out.printlnComment(OUTPUT_COMMENT);
		out.printKeyword(OUTPUT_KEYWORD).println();
		out.printlnKeyword(OUTPUT_KEYWORD);
		out.printOperator(OUTPUT_OPERATOR).println();
		out.printlnOperator(OUTPUT_OPERATOR);
		out.printLabel(OUTPUT_LABEL).println();
		out.printlnLabel(OUTPUT_LABEL);
	}

}
