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
package de.carne.mcd.x86;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.carne.mcd.common.instruction.Instruction;
import de.carne.mcd.common.instruction.InstructionOpcode;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;

/**
 * A single x86 instruction.
 */
public class X86Instruction implements Instruction {

	private final Map<Byte, X86InstructionSignature> signatures;

	/**
	 * Constructs a new {@linkplain X86Instruction} instance.
	 *
	 * @param signatures the instruction signatures to use.
	 */
	public X86Instruction(Map<Byte, X86InstructionSignature> signatures) {
		this.signatures = signatures;
	}

	static X86Instruction load(DataInput in) throws IOException {
		Map<Byte, X86InstructionSignature> signatures = new HashMap<>();
		int signatureCount = in.readInt();

		for (int signatureIndex = 0; signatureIndex < signatureCount; signatureIndex++) {
			Byte opcodeExtension = Byte.valueOf(in.readByte());
			String mnemonic = in.readUTF();
			boolean hasModRM = false;
			List<OperandType> operands = new ArrayList<>();
			char operandType;

			do {
				operandType = in.readChar();

				String operandName = in.readUTF();

				switch (operandType) {
				case 'i':
					operands.add(ImmediateOperandType.valueOf(operandName));
					break;
				case 'm':
					hasModRM = true;
					operands.add(ModRMOperandType.valueOf(operandName));
					break;
				case '*':
					operands.add(ImplicitOperandDecoder.fromName(operandName));
					break;
				case '\0':
					// instruction complete
					break;
				default:
					throw new IOException("Unrecognized operand type: " + operandType + ":" + operandName);
				}
			} while (operandType != '\0');
			signatures.put(opcodeExtension, new X86InstructionSignature(mnemonic, hasModRM, operands));
		}
		return new X86Instruction(signatures);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(this.signatures.size());
		for (Map.Entry<Byte, X86InstructionSignature> entry : this.signatures.entrySet()) {
			out.write(entry.getKey().byteValue());

			X86InstructionSignature signature = entry.getValue();

			out.writeUTF(signature.mnemonic());

			for (OperandType operand : signature.operands()) {
				out.writeChar(operand.type());
				out.writeUTF(operand.name());
			}
			out.writeChar('\0');
			out.writeUTF("");
		}
	}

	@Override
	public void decode(long ip, InstructionOpcode opcode, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		X86InstructionSignature signature = this.signatures.get(X86InstructionSignature.NO_OPCODE_EXTENSION);
		byte modrmByte = 0;

		if (signature == null) {
			modrmByte = in.decodeI8();

			Byte opcodeExtension = Byte.valueOf((byte) ((modrmByte >> 3) & 0x7));

			signature = this.signatures.get(opcodeExtension);
			if (signature == null) {
				throw new IOException("Failed to decode extended opcode: " + opcode + " /" + opcodeExtension);
			}
		} else if (signature.hasModRM()) {
			modrmByte = in.decodeI8();
		}
		out.printKeyword(signature.mnemonic());

		int operandIndex = 0;

		for (OperandType operand : signature.operands()) {
			out.print(operandIndex == 0 ? " " : ", ");
			operand.decode(ip, modrmByte, in, out);
			operandIndex++;
		}
		out.println();
	}

}
