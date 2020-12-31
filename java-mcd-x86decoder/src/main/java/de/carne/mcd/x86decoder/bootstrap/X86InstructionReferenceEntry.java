/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.x86decoder.bootstrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.carne.mcd.bootstrap.InstructionReferenceEntry;
import de.carne.mcd.instruction.Instruction;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.mcd.x86decoder.ImplicitDecoder;
import de.carne.mcd.x86decoder.NamedDecoder;
import de.carne.mcd.x86decoder.PrefixDecoder;
import de.carne.mcd.x86decoder.X86Instruction;
import de.carne.mcd.x86decoder.X86InstructionOpcodes;
import de.carne.mcd.x86decoder.X86InstructionVariant;
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
		InstructionOpcode opcode = opcode();
		Map<Byte, X86InstructionVariant> variants = new HashMap<>();
		List<String> variantStrings = extraFields();
		PrefixDecoder prefixDecoder = X86InstructionOpcodes.getPrefixDecoder(opcode);

		if (prefixDecoder != null) {
			List<NamedDecoder> decoders = new ArrayList<>();

			decoders.add(prefixDecoder);
			variants.put(X86InstructionVariant.NO_OPCODE_EXTENSION, new X86InstructionVariant(mnemonic(), decoders));
		} else if (!variantStrings.isEmpty()) {
			for (String variantString : variantStrings) {
				StringTokenizer variantStringTokens = new StringTokenizer(variantString, ",");
				Byte opcodeExtension = X86InstructionVariant.NO_OPCODE_EXTENSION;
				String mnemonic = mnemonic();
				List<NamedDecoder> decoders = new ArrayList<>();

				while (variantStringTokens.hasMoreElements()) {
					String variantStringToken = variantStringTokens.nextToken().trim();

					if (variantStringToken.startsWith("/")) {
						opcodeExtension = decodeOpcodeExtension(variantStringToken);
						mnemonic = decodeMnemonic(variantStringToken);
					} else {
						NamedDecoder decoder = getDecoder(variantStringToken);

						decoders.add(decoder);
					}
				}
				variants.put(opcodeExtension, new X86InstructionVariant(mnemonic, decoders));
			}
		} else {
			variants.put(X86InstructionVariant.NO_OPCODE_EXTENSION, new X86InstructionVariant(mnemonic()));
		}
		return new X86Instruction(variants);
	}

	private Byte decodeOpcodeExtension(String variantStringToken) throws IOException {
		int opcodeExtension = variantStringToken.charAt(1) - '0';

		if (opcodeExtension < 0 || 7 < opcodeExtension) {
			throw new IOException("Invalid signature token: " + variantStringToken);
		}
		return Byte.valueOf((byte) opcodeExtension);
	}

	private String decodeMnemonic(String variantStringToken) {
		return variantStringToken.substring(3);
	}

	private NamedDecoder getDecoder(String variantStringToken) {
		NamedDecoder decoder = null;

		for (X86Symbol symbol : X86Symbol.values()) {
			if (symbol.symbol().equals(variantStringToken)) {
				decoder = symbol.decoder();
				break;
			}
		}
		if (decoder == null) {
			decoder = ImplicitDecoder.getInstance(variantStringToken);
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
