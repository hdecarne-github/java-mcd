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
package de.carne.mcd.x86decoder.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.carne.mcd.bootstrap.InstructionIndexBuilder;
import de.carne.util.Check;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * Helper program used to fetch and process the x86_16 reference for instruction index bootstrapping.
 */
public class BootstrapX86b64InstructionIndex {

	private static final Log LOG = new Log();

	private static final File INSTRUCTION_REFERENCE_FILE = new File(
			"./src/main/resources/de/carne/mcd/x86/bootstrap/X86b64InstructionReference.txt");
	private static final File INSTRUCTION_INDEX_FILE = new File(
			"./src/main/resources/de/carne/mcd/x86/X86b64InstructionIndex.bin");

	private BootstrapX86b64InstructionIndex() {
		// Prevent instantiation
	}

	/**
	 * Executes the program.
	 *
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		try {
			switch (args.length) {
			case 1:
				updateInstructionReference(Objects.requireNonNull(args[0]));
				updateInstructionIndex();
				break;
			case 2:
				updateInstructionIndex();
				break;
			default:
				Check.fail("Invalid command line: ''{0}''", Strings.join(args, ", "));
			}
		} catch (Exception e) {
			LOG.error(e, "Processing failure");
		}
	}

	private static void updateInstructionReference(String source) throws IOException {
		X86InstructionReferenceScraper scraper = new X86InstructionReferenceScraper(new X86b64Mode());

		scraper.scrape(source);

		X86InstructionReference reference = new X86InstructionReference();

		reference.addOrUpdateEntries(scraper);
		reference.save(INSTRUCTION_REFERENCE_FILE);
	}

	private static void updateInstructionIndex() throws IOException {
		X86InstructionReference reference = new X86InstructionReference();

		reference.load(INSTRUCTION_REFERENCE_FILE);

		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		reference.build(builder);

		long totalIndexSize = builder.save(INSTRUCTION_INDEX_FILE);

		LOG.notice("Index entry count   : {0}", builder.entryCount());
		LOG.notice("Index opcode bytes  : {0}", builder.opcodeBytes());
		LOG.notice("Index position bytes: {0}", builder.positionBytes());
		LOG.notice("Index size          : {0}", totalIndexSize);
	}

}
