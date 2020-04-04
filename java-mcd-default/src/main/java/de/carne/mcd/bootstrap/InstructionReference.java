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
package de.carne.mcd.bootstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.logging.Log;
import de.carne.mcd.instruction.InstructionIndex;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.util.Strings;

/**
 * Helper class used to create and update a instruction reference file suitable for {@linkplain InstructionIndex}
 * generation.
 *
 * @param <T> the actual instruction reference entry type.
 */
public abstract class InstructionReference<T extends InstructionReferenceEntry> {

	private static final Log LOG = new Log();

	private static final String FIELD_SEPARATOR = ";";

	private final Map<InstructionOpcode, T> referenceMap = new TreeMap<>();
	private final Set<InstructionOpcode> untouchedEntries = new HashSet<>();
	private final Set<InstructionOpcode> uptodateEntries = new HashSet<>();
	private final Set<InstructionOpcode> updatedEntries = new HashSet<>();
	private final Set<InstructionOpcode> addedEntries = new HashSet<>();

	/**
	 * Loads instruction reference entries from a file.
	 *
	 * @param file the file to load from.
	 * @throws IOException if an I/O error occurs.
	 */
	public void load(File file) throws IOException {
		load(file, StandardCharsets.UTF_8);
	}

	/**
	 * Loads instruction reference entries from a file.
	 *
	 * @param file the file to load from.
	 * @param cs the {@linkplain Charset} to use for text conversion.
	 * @throws IOException if an I/O error occurs.
	 */
	public void load(File file, Charset cs) throws IOException {
		LOG.info("Loading instruction reference from file ''{0}''...", file);

		this.referenceMap.clear();
		this.untouchedEntries.clear();
		this.uptodateEntries.clear();
		this.updatedEntries.clear();
		this.addedEntries.clear();
		try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), cs))) {
			String line;

			while ((line = lineReader.readLine()) != null) {
				T entry = newEntry(decodeEntryData(line));

				this.referenceMap.put(entry.opcode(), entry);
			}
		}
		this.untouchedEntries.addAll(this.referenceMap.keySet());

		LOG.info("Loaded {0} reference entries", this.referenceMap.size());
	}

	private InstructionReferenceEntry decodeEntryData(String line) throws IOException {
		StringTokenizer tokens = new StringTokenizer(line, FIELD_SEPARATOR);
		InstructionOpcode opcode;
		String mnemonic;
		List<String> extraFields = new ArrayList<>();

		try {
			opcode = InstructionOpcode.wrap(InstructionOpcode.parse(tokens.nextToken().trim()));
			mnemonic = tokens.nextToken().trim();
		} catch (Exception e) {
			throw new IOException("Failed to decode reference line: \"" + Strings.encode(line) + "\"", e);
		}
		while (tokens.hasMoreElements()) {
			extraFields.add(tokens.nextToken().trim());
		}
		return new InstructionReferenceEntry(opcode, mnemonic, extraFields);
	}

	private T newEntry(InstructionOpcode opcode, String mnemonic, List<String> extraFields) throws IOException {
		return newEntry(new InstructionReferenceEntry(opcode, mnemonic, extraFields));
	}

	protected abstract T newEntry(InstructionReferenceEntry entryData) throws IOException;

	/**
	 * Adds or updates a collection of instruction reference entries.
	 *
	 * @param entries the entries to add or update.
	 * @throws IOException if an I/O error occurs.
	 */
	public void addOrUpdateEntries(Iterable<T> entries) throws IOException {
		for (T entry : entries) {
			addOrUpdateEntry(entry);
		}
	}

	/**
	 * Adds or updates an instruction reference entry.
	 *
	 * @param entry the entry to add or update.
	 * @throws IOException if an I/O error occurs.
	 */
	public void addOrUpdateEntry(T entry) throws IOException {
		@Nullable T oldEntry = this.referenceMap.get(entry.opcode());
		T newEntry;

		if (oldEntry != null) {
			newEntry = mergeEntries(oldEntry, entry);
			this.untouchedEntries.remove(oldEntry.opcode());
			if (newEntry.equals(oldEntry)) {
				this.uptodateEntries.add(newEntry.opcode());
			} else {
				this.updatedEntries.add(newEntry.opcode());
			}
		} else {
			newEntry = entry;
			this.addedEntries.add(newEntry.opcode());
		}
		this.referenceMap.put(newEntry.opcode(), newEntry);
	}

	@SuppressWarnings("null")
	protected T mergeEntries(T left, T right) throws IOException {
		List<String> leftExtraFields = left.extraFields();
		List<String> rightExtraFields = right.extraFields();
		List<String> mergedExtraFields;

		if (leftExtraFields.size() > rightExtraFields.size()) {
			mergedExtraFields = new ArrayList<>(leftExtraFields);

			int extraFieldIndex = 0;

			for (String rightExtraField : right.extraFields()) {
				mergedExtraFields.set(extraFieldIndex, rightExtraField);
				extraFieldIndex++;
			}
		} else {
			mergedExtraFields = new ArrayList<>(rightExtraFields);
		}
		return newEntry(left.opcode(), right.mnemonic(), mergedExtraFields);
	}

	/**
	 * Saves all instruction reference entries to a file.
	 *
	 * @param file the file to save to.
	 * @throws IOException if an I/O error occurs.
	 */
	public void save(File file) throws IOException {
		save(file, StandardCharsets.UTF_8);
	}

	/**
	 * Saves all instruction reference entries to a file.
	 *
	 * @param file the file to save to.
	 * @param cs the {@linkplain Charset} to use for text conversion.
	 * @throws IOException if an I/O error occurs.
	 */
	public void save(File file, Charset cs) throws IOException {
		LOG.info("Saving instruction reference to file ''{0}''...", file);

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath(),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE), cs))) {
			StringBuilder lineBuffer = new StringBuilder();

			for (T entry : this.referenceMap.values()) {
				lineBuffer.setLength(0);
				lineBuffer.append(entry.opcode().toString()).append(FIELD_SEPARATOR);
				lineBuffer.append(entry.mnemonic());
				for (String extraField : entry.extraFields()) {
					lineBuffer.append(FIELD_SEPARATOR).append(extraField);
				}
				writer.write(lineBuffer.toString());
				writer.newLine();
			}
		}

		LOG.info("Saved {0} reference entries", this.referenceMap.size());
	}

	/**
	 * Builds up the instruction index by feeding all reference entries into the given
	 * {@linkplain InstructionIndexBuilder} instance.
	 *
	 * @param builder the {@linkplain InstructionIndexBuilder} instance to feed the entries into.
	 * @return the updated {@linkplain InstructionIndexBuilder} instance.
	 * @throws IOException if a reference entry conversion fails.
	 * @see InstructionReferenceEntry#toInstruction()
	 */
	public InstructionIndexBuilder build(InstructionIndexBuilder builder) throws IOException {
		for (InstructionReferenceEntry entry : this.referenceMap.values()) {
			builder.add(entry.opcode(), entry.toInstruction());
		}
		return builder;
	}

	/**
	 * Logs the current status of this instance.
	 */
	public void logStatus() {
		LOG.notice("Total reference entries: {0}", this.referenceMap.size());
		LOG.notice("             up-to-date: {0}", this.uptodateEntries.size());
		LOG.notice("              untouched: {0} {1}", this.untouchedEntries.size(), this.untouchedEntries);
		LOG.notice("                updated: {0} {1}", this.updatedEntries.size(), this.updatedEntries);
		LOG.notice("                  added: {0} {1}", this.addedEntries.size(), this.addedEntries);
	}

}
