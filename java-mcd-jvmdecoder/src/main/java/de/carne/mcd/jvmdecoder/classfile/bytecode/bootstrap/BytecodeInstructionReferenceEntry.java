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
package de.carne.mcd.jvmdecoder.classfile.bytecode.bootstrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import de.carne.mcd.bootstrap.InstructionReferenceEntry;
import de.carne.mcd.instruction.Instruction;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.mcd.jvmdecoder.classfile.bytecode.ByteOperandType;
import de.carne.mcd.jvmdecoder.classfile.bytecode.BytecodeInstruction;
import de.carne.mcd.jvmdecoder.classfile.bytecode.IntOperandType;
import de.carne.mcd.jvmdecoder.classfile.bytecode.LookupswitchOperandDecoder;
import de.carne.mcd.jvmdecoder.classfile.bytecode.OperandType;
import de.carne.mcd.jvmdecoder.classfile.bytecode.ShortOperandType;
import de.carne.mcd.jvmdecoder.classfile.bytecode.TableswitchOperandDecoder;

class BytecodeInstructionReferenceEntry extends InstructionReferenceEntry {

	BytecodeInstructionReferenceEntry(InstructionReferenceEntry entryData) {
		super(entryData);
	}

	BytecodeInstructionReferenceEntry(InstructionOpcode opcode, String mnemonic, @NonNull String... extraFields) {
		super(opcode, mnemonic, Arrays.asList(extraFields));
	}

	@Override
	public Instruction toInstruction() throws IOException {
		List<OperandType> operands = new ArrayList<>();

		for (String extraField : extraFields()) {
			if (extraField.equals("t")) {
				operands.add(new TableswitchOperandDecoder());
			} else if (extraField.equals("l")) {
				operands.add(new LookupswitchOperandDecoder());
			} else if (extraField.startsWith("B:")) {
				operands.add(ByteOperandType.valueOf(extraField.substring(2)));
			} else if (extraField.startsWith("S:")) {
				operands.add(ShortOperandType.valueOf(extraField.substring(2)));
			} else if (extraField.startsWith("I:")) {
				operands.add(IntOperandType.valueOf(extraField.substring(2)));
			} else {
				throw new IOException("Unrecognized operand: " + extraField);
			}
		}
		return new BytecodeInstruction(mnemonic(), operands.toArray(new OperandType[operands.size()]));
	}

}
