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
package de.carne.mcd.jvm.bytecode;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.carne.mcd.common.Instruction;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;

/**
 * A single bytecode instruction with a variable number of operands.
 */
public class BytecodeInstruction implements Instruction {

	private final String mnemonic;
	private final OperandDecoder[] operands;

	/**
	 * Constructs a new {@linkplain BytecodeInstruction} instance.
	 *
	 * @param mnemonic the instruction mnemonic to use.
	 * @param operands the instruction operands.
	 */
	public BytecodeInstruction(String mnemonic, OperandDecoder[] operands) {
		this.mnemonic = mnemonic;
		this.operands = operands;
	}

	static BytecodeInstruction load(DataInput in) throws IOException {
		String mnemonic = in.readUTF();
		List<OperandDecoder> operands = new ArrayList<>();
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
		return new BytecodeInstruction(mnemonic, operands.toArray(new OperandDecoder[operands.size()]));
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeUTF(this.mnemonic);
		for (OperandDecoder operand : this.operands) {
			out.writeChar(operand.type());
			out.writeUTF(operand.name());
		}
		out.writeChar('\0');
		out.writeUTF("");
	}

	@Override
	public void decode(int pc, MCDDecodeBuffer buffer, MCDOutput out) throws IOException {
		if (this.operands.length > 0) {
			out.printKeyword(this.mnemonic).print(" ");
			for (OperandDecoder operand : this.operands) {
				operand.decode(pc, buffer, out);
			}
			out.println();
		} else {
			out.printlnKeyword(this.mnemonic);
		}
	}

}
