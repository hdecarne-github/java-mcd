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
package de.carne.mcd.jvm.bootstrap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import de.carne.boot.check.Check;
import de.carne.boot.logging.Log;
import de.carne.mcd.common.InstructionIndexBuilder;
import de.carne.mcd.common.Opcode;
import de.carne.mcd.jvm.bytecode.BytecodeInstructionV;
import de.carne.util.Strings;

/**
 * Helper program responsible for fetching raw instruction reference data and merging it into the InstructionReference
 * file for further processing.
 */
public class BootstrapInstructionIndex {

	private static final Log LOG = new Log();

	private static final String INSTRUCTION_REFERENCES_PATH = "./src/main/resources/de/carne/mcd/jvm/bootstrap/InstructionReferences.txt";
	private static final String INSTRUCTION_INDEX_PATH = "./src/main/resources/de/carne/mcd/jvm/bytecode/BytecodeInstructionIndex.index";

	private BootstrapInstructionIndex() {
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
		} catch (IOException e) {
			LOG.error(e, "Processing failure");
		}
	}

	private static void updateInstructionReferences(String source) throws IOException {
		try (ReferenceScraper scraper = openReference(source)) {
			InstructionReferences references = InstructionReferences.load(INSTRUCTION_REFERENCES_PATH);
			int referenceCount = 0;
			Reference reference;

			while ((reference = scraper.scrapeNext()) != null) {
				references.addOrUpdateReference(reference);
				referenceCount++;
			}
			references.store();

			LOG.notice("Processed references: {0}", referenceCount);
			LOG.notice("Added references    : {0}", references.addedReferences());
			LOG.notice("Updated references  : {0}", references.updatedReferences());
			LOG.notice("Untouched references: {0}", references.untouchedReferences());
		}
	}

	private static void updateInstructionIndex() throws IOException {
		InstructionReferences references = InstructionReferences.load(INSTRUCTION_REFERENCES_PATH);
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		for (Reference reference : references) {
			LOG.debug("Adding instruction to index: {0}", reference);

			Opcode opcode = reference.opcode();
			String mnomic = reference.mnomic();

			builder.add(opcode, new BytecodeInstructionV(mnomic));
		}

		Path instructionIndexPath = Paths.get(INSTRUCTION_INDEX_PATH);

		LOG.notice("Storing instruction index: ''{0}''...", instructionIndexPath);

		long totalIndexSize;

		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(instructionIndexPath,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))) {
			totalIndexSize = builder.store(out);
		}

		LOG.notice("Index entry count   : {0}", builder.entryCount());
		LOG.notice("Index opcode bytes  : {0}", builder.opcodeBytes());
		LOG.notice("Index position bytes: {0}", builder.positionBytes());
		LOG.notice("Index size          : {0}", totalIndexSize);
	}

	private static ReferenceScraper openReference(String source) throws IOException {
		LOG.info("Opening reference source: ''{0}''...", source);

		URL referenceUrl = new URL(source);

		return new ReferenceScraper(referenceUrl.openStream(), StandardCharsets.UTF_8);
	}

}
