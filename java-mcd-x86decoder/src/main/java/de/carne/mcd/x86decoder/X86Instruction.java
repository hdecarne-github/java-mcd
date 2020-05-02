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
package de.carne.mcd.x86decoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.carne.mcd.MachineCodeDecoder;
import de.carne.mcd.instruction.Instruction;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;

/**
 * A single x86 instruction.
 */
public class X86Instruction implements Instruction {

	private final Map<Byte, X86InstructionVariant> variants;

	/**
	 * Constructs a new {@linkplain X86Instruction} instance.
	 *
	 * @param variants the possible instruction variants.
	 */
	public X86Instruction(Map<Byte, X86InstructionVariant> variants) {
		this.variants = variants;
	}

	/**
	 * Loads a {@linkplain X86Instruction} instance from a stream.
	 *
	 * @param in the {@linkplain DataInput} instance to load from.
	 * @return the loaded {@linkplain X86Instruction} instance.
	 * @throws IOException if an I/O error occurs.
	 */
	public static X86Instruction load(DataInput in) throws IOException {
		Map<Byte, X86InstructionVariant> variants = new HashMap<>();
		int variantCount = in.readInt();

		for (int variantIndex = 0; variantIndex < variantCount; variantIndex++) {
			Byte opcodeExtension = Byte.valueOf(in.readByte());
			String mnemonic = in.readUTF();
			List<NamedDecoder> decoders = new ArrayList<>();
			char decoderType;

			do {
				decoderType = in.readChar();

				String decoderName = in.readUTF();

				switch (decoderType) {
				case 'p':
					decoders.add(PrefixDecoder.valueOf(decoderName));
					break;
				case 'm':
					decoders.add(ModRMDecoder.valueOf(decoderName));
					break;
				case 'i':
					decoders.add(ImmediateDecoder.valueOf(decoderName));
					break;
				case '*':
					decoders.add(ImplicitDecoder.getInstance(decoderName));
					break;
				case '\0':
					// instruction complete
					break;
				default:
					throw new IOException("Unrecognized decoder type: " + decoderType + ":" + decoderName);
				}
			} while (decoderType != '\0');
			variants.put(opcodeExtension, new X86InstructionVariant(mnemonic, decoders));
		}
		return new X86Instruction(variants);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(this.variants.size());
		for (Map.Entry<Byte, X86InstructionVariant> entry : this.variants.entrySet()) {
			out.write(entry.getKey().byteValue());

			X86InstructionVariant variant = entry.getValue();

			out.writeUTF(variant.mnemonic());

			for (NamedDecoder operand : variant.decoders()) {
				out.writeChar(operand.type());
				out.writeUTF(operand.name());
			}
			out.writeChar('\0');
			out.writeUTF("");
		}
	}

	@Override
	public void decode(long ip, InstructionOpcode opcode, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		X86DecoderState decoderState = MachineCodeDecoder.getDecoder(X86Decoder.class).state();
		X86InstructionVariant signature = this.variants.get(X86InstructionVariant.NO_OPCODE_EXTENSION);

		if (signature == null) {
			ModRM modRM = decoderState.setModRM(in.decodeI8());
			Byte opcodeExtension = Byte.valueOf((byte) modRM.regOrOpcodeIndex());

			signature = this.variants.get(opcodeExtension);
			if (signature == null) {
				throw new IOException("Failed to decode extended opcode: " + opcode + " /" + opcodeExtension);
			}
		} else if (signature.hasModRM()) {
			decoderState.setModRM(in.decodeI8());
		}
		if (!signature.isPrefix()) {
			out.printKeyword(signature.mnemonic());

			int operandIndex = 0;

			for (NamedDecoder operand : signature.decoders()) {
				out.print(operandIndex == 0 ? " " : ", ");
				operand.decode(decoderState, in, out);
				operandIndex++;
			}
			out.println();
		} else {
			for (NamedDecoder operand : signature.decoders()) {
				operand.decode(decoderState, in, out);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		for (X86InstructionVariant signature : this.variants.values()) {
			if (buffer.length() > 0) {
				buffer.append(", ");
			}
			buffer.append(signature);
		}
		return buffer.toString();
	}

}
