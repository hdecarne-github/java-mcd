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
package de.carne.mcd.common.test.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.test.api.io.TempFile;
import de.carne.test.extension.TempPathExtension;

/**
 * Test {@linkplain MCDInputBuffer} class.
 */
@ExtendWith(TempPathExtension.class)
class MCDInputBufferTest {

	private static final byte[] TEST_BYTES = new byte[256];

	static {
		for (int testByteValue = 0; testByteValue < TEST_BYTES.length; testByteValue++) {
			TEST_BYTES[testByteValue] = (byte) testByteValue;
		}
	}

	@Test
	void testSkipAndSlice(
			@TempFile(content = { (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
					(byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b, (byte) 0x0c,
					(byte) 0x0d, (byte) 0x0e, (byte) 0x0f, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13,
					(byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x01a,
					(byte) 0x1b, (byte) 0x1c, (byte) 0x1d, (byte) 0x1e, (byte) 0x1f, (byte) 0x20 }) Path testFile)
			throws IOException {
		try (ReadableByteChannel channel = Files.newByteChannel(testFile, StandardOpenOption.READ)) {
			testSkipAndSlice(channel);
		}
		try (ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(TEST_BYTES))) {
			testSkipAndSlice(channel);
		}
	}

	private void testSkipAndSlice(ReadableByteChannel channel) throws IOException {
		MCDInputBuffer buffer = new MCDInputBuffer(channel, ByteOrder.BIG_ENDIAN);

		buffer.skip(16);

		Assertions.assertEquals((byte) 0x10, buffer.decodeI8());

		try (SeekableByteChannel slice = buffer.slice(16)) {

			Assertions.assertEquals(16, slice.size());
			Assertions.assertEquals(0, slice.position());

			slice.position(15);

			Assertions.assertEquals(15, slice.position());

			ByteBuffer byteBuffer = ByteBuffer.allocate(2);
			int read;

			read = slice.read(byteBuffer);
			byteBuffer.flip();

			Assertions.assertEquals(1, read);
			Assertions.assertEquals((byte) 0x20, byteBuffer.get());

			slice.position(0);

			Assertions.assertEquals(0, slice.position());

			byteBuffer.clear();
			read = slice.read(byteBuffer);

			Assertions.assertEquals(2, read);
			Assertions.assertEquals(2, slice.position());
		}
	}

	@Test
	void testDecodeValues() throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(TEST_BYTES))) {
			MCDInputBuffer buffer = new MCDInputBuffer(channel, ByteOrder.BIG_ENDIAN);

			buffer.setAutoCommit(false);
			Assertions.assertEquals((byte) 0x00, buffer.decodeI8());
			Assertions.assertEquals((short) 0x0102, buffer.decodeI16());
			Assertions.assertEquals(0x03040506, buffer.decodeI32());
			Assertions.assertEquals(0x0708090a0b0c0d0el, buffer.decodeI64());
			Assertions.assertEquals(Float.intBitsToFloat(0x0f101112), buffer.decodeF32());
			Assertions.assertEquals(Double.longBitsToDouble(0x131415161718191al), buffer.decodeF64());
			Assertions.assertArrayEquals(new byte[] { (byte) 0x1b, (byte) 0x1c },
					bufferToBytes(buffer.decodeI8Array(2)));
			Assertions.assertArrayEquals(new short[] { (short) 0x1d1e, (short) 0x1f20 },
					bufferToShorts(buffer.decodeI16Array(2)));
			Assertions.assertArrayEquals(new int[] { 0x21222324, 0x25262728 }, bufferToInts(buffer.decodeI32Array(2)));
			Assertions.assertArrayEquals(new long[] { 0x292a2b2c2d2e2f30l, 0x3132333435363738l },
					bufferToLongs(buffer.decodeI64Array(2)));
			buffer.discard(16);
			Assertions.assertArrayEquals(new long[] { 0x292a2b2c2d2e2f30l, 0x3132333435363738l },
					bufferToLongs(buffer.decodeI64Array(2)));
			buffer.discard();
			buffer.setAutoCommit(true);
			Assertions.assertEquals((byte) 0x00, buffer.decodeI8());
			Assertions.assertEquals((short) 0x0102, buffer.decodeI16());
			Assertions.assertEquals(0x03040506, buffer.decodeI32());
			Assertions.assertEquals(0x0708090a0b0c0d0el, buffer.decodeI64());
			Assertions.assertEquals(Float.intBitsToFloat(0x0f101112), buffer.decodeF32());
			Assertions.assertEquals(Double.longBitsToDouble(0x131415161718191al), buffer.decodeF64());
			Assertions.assertArrayEquals(new byte[] { (byte) 0x1b, (byte) 0x1c },
					bufferToBytes(buffer.decodeI8Array(2)));
			Assertions.assertArrayEquals(new short[] { (short) 0x1d1e, (short) 0x1f20 },
					bufferToShorts(buffer.decodeI16Array(2)));
			Assertions.assertArrayEquals(new int[] { 0x21222324, 0x25262728 }, bufferToInts(buffer.decodeI32Array(2)));
			Assertions.assertArrayEquals(new long[] { 0x292a2b2c2d2e2f30l, 0x3132333435363738l },
					bufferToLongs(buffer.decodeI64Array(2)));
		}
	}

	private byte[] bufferToBytes(ByteBuffer buffer) {
		byte[] bytes = new byte[buffer.remaining()];
		int byteIndex = 0;

		while (buffer.remaining() > 0) {
			bytes[byteIndex] = buffer.get();
			byteIndex++;
		}
		return bytes;
	}

	private short[] bufferToShorts(ByteBuffer buffer) {
		short[] shorts = new short[buffer.remaining() / Short.BYTES];
		int shortIndex = 0;

		while (buffer.remaining() > 0) {
			shorts[shortIndex] = buffer.getShort();
			shortIndex++;
		}
		return shorts;
	}

	private int[] bufferToInts(ByteBuffer buffer) {
		int[] ints = new int[buffer.remaining() / Integer.BYTES];
		int intIndex = 0;

		while (buffer.remaining() > 0) {
			ints[intIndex] = buffer.getInt();
			intIndex++;
		}
		return ints;
	}

	private long[] bufferToLongs(ByteBuffer buffer) {
		long[] longs = new long[buffer.remaining() / Long.BYTES];
		int longIndex = 0;

		while (buffer.remaining() > 0) {
			longs[longIndex] = buffer.getLong();
			longIndex++;
		}
		return longs;
	}

	@Test
	void testDecodeMagic() throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(TEST_BYTES))) {
			MCDInputBuffer buffer = new MCDInputBuffer(channel, ByteOrder.BIG_ENDIAN);

			Assertions.assertDoesNotThrow(() -> buffer.decodeMagic((byte) 0x00));
			Assertions.assertThrows(IOException.class, () -> buffer.decodeMagic((byte) 0x00));
			Assertions.assertDoesNotThrow(() -> buffer.decodeMagic((short) 0x0203));
			Assertions.assertThrows(IOException.class, () -> buffer.decodeMagic((short) 0x0203));
			Assertions.assertDoesNotThrow(() -> buffer.decodeMagic(0x06070809));
			Assertions.assertThrows(IOException.class, () -> buffer.decodeMagic(0x06070809));
			Assertions.assertDoesNotThrow(() -> buffer.decodeMagic(0x0e0f101112131415l));
			Assertions.assertThrows(IOException.class, () -> buffer.decodeMagic(0x0e0f101112131415l));
		}
	}

}