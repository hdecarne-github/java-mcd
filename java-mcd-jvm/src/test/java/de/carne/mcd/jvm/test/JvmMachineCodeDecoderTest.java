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
import java.io.Serializable;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import de.carne.mcd.common.MCDOutputChannel;
import de.carne.mcd.common.MCDOutputPrinterChannel;
import de.carne.mcd.jvm.JvmMachineCodeDecoder;

/**
 * Test {@linkplain JvmMachineCodeDecoder} class.
 */
class JvmMachineCodeDecoderTest {

	private static class NestedPrivateClass implements Serializable {

		private static final long serialVersionUID = 3839160795528862484L;

	}

	@Test
	void testDecodeThis() throws IOException {
		testDecode(getClass());
	}

	@Test
	void testDecodeNestedPrivate() throws IOException {
		testDecode(NestedPrivateClass.class);
	}

	@Test
	void testDecodeAbstract() throws IOException {
		testDecode(InputStream.class);
	}

	@Test
	void testDecodeFinal() throws IOException {
		testDecode(String.class);
	}

	@Test
	void testDecodeNested() throws IOException {
		testDecode(Calendar.Builder.class);
	}

	@Test
	void testDecodeObject() throws IOException {
		testDecode(Object.class);
	}

	@Test
	void testDecodeInterface() throws IOException {
		testDecode(ReadableByteChannel.class);
	}

	@Test
	void testDecodeEnum() throws IOException {
		testDecode(StandardOpenOption.class);
	}

	@Test
	void testDecodeAnnotation() throws IOException {
		testDecode(DisabledOnOs.class);
	}

	@Test
	void testDecodePackage() throws IOException {
		testDecode("package-info.class");
	}

	private void testDecode(Class<?> clazz) throws IOException {
		testDecode("/" + clazz.getName().replace('.', '/') + ".class");
	}

	private void testDecode(String resource) throws IOException {
		JvmMachineCodeDecoder decoder = new JvmMachineCodeDecoder();

		try (ReadableByteChannel in = getByteCode(resource);
				MCDOutputChannel out = new MCDOutputPrinterChannel(System.out, false)) {
			decoder.decode(in, out);
		}
	}

	private ReadableByteChannel getByteCode(String resource) {
		return Channels.newChannel(Objects.requireNonNull(getClass().getResourceAsStream(resource)));
	}

}
