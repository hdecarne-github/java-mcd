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
package de.carne.mcd.x86.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.io.IOUtil;
import de.carne.mcd.PlainMCDOutput;
import de.carne.mcd.x86.X86Decoder;
import de.carne.mcd.x86.X86b16Decoder;
import de.carne.mcd.x86.X86b32Decoder;
import de.carne.mcd.x86.X86b64Decoder;
import de.carne.test.helper.diff.Diff;
import de.carne.test.helper.diff.DiffResult;
import de.carne.util.logging.Log;

/**
 * Test {@linkplain X86b16Decoder} class.
 */
class X86DecoderTest {

	private static final Log LOG = new Log();

	@Test
	void testX86b16Decoder() throws IOException {
		testX86Decoder(new X86b16Decoder(), TestFiles.WINDOWS_EXE.getPath(), 0x40, 184);
	}

	@Test
	void testX86b32Decoder() throws IOException {
		testX86Decoder(new X86b32Decoder(), TestFiles.WINDOWS_EXE.getPath(), 0x400, 4096);
	}

	@Test
	void testX86b64Decoder() throws IOException {
		testX86Decoder(new X86b64Decoder(), TestFiles.WINDOWS64_EXE.getPath(), 0x400, 4096);
	}

	private void testX86Decoder(X86Decoder decoder, Path file, long offset, int length) throws IOException {
		StringWriter decodeBuffer = new StringWriter();

		try (ReadableByteChannel code = getCode(file, offset, length);
				PlainMCDOutput out = new PlainMCDOutput(decodeBuffer, false)) {
			decoder.decode(code, out);
		}

		String referenceOutput = getReferenceOutput(decoder.getClass().getSimpleName() + ".txt");
		String decodeOutput = decodeBuffer.toString();
		DiffResult<String> diffResult = Diff.lines(referenceOutput, decodeOutput);

		Assertions.assertEquals(DiffResult.lineMatch(), diffResult);
	}

	private ReadableByteChannel getCode(Path path, long offset, int length) throws IOException {
		byte[] code = new byte[length];

		try (FileChannel file = FileChannel.open(path, StandardOpenOption.READ)) {
			ByteBuffer buffer = ByteBuffer.wrap(code);

			file.read(buffer, offset);
		}
		return Channels.newChannel(new ByteArrayInputStream(code));
	}

	private String getReferenceOutput(String resource) throws IOException {
		String output;

		try (InputStream referenceStream = getClass().getResourceAsStream(resource)) {
			if (referenceStream != null) {
				output = new String(IOUtil.readAllBytes(referenceStream));
				output.replaceAll("\\\r\\\n|\\\\r|\\\n", System.lineSeparator());
			} else {
				output = "";

				LOG.warning("No reference resource ''{0}'' defined; test will fail", resource);
			}
		}
		return output;
	}

}
