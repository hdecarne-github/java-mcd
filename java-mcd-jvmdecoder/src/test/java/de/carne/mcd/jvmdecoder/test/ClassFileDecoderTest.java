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
package de.carne.mcd.jvmdecoder.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.platform.commons.JUnitException;

import de.carne.io.Closeables;
import de.carne.io.IOUtil;
import de.carne.mcd.PlainMCDOutput;
import de.carne.mcd.jvmdecoder.ClassFileDecoder;
import de.carne.test.helper.diff.Diff;
import de.carne.test.helper.diff.DiffResult;
import de.carne.util.Debug;
import de.carne.util.logging.Log;

/**
 * Test {@linkplain ClassFileDecoder} class.
 */
class ClassFileDecoderTest {

	private static final Log LOG = new Log();

	@Test
	void testJunitJar() throws IOException {
		testDecodeAll(JUnitException.class);
	}

	@Test
	void testJavaDefaultJar() throws IOException {
		testDecodeAll(Debug.class);
	}

	@Test
	void testDecodeObject() throws IOException {
		testDecode(Object.class, true);
	}

	@Test
	void testDecodeAbstractClass() throws IOException {
		testDecode(InputStream.class, true);
	}

	@Test
	void testDecodeFinalClass() throws IOException {
		testDecode(StringBuilder.class, true);
	}

	@Test
	void testDecodeGenericClass() throws IOException {
		testDecode(Optional.class, true);
	}

	@Test
	void testDecodeNestedClass() throws IOException {
		testDecode(Calendar.Builder.class, true);
	}

	@Test
	void testDecodeInterfaceClass() throws IOException {
		testDecode(ReadableByteChannel.class, true);
	}

	@Test
	void testDecodeEnumClass() throws IOException {
		testDecode(StandardOpenOption.class, true);
	}

	@Test
	void testDecodeAnnotationClass() throws IOException {
		testDecode(DisabledOnOs.class, true);
	}

	@Test
	void testDecodePackageClass() throws IOException {
		testDecode("/de/carne/package-info.class", true);
	}

	@Test
	void testDecodeModuleClass() throws IOException {
		testDecode("/module-info.class", true);
	}

	@Test
	void testDecodeTypeAnnotations() throws IOException {
		testDecode(Closeables.class, true);
	}

	@Test
	void testDecodeDecoderTestClass() throws IOException {
		testDecode(DecoderTestClass.class, false);
	}

	private String getClassResourceName(Class<?> clazz) {
		return "/" + clazz.getName().replace('.', '/') + ".class";
	}

	private void testDecodeAll(Class<?> clazz) throws IOException {
		URL clazzUrl = getClass().getResource(getClassResourceName(clazz));

		Assertions.assertEquals("jar", clazzUrl.getProtocol());

		StringWriter decodeBuffer = new StringWriter();

		try (JarFile jarFile = ((JarURLConnection) clazzUrl.openConnection()).getJarFile()) {
			ClassFileDecoder decoder = new ClassFileDecoder();
			Enumeration<JarEntry> jarEntries = jarFile.entries();

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				String jarEntryName = jarEntry.getName();

				if (jarEntryName.endsWith(".class")) {
					LOG.info("Decode class {0}...", jarEntryName);

					try (InputStream jarEntryStream = jarFile.getInputStream(jarEntry);
							ReadableByteChannel jarEntryChannel = Channels.newChannel(jarEntryStream);
							PlainMCDOutput out = new PlainMCDOutput(decodeBuffer, false)) {
						decoder.decode(jarEntryChannel, out);
					}
				}
			}
		} catch (IOException e) {
			LOG.error(e, "Decode failure; decode ouput so far:");

			throw e;
		}
	}

	private void testDecode(Class<?> clazz, boolean verify) throws IOException {
		testDecode(getClassResourceName(clazz), verify);
	}

	private void testDecode(String resource, boolean verify) throws IOException {
		LOG.info("Decode class {0}...", resource);

		ClassFileDecoder decoder = new ClassFileDecoder();
		StringWriter decodeBuffer = new StringWriter();

		try (ReadableByteChannel in = getByteCode(resource);
				PlainMCDOutput out = new PlainMCDOutput(decodeBuffer, false)) {
			decoder.decode(in, out);
		}
		if (verify) {
			String referenceOutput = getReferenceOutput(resource);
			String decodeOutput = decodeBuffer.toString();
			DiffResult<String> diffResult = Diff.lines(referenceOutput, decodeOutput);

			Assertions.assertEquals(DiffResult.lineMatch(), diffResult);
		}
	}

	private ReadableByteChannel getByteCode(String resource) throws IOException {
		InputStream resourceStream = getClass().getModule().getResourceAsStream(resource);

		if (resourceStream == null) {
			resourceStream = getClass().getResourceAsStream(resource);
		}
		return Channels.newChannel(Objects.requireNonNull(resourceStream));
	}

	private String getReferenceOutput(String resource) throws IOException {
		String referenceResource = resource.replace('/', '.').replaceAll("^\\.", "").replaceAll("\\$", "_")
				.replaceAll("\\.class$", ".txt");
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
