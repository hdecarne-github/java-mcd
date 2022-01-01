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
package de.carne.mcd.jvmdecoder.classfile.bytecode;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.carne.mcd.instruction.Instruction;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * A single bytecode instruction with a variable number of operands.
 */
public class BytecodeInstruction implements Instruction {

	private final String mnemonic;
	private final OperandType[] operands;

	/**
	 * Constructs a new {@linkplain BytecodeInstruction} instance.
	 *
	 * @param mnemonic the instruction mnemonic to use.
	 * @param operands the instruction operands.
	 */
	public BytecodeInstruction(String mnemonic, OperandType[] operands) {
		this.mnemonic = mnemonic;
		this.operands = operands;
	}

	static BytecodeInstruction load(DataInput in) throws IOException {
		String mnemonic = in.readUTF();
		List<OperandType> operands = new ArrayList<>();
		char operandType;

		do {
			operandType = in.readChar();

			String operandName = in.readUTF();

			switch (operandType) {
			case 't':
				operands.add(new TableswitchOperandDecoder());
				break;
			case 'l':
				operands.add(new LookupswitchOperandDecoder());
				break;
			case 'B':
				operands.add(ByteOperandType.valueOf(operandName));
				break;
			case 'S':
				operands.add(ShortOperandType.valueOf(operandName));
				break;
			case 'I':
				operands.add(IntOperandType.valueOf(operandName));
				break;
			case '\0':
				// instruction complete
				break;
			default:
				throw new IOException("Unrecognized operand type: " + operandType + ":" + operandName);
			}
		} while (operandType != '\0');
		return new BytecodeInstruction(mnemonic, operands.toArray(new OperandType[operands.size()]));
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeUTF(this.mnemonic);
		for (OperandType operand : this.operands) {
			out.writeChar(operand.type());
			out.writeUTF(operand.name());
		}
		out.writeChar('\0');
		out.writeUTF("");
	}

	@Override
	public void decode(long ip, InstructionOpcode opcode, MCDInputBuffer in, MCDOutputBuffer out)
			throws IOException {
		if (this.operands.length > 0) {
			out.printKeyword(this.mnemonic).print(" ");
			for (OperandType operand : this.operands) {
				operand.decode((int) ip, in, out);
			}
			out.println();
		} else {
			out.printlnKeyword(this.mnemonic);
		}
	}

}
