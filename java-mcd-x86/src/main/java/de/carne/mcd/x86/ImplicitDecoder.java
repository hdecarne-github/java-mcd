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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.carne.boot.Exceptions;
import de.carne.mcd.io.MCDInputBuffer;
import de.carne.mcd.io.MCDOutputBuffer;
import de.carne.mcd.io.MCDPrintBuffer;

/**
 * Implicit operand decoder.
 */
public class ImplicitDecoder implements NamedDecoder {

	private static final Map<String, ImplicitDecoder> IMPLICIT_DECODER_INSTANCES = new HashMap<>();

	private final MCDPrintBuffer output;

	private ImplicitDecoder(String outputString) {
		this.output = decodeOutputString(outputString);
	}

	private static MCDPrintBuffer decodeOutputString(String outputString) {
		MCDPrintBuffer output = new MCDPrintBuffer();

		try {
			StringBuilder keyword = new StringBuilder();

			for (int charIndex = 0; charIndex < outputString.length(); charIndex++) {
				char outputChar = outputString.charAt(charIndex);

				if ("[]".indexOf(outputChar) >= 0) {
					flushDecodedKeyword(output, keyword);
					output.print(new String(new char[] { outputChar }));
				} else if (":".indexOf(outputChar) >= 0) {
					flushDecodedKeyword(output, keyword);
					output.printOperator(new String(new char[] { outputChar }));
				} else if ("?".indexOf(outputChar) >= 0) {
					flushDecodedKeyword(output, keyword);
					output.printError(new String(new char[] { outputChar }));
				} else {
					keyword.append(outputChar);
				}
			}
			flushDecodedKeyword(output, keyword);
		} catch (IOException e) {
			throw Exceptions.toRuntime(e);
		}
		return output;
	}

	private static void flushDecodedKeyword(MCDPrintBuffer output, StringBuilder keyword) throws IOException {
		if (keyword.length() > 0) {
			output.printKeyword(keyword.toString());
			keyword.setLength(0);
		}
	}

	/**
	 * Gets the {@linkplain ImplicitDecoder} instance for the given output.
	 *
	 * @param outputString the output to get {@linkplain ImplicitDecoder} instance for.
	 * @return the {@linkplain ImplicitDecoder} instance for the given output.
	 */
	public static synchronized ImplicitDecoder getInstance(String outputString) {
		return IMPLICIT_DECODER_INSTANCES.computeIfAbsent(outputString, ImplicitDecoder::new);
	}

	@Override
	public char type() {
		return '*';
	}

	@Override
	public String name() {
		return this.output.toString();
	}

	@Override
	public void decode(X86DecoderState decoderState, MCDInputBuffer buffer, MCDOutputBuffer out) throws IOException {
		this.output.printTo(out);
	}

}
