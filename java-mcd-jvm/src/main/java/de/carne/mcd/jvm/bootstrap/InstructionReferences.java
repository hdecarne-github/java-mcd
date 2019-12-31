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
package de.carne.mcd.jvm.bootstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class InstructionReferences implements Iterable<Reference> {

	private final String path;
	private final Map<String, Reference> referenceMap;
	private final Set<String> addedReferences = new HashSet<>();
	private final Set<String> updatedReferences = new HashSet<>();
	private final Set<String> untouchedReferences = new HashSet<>();

	private InstructionReferences(String path, Map<String, Reference> referenceMap) {
		this.path = path;
		this.referenceMap = referenceMap;
		this.untouchedReferences.addAll(this.referenceMap.keySet());
	}

	public static InstructionReferences load(String path) throws IOException {
		Map<String, Reference> referenceMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
			String line;

			while ((line = reader.readLine()) != null) {
				Reference reference = Reference.fromLine(line);

				referenceMap.put(reference.mnomic(), reference);
			}
		}
		return new InstructionReferences(path, referenceMap);
	}

	public void store() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.path, false), StandardCharsets.UTF_8))) {
			List<String> instructions = new ArrayList<>(this.referenceMap.keySet());

			instructions.sort(String::compareTo);
			for (String instruction : instructions) {
				writer.write(Objects.requireNonNull(this.referenceMap.get(instruction)).toLine());
				writer.write(System.lineSeparator());
			}
		}
	}

	public void addOrUpdateReference(Reference reference) {
		String instruction = reference.mnomic();
		Reference existingReference = this.referenceMap.get(instruction);

		if (existingReference != null) {
			existingReference.update(reference);
			this.updatedReferences.add(instruction);
		} else {
			this.referenceMap.put(instruction, reference);
			this.addedReferences.add(instruction);
		}
		this.untouchedReferences.remove(instruction);
	}

	public Set<String> addedReferences() {
		return Collections.unmodifiableSet(this.addedReferences);
	}

	public Set<String> updatedReferences() {
		return Collections.unmodifiableSet(this.updatedReferences);
	}

	public Set<String> untouchedReferences() {
		return Collections.unmodifiableSet(this.untouchedReferences);
	}

	@Override
	public Iterator<Reference> iterator() {
		return this.referenceMap.values().iterator();
	}

}
