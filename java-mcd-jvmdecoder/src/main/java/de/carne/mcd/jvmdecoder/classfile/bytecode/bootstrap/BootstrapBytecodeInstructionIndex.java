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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import de.carne.mcd.bootstrap.InstructionIndexBuilder;
import de.carne.util.Check;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * Helper program used to fetch and process the bytecode reference for instruction index bootstrapping.
 */
public class BootstrapBytecodeInstructionIndex {

	private static final Log LOG = new Log();

	private static final File INSTRUCTION_REFERENCE_FILE = new File(
			"./src/main/resources/de/carne/mcd/jvm/classfile/bytecode/bootstrap/BytecodeInstructionReference.txt");
	private static final File INSTRUCTION_INDEX_FILE = new File(
			"./src/main/resources/de/carne/mcd/jvm/classfile/bytecode/BytecodeInstructionIndex.bin");

	private BootstrapBytecodeInstructionIndex() {
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
			case 0:
				updateInstructionIndex();
				break;
			case 1:
				updateInstructionReferences(Objects.requireNonNull(args[0]));
				updateInstructionIndex();
				break;
			default:
				Check.fail("Invalid command line: ''{0}''", Strings.join(args, ", "));
			}
		} catch (Exception e) {
			LOG.error(e, "Processing failure");
		}
	}

	private static void updateInstructionReferences(String source) throws IOException {
		try (BytecodeInstructionReferenceScraper scraper = openReference(source)) {
			BytecodeInstructionReference reference = new BytecodeInstructionReference();

			reference.load(INSTRUCTION_REFERENCE_FILE);

			BytecodeInstructionReferenceEntry referenceEntry;

			while ((referenceEntry = scraper.scrapeNext()) != null) {
				reference.addOrUpdateEntry(referenceEntry);
			}
			reference.save(INSTRUCTION_REFERENCE_FILE);
			reference.logStatus();
		}
	}

	private static void updateInstructionIndex() throws IOException {
		BytecodeInstructionReference reference = new BytecodeInstructionReference();

		reference.load(INSTRUCTION_REFERENCE_FILE);

		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		reference.build(builder);

		long totalIndexSize = builder.save(INSTRUCTION_INDEX_FILE);

		LOG.notice("Index entry count   : {0}", builder.entryCount());
		LOG.notice("Index opcode bytes  : {0}", builder.opcodeBytes());
		LOG.notice("Index position bytes: {0}", builder.positionBytes());
		LOG.notice("Index size          : {0}", totalIndexSize);
	}

	private static BytecodeInstructionReferenceScraper openReference(String source) throws IOException {
		LOG.info("Opening reference source: ''{0}''...", source);

		URL referenceUrl = new URL(source);

		return new BytecodeInstructionReferenceScraper(referenceUrl.openStream(), StandardCharsets.UTF_8);
	}

}
