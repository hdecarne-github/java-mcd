/*
 * Copyright (c) 2019 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.jvm.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import de.carne.boot.logging.Log;
import de.carne.io.Closeables;
import de.carne.io.IOUtil;
import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.common.PlainMCDOutput;
import de.carne.mcd.jvm.ClassFileDecoder;

/**
 * Test {@linkplain ClassFileDecoder} class.
 */
class ClassFileDecoderTest {

	private static final Log LOG = new Log();

	@Test
	void testDecodeObject() throws IOException {
		testDecode(Object.class);
	}

	@Test
	void testDecodeAbstractClass() throws IOException {
		testDecode(InputStream.class);
	}

	@Test
	void testDecodeFinalClass() throws IOException {
		testDecode(String.class);
	}

	@Test
	void testDecodeGenericClass() throws IOException {
		testDecode(HashMap.class);
	}

	@Test
	void testDecodeNestedClass() throws IOException {
		testDecode(Calendar.Builder.class);
	}

	@Test
	void testDecodeInterfaceClass() throws IOException {
		testDecode(ReadableByteChannel.class);
	}

	@Test
	void testDecodeEnumClass() throws IOException {
		testDecode(StandardOpenOption.class);
	}

	@Test
	void testDecodeAnnotationClass() throws IOException {
		testDecode(DisabledOnOs.class);
	}

	@Test
	void testDecodePackageClass() throws IOException {
		testDecode("package-info.class");
	}

	@Test
	void testDecodeTypeAnnotations() throws IOException {
		testDecode(Closeables.class);
	}

	private void testDecode(Class<?> clazz) throws IOException {
		testDecode("/" + clazz.getName().replace('.', '/') + ".class");
	}

	private void testDecode(String resource) throws IOException {
		ClassFileDecoder decoder = new ClassFileDecoder();
		StringWriter decodeBuffer = new StringWriter();

		try (ReadableByteChannel in = getByteCode(resource); MCDOutput out = new PlainMCDOutput(decodeBuffer, false)) {
			decoder.decode(in, out);
		}

		String decodeOutput = decodeBuffer.toString();
		String referenceOutput = getReferenceOutput(resource);

		Assertions.assertEquals(referenceOutput, decodeOutput);
	}

	private ReadableByteChannel getByteCode(String resource) {
		return Channels.newChannel(Objects.requireNonNull(getClass().getResourceAsStream(resource)));
	}

	private String getReferenceOutput(String resource) throws IOException {
		String referenceResource = resource.replace('/', '.').replaceAll("^\\.", "").replaceAll("\\$", "_")
				.replaceAll("\\.class$", ".jcf");
		String output;

		try (InputStream referenceStream = getClass().getResourceAsStream(referenceResource)) {
			if (referenceStream != null) {
				output = new String(IOUtil.readAllBytes(referenceStream));
				output.replaceAll("\\\r\\\n|\\\\r|\\\n", System.lineSeparator());
			} else {
				output = "";

				LOG.warning("No reference resource ''{0}'' defined; test will fail", referenceResource);
			}
		}
		return output;
	}

}
