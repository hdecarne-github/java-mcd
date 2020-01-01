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
package de.carne.mcd.common.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.common.PlainMCDOutput;

/**
 * Test {@linkplain MachineCodeDecoder} class.
 */
class MachineCodeDecoderTest {

	private static class TestMachineCodeDecoder extends MachineCodeDecoder {

		public TestMachineCodeDecoder(ByteOrder byteOrder) {
			super(TestMachineCodeDecoder.class.getSimpleName(), byteOrder);
		}

		@Override
		public void decode(ReadableByteChannel in, MCDOutput out) throws IOException {
			MCDDecodeBuffer buffer = newDecodeBuffer(in);

			out.print(Integer.toHexString(buffer.decodeI32()));
		}

	}

	private static final byte[] TEST_DATA = new byte[] { 0x01, 0x23, 0x45, 0x67 };

	@Test
	void testDecoder() throws IOException {
		TestMachineCodeDecoder decoder = new TestMachineCodeDecoder(ByteOrder.LITTLE_ENDIAN);

		Assertions.assertEquals(decoder.getClass().getSimpleName(), decoder.name());

		String decodedLE = runDecoder(decoder, TEST_DATA);

		Assertions.assertEquals("67452301", decodedLE);

		decoder.setByteOrder(ByteOrder.BIG_ENDIAN);

		String decodedBE = runDecoder(decoder, TEST_DATA);

		Assertions.assertEquals("1234567", decodedBE);
	}

	private String runDecoder(MachineCodeDecoder decoder, byte[] data) throws IOException {
		ByteArrayOutputStream decoded = new ByteArrayOutputStream();

		try (ReadableByteChannel in = Channels.newChannel(new ByteArrayInputStream(data));
				PlainMCDOutput out = new PlainMCDOutput(new PrintWriter(decoded), true)) {
			decoder.decode(in, out);
			out.flush();
		}
		return new String(decoded.toByteArray());
	}

}
