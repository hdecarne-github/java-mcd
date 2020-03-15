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
package de.carne.mcd.common.bootstrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.check.Check;
import de.carne.mcd.common.instruction.Instruction;
import de.carne.mcd.common.instruction.InstructionOpcode;
import de.carne.util.Strings;

/**
 * Helper class representing a single entry/line in an instruction reference file.
 *
 * @see InstructionReference
 */
public class InstructionReferenceEntry {

	/**
	 * "No value" field
	 */
	public static final String NO_VALUE = "-";

	private final InstructionOpcode opcode;
	private final String mnemonic;
	private final List<String> extraFields;

	protected InstructionReferenceEntry(InstructionOpcode opcode, String mnemonic, List<String> extraFields) {
		this.opcode = opcode;
		this.mnemonic = mnemonic;

		List<String> normalizedExtraFields = new ArrayList<>(extraFields.size());

		for (String extraField : extraFields) {
			normalizedExtraFields.add(Strings.isEmpty(extraField) ? NO_VALUE : extraField);
		}
		this.extraFields = normalizedExtraFields;
	}

	protected InstructionReferenceEntry(InstructionReferenceEntry entryData) {
		this(entryData.opcode, entryData.mnemonic, entryData.extraFields);
	}

	/**
	 * Gets the {@linkplain InstructionOpcode} this entry defines.
	 *
	 * @return the {@linkplain InstructionOpcode} this entry defines.
	 */
	public InstructionOpcode opcode() {
		return this.opcode;
	}

	/**
	 * Gets the mnemonic associated with this entry's opcode.
	 *
	 * @return the mnemonic associated with this entry's opcode.
	 */
	public String mnemonic() {
		return this.mnemonic;
	}

	/**
	 * Gets the mnemonic associated with this entry's opcode.
	 *
	 * @return the mnemonic associated with this entry's opcode.
	 */
	public List<String> extraFields() {
		return Collections.unmodifiableList(this.extraFields);
	}

	/**
	 * Gets the value of a specific extra field.
	 *
	 * @param index the index of the extra field to get.
	 * @return the extra field value.
	 */
	public String getExtraField(int index) {
		return this.extraFields.get(index);
	}

	/**
	 * Sets the value of a specific extra field.
	 *
	 * @param index the index of the extra field to set.
	 * @param value the value to set.
	 */
	public void setExtraField(int index, String value) {
		this.extraFields.set(index, value);
	}

	/**
	 * Adds an additional number of extra fields to this entry.
	 * 
	 * @param additionalExtraFields the additional extra fields to add.
	 */
	public void addExtraFields(List<String> additionalExtraFields) {
		this.extraFields.addAll(additionalExtraFields);
	}

	/**
	 * Converts this entry to the corresponding {@linkplain Instruction} instance.
	 *
	 * @return the {@linkplain Instruction} instance represented by this entry.
	 * @throws IOException if the conversion fails.
	 */
	public Instruction toInstruction() throws IOException {
		// Should never be called (in this class)
		throw Check.fail();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.opcode, this.mnemonic);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return (this == obj
				|| (obj instanceof InstructionReferenceEntry && deepEquals((InstructionReferenceEntry) obj)));
	}

	private boolean deepEquals(InstructionReferenceEntry obj) {
		return this.opcode.equals(obj.opcode) && this.mnemonic.equals(obj.mnemonic)
				&& this.extraFields.equals(obj.extraFields);
	}

	@Override
	public String toString() {
		return this.opcode + " " + this.mnemonic;
	}

}
