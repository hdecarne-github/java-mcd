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
package de.carne.mcd.jvm.test;

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

import javax.crypto.Cipher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import de.carne.boot.logging.Log;
import de.carne.io.Closeables;
import de.carne.io.IOUtil;
import de.carne.mcd.common.io.PlainMCDOutput;
import de.carne.mcd.jvm.ClassFileDecoder;
import de.carne.util.Debug;

/**
 * Test {@linkplain ClassFileDecoder} class.
 */
class ClassFileDecoderTest {

	private static final Log LOG = new Log();

	@Test
	void testDecodeJceJar() throws IOException {
		testDecodeAll(Cipher.class);
	}

	@Test
	void testJavaDefaultJar() throws IOException {
		testDecodeAll(Debug.class);
	}

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
		testDecode(Optional.class);
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
		testDecode("/de/carne/boot/package-info.class");
	}

	@Test
	void testDecodeTypeAnnotations() throws IOException {
		testDecode(Closeables.class);
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

	private void testDecode(Class<?> clazz) throws IOException {
		testDecode(getClassResourceName(clazz));
	}

	private void testDecode(String resource) throws IOException {
		LOG.info("Decode class {0}...", resource);

		ClassFileDecoder decoder = new ClassFileDecoder();
		StringWriter decodeBuffer = new StringWriter();

		try (ReadableByteChannel in = getByteCode(resource);
				PlainMCDOutput out = new PlainMCDOutput(decodeBuffer, false)) {
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
