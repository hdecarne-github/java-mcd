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
package de.carne.mcd.jvmdecoder.classfile.bytecode.bootstrap;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.util.Strings;
import de.carne.util.logging.Log;

final class BytecodeInstructionReferenceScraper implements Closeable {

	private static final Log LOG = new Log();

	private final BufferedReader in;
	private final Deque<BytecodeInstructionReferenceEntry> entries = new LinkedList<>();

	BytecodeInstructionReferenceScraper(InputStream referenceStream, Charset cs) {
		this.in = new BufferedReader(new InputStreamReader(referenceStream, cs));
	}

	@Nullable
	public BytecodeInstructionReferenceEntry scrapeNext() throws IOException {
		return (!this.entries.isEmpty() || scrapNextReference() ? this.entries.pop() : null);
	}

	@Override
	public void close() throws IOException {
		this.in.close();
	}

	private static final Set<String> IGNORED_INSTRUCTIONS = new HashSet<>();

	static {
		IGNORED_INSTRUCTIONS.add("dup2_x1");
		IGNORED_INSTRUCTIONS.add("dup2_x2");
		IGNORED_INSTRUCTIONS.add("pop2");
		IGNORED_INSTRUCTIONS.add("wide");
	}

	private static final Pattern INSTRUCTION_ENTRY_PATTERN = Pattern.compile(
			".*<h3 class=\"title\"><a name=\"jvms-6\\.5\\..+\"></a><span class=\"emphasis\"><em>(.*)</em></span></h3>.*");
	private static final Pattern SECTION_PATTERN = Pattern.compile(".*<div class=\"section\" title=\"(.+)\">.*");
	private static final Pattern FORMAT_PATTERN = Pattern.compile(".*<span class=\"emphasis\"><em>(.+)</em></span>.*");
	private static final Pattern FORM1_PATTERN = Pattern
			.compile(".*<p class=\"norm\"><span class=\"emphasis\"><em>(.+)</em></span> = (\\d+) .*");
	private static final Pattern FORM2_PATTERN = Pattern.compile(".*<p class=\"norm\">(.+) = (\\d+) .*");
	private static final Pattern PENDING_END_PARAGRAPH_PATTERN = Pattern.compile(".*</p>.*");
	private static final Pattern OPERAND_STACK_IN1_PATTERN = Pattern
			.compile(".*<p class=\"norm\">\\.\\.\\.[,]?(.*)<span class=\"symbol\">&#8594;</span>.*");
	private static final Pattern OPERAND_STACK_IN2_PATTERN = Pattern.compile(".*<p class=\"norm\">(No change)</p>.*");
	private static final Pattern OPERAND_STACK_OUT1_PATTERN = Pattern
			.compile(".*<p class=\"norm\">(\\[empty\\])</p>.*");
	private static final Pattern OPERAND_STACK_OUT2_PATTERN = Pattern
			.compile(".*<p class=\"norm\">[\\\\.]{0,3}[,]?(.*)</p>");
	private static final Pattern OPERAND_STACK_OUT3_PATTERN = Pattern
			.compile(".*<p class=\"norm\">[\\.]{0,3}[,]?[ ]?(&lt;.*&gt;)");

	private static final String NO_OPERAND_STACK_CHANGE = "No change";

	@SuppressWarnings("squid:S3776")
	private boolean scrapNextReference() throws IOException {
		String line;

		LOG.debug("Scraping next reference...");

		while (this.entries.isEmpty() && (line = readLine()) != null) {
			Matcher instructionEntryMatcher = INSTRUCTION_ENTRY_PATTERN.matcher(line);

			if (instructionEntryMatcher.matches()) {
				String instruction = decodeHtml(Objects.requireNonNull(instructionEntryMatcher.group(1)));

				if (IGNORED_INSTRUCTIONS.contains(instruction)) {
					LOG.notice("Ignoring instruction reference: {0}", instruction);

					continue;
				}

				LOG.notice("Processing instruction reference: {0}", instruction);

				InstructionFormat format = null;
				List<InstructionForm> forms = null;
				String operandStackIn = null;
				String operandStackOut = null;

				while (true) {
					Matcher sectionMatcher = SECTION_PATTERN.matcher(safeReadLine());

					if (sectionMatcher.matches()) {
						String section = decodeHtml(Objects.requireNonNull(sectionMatcher.group(1)));

						LOG.debug("Scraping section ''{0}''", section);

						if ("Operation".equals(section)) {
							// Ignore section
						} else if ("Format".equals(section)) {
							format = scrapeFormat();
						} else if ("Forms".equals(section)) {
							forms = scrapForms();
						} else if ("Operand Stack".equals(section)) {
							operandStackIn = scrapeOperandStackIn();
							if (NO_OPERAND_STACK_CHANGE.equals(operandStackIn)) {
								operandStackIn = "";
								operandStackOut = "";
							} else {
								operandStackOut = scrapeOperandStackOut();
							}
							break;
						}
					}
				}
				if (format == null || forms == null || operandStackIn == null || operandStackOut == null) {
					throw new IOException("Scrape failure");
				}

				LOG.info(" format: {0}", format);
				LOG.info(" forms: {0}", Strings.join(forms, "|"));

				for (InstructionForm form : forms) {
					this.entries.add(new BytecodeInstructionReferenceEntry(form.opcode(), form.mnemonic()));
				}
			}
		}
		return !this.entries.isEmpty();
	}

	private InstructionFormat scrapeFormat() throws IOException {
		LOG.debug("Scraping format...");

		Matcher formatMatcher;

		do {
			formatMatcher = FORMAT_PATTERN.matcher(safeReadLine());
		} while (!formatMatcher.matches());

		String instruction = decodeHtml(Objects.requireNonNull(formatMatcher.group(1)));
		List<String> arguments = new ArrayList<>();

		formatMatcher = FORMAT_PATTERN.matcher(safeReadLine());
		while (formatMatcher.matches()) {
			arguments.add(decodeHtml(Objects.requireNonNull(formatMatcher.group(1))));
			formatMatcher = FORMAT_PATTERN.matcher(safeReadLine());
		}
		return new InstructionFormat(instruction, arguments);
	}

	private List<InstructionForm> scrapForms() throws IOException {
		LOG.debug("Scraping forms...");

		List<InstructionForm> forms = new ArrayList<>();
		Matcher formMatcher;

		do {
			formMatcher = anyMatcher(safeReadLine(), FORM1_PATTERN, FORM2_PATTERN);
		} while (!formMatcher.matches());
		do {
			if (formMatcher.groupCount() != 0) {
				String mnemonic = Objects.requireNonNull(formMatcher.group(1));
				byte[] opcodes = new byte[] { decodeByte(Objects.requireNonNull(formMatcher.group(2))) };

				forms.add(new InstructionForm(mnemonic, opcodes));
			}
			formMatcher = anyMatcher(safeReadLine(), FORM1_PATTERN, FORM2_PATTERN, PENDING_END_PARAGRAPH_PATTERN);
		} while (formMatcher.matches());
		return forms;
	}

	private static final String[][] DECODE_OPERAND_STACK_TABLE = { { "<span class=\"emphasis\"><em>", "" },
			{ "</em></span>", "" }, { "<code class=\"literal\">", "" }, { "</code>", "" }, { "&lt;", "<" },
			{ "&gt;", ">" }, { "[empty]", "" } };

	private String scrapeOperandStackIn() throws IOException {
		LOG.debug("Scraping operand stack in...");

		Matcher operandStackInMatcher;

		do {
			operandStackInMatcher = anyMatcher(safeReadLine(), OPERAND_STACK_IN1_PATTERN, OPERAND_STACK_IN2_PATTERN);
		} while (!operandStackInMatcher.matches());

		return decodeText(Objects.requireNonNull(operandStackInMatcher.group(1)), DECODE_OPERAND_STACK_TABLE).trim();
	}

	private String scrapeOperandStackOut() throws IOException {
		LOG.debug("Scraping operand stack out...");

		Matcher operandStackOutMatcher;

		do {
			operandStackOutMatcher = anyMatcher(safeReadLine(), OPERAND_STACK_OUT1_PATTERN, OPERAND_STACK_OUT2_PATTERN,
					OPERAND_STACK_OUT3_PATTERN, PENDING_END_PARAGRAPH_PATTERN);
		} while (operandStackOutMatcher.matches() && operandStackOutMatcher.groupCount() == 0);
		if (!operandStackOutMatcher.matches()) {
			throw new IOException("Failed to match operand stack out argument(s)");
		}

		String out;

		if (operandStackOutMatcher.groupCount() != 0) {
			out = decodeText(Objects.requireNonNull(operandStackOutMatcher.group(1)), DECODE_OPERAND_STACK_TABLE)
					.trim();
		} else {
			out = "";
		}
		return out;
	}

	private Matcher anyMatcher(String input, Pattern pattern, Pattern... extraPatterns) {
		Matcher matcher = pattern.matcher(input);

		for (Pattern extraPattern : extraPatterns) {
			if (matcher.matches()) {
				break;
			}
			matcher = extraPattern.matcher(input);
		}
		return matcher;
	}

	@Nullable
	private String readLine() throws IOException {
		String line = this.in.readLine();

		if (line != null) {
			LOG.trace("Processing line: {0}", line);
		}
		return line;
	}

	private String safeReadLine() throws IOException {
		String line = readLine();

		if (line == null) {
			throw new EOFException("Unexpected EOF");
		}
		return line;
	}

	private static final String[][] DECODE_HTML_TABLE = { { "&lt;", "<" }, { "&gt;", ">" }, { "&nbsp;", " " } };

	private String decodeHtml(String html) {
		return decodeText(html, DECODE_HTML_TABLE);
	}

	private String decodeText(String text, String[][] decodeTable) {
		String decoded = text;

		for (String[] mapping : decodeTable) {
			decoded = decoded.replace(mapping[0], mapping[1]);
		}
		return decoded;
	}

	private byte decodeByte(String text) throws IOException {
		int byteValue;

		try {
			byteValue = Integer.parseInt(text);
		} catch (NumberFormatException e) {
			throw new IOException("Failed to decode byte value: '" + text + "'", e);
		}
		if (byteValue < 0 || 255 < byteValue) {
			throw new IOException("Byte value out of range: " + text);
		}
		return (byte) (byteValue & 0xff);
	}

}
