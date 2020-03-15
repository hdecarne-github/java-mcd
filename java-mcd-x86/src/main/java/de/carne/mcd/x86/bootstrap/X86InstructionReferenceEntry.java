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
package de.carne.mcd.x86.bootstrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.carne.mcd.common.bootstrap.InstructionReferenceEntry;
import de.carne.mcd.common.instruction.Instruction;
import de.carne.mcd.common.instruction.InstructionOpcode;
import de.carne.mcd.x86.ImplicitOperandDecoder;
import de.carne.mcd.x86.ModRMOperandType;
import de.carne.mcd.x86.OperandType;
import de.carne.mcd.x86.X86Instruction;
import de.carne.mcd.x86.X86InstructionSignature;
import de.carne.util.Strings;

@SuppressWarnings("squid:S2160")
class X86InstructionReferenceEntry extends InstructionReferenceEntry {

	private boolean x86b16 = true;
	private boolean x86b32 = true;
	private boolean x86b64 = true;

	X86InstructionReferenceEntry(InstructionOpcode opcode, String mnemonic, String signature) {
		super(opcode, mnemonic, (Strings.notEmpty(signature) ? Arrays.asList(signature) : Collections.emptyList()));
	}

	X86InstructionReferenceEntry(InstructionReferenceEntry entryData) {
		super(entryData);
	}

	@Override
	public Instruction toInstruction() throws IOException {
		Map<Byte, X86InstructionSignature> signatures = new HashMap<>();
		List<String> signatureStrings = extraFields();

		if (!signatureStrings.isEmpty()) {
			for (String signatureString : signatureStrings) {
				StringTokenizer signatureTokens = new StringTokenizer(signatureString, ",");
				Byte opcodeExtension = X86InstructionSignature.NO_OPCODE_EXTENSION;
				String mnemonic = mnemonic();
				boolean hasModRM = false;
				List<OperandType> operands = new ArrayList<>();

				while (signatureTokens.hasMoreElements()) {
					String signatureToken = signatureTokens.nextToken().trim();

					if (signatureToken.startsWith("/")) {
						opcodeExtension = decodeOpcodeExtension(signatureToken);
						mnemonic = decodeMnemonic(signatureToken);
					} else {
						OperandType operand = getOperandDecoder(signatureToken);

						hasModRM = operand instanceof ModRMOperandType;
						operands.add(operand);
					}
				}
				signatures.put(opcodeExtension, new X86InstructionSignature(mnemonic, hasModRM, operands));
			}
		} else {
			signatures.put(X86InstructionSignature.NO_OPCODE_EXTENSION, new X86InstructionSignature(mnemonic(), false));
		}
		return new X86Instruction(signatures);
	}

	private Byte decodeOpcodeExtension(String signatureToken) throws IOException {
		int opcodeExtension = signatureToken.charAt(1) - '0';

		if (opcodeExtension < 0 || 7 < opcodeExtension) {
			throw new IOException("Invalid signature token: " + signatureToken);
		}
		return Byte.valueOf((byte) opcodeExtension);
	}

	private String decodeMnemonic(String signatureToken) {
		return signatureToken.substring(3);
	}

	private OperandType getOperandDecoder(String signatureToken) {
		OperandType decoder = null;

		for (X86Symbol symbol : X86Symbol.values()) {
			if (symbol.symbol().equals(signatureToken)) {
				decoder = symbol.decoder();
				break;
			}
		}
		if (decoder == null) {
			decoder = ImplicitOperandDecoder.fromName(signatureToken);
		}
		return decoder;
	}

	public boolean isX86b16() {
		return this.x86b16;
	}

	public void disableX86b16() {
		this.x86b16 = false;
	}

	public boolean isX86b32() {
		return this.x86b32;
	}

	public void disableX86b32() {
		this.x86b32 = false;
	}

	public boolean isX86b64() {
		return this.x86b64;
	}

	public void disableX86b64() {
		this.x86b64 = false;
	}

	public boolean isOpcdExt() {
		List<String> extraFields = extraFields();

		return !extraFields.isEmpty() && extraFields.get(0).startsWith("/");
	}

}
