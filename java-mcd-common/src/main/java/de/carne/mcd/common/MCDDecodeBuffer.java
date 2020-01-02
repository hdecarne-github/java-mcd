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
package de.carne.mcd.common;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.check.Check;
import de.carne.boot.logging.Log;
import de.carne.io.Defaults;

/**
 * Helper class providing the low level read and decoding functions for byte channel access.
 */
public final class MCDDecodeBuffer {

	private static final Log LOG = new Log();

	private static final String MESSAGE_UNEXPECTED_MAGIC_VALUE = "Unexpected magic value: ";

	private final ReadableByteChannel in;
	private ByteBuffer decodeBuffer;
	private @Nullable ByteBuffer rereadBuffer = null;
	private long totalRead = 0;

	/**
	 * Constructs a new {@linkplain MCDDecodeBuffer} instance.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to decode from.
	 * @param byteOrder the {@linkplain ByteOrder} to use for decoding.
	 */
	public MCDDecodeBuffer(ReadableByteChannel in, ByteOrder byteOrder) {
		this.in = in;
		this.decodeBuffer = ByteBuffer.allocate(Defaults.DEFAULT_BUFFER_SIZE).order(byteOrder);
	}

	/**
	 * Gets the total number of bytes ready via this {@linkplain MCDDecodeBuffer} instance.
	 *
	 * @return the total number of bytes ready via this {@linkplain MCDDecodeBuffer} instance.
	 */
	public long getTotalRead() {
		return this.totalRead;
	}

	/**
	 * Slices the requested number of bytes from the byte channel and wraps them into a {@linkplain SeekableByteChannel}
	 * instance.
	 * <p>
	 * In case of a sequential byte channel the bytes are buffered in memory up to a limit of
	 * {@linkplain Defaults#MAX_BUFFER_SIZE} bytes. Bytes exceeding this limit are automatically skipped. To detect the
	 * latter compare the size of the returned {@linkplain SeekableByteChannel} to the requested size.
	 * </p>
	 *
	 * @param length the number of bytes to slice.
	 * @return the {@linkplain SeekableByteChannel} providing access to the sliced bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public SeekableByteChannel slice(long length) throws IOException {
		Check.isTrue(length >= 0);

		SeekableByteChannel slice;

		if (this.in instanceof SeekableByteChannel) {
			SeekableByteChannel channel = (SeekableByteChannel) this.in;
			long position = channel.position();

			slice = new SlicedChannel(channel, position, length);
			channel.position(position + length);
			this.totalRead += length;
		} else {
			ByteBuffer buffer = ByteBuffer.allocate((int) Math.min(length, Defaults.MAX_BUFFER_SIZE))
					.order(this.decodeBuffer.order());

			readFully(buffer);
			buffer.flip();

			long remaining = length - buffer.remaining();

			if (remaining > 0) {
				LOG.warning("Skipping {0} bytes exceeding the buffer limit", remaining);

				skip(remaining);
			}
			slice = new SlicedBuffer(buffer);
		}
		return slice;
	}

	/**
	 * Skips the requested number of bytes from the byte channel.
	 *
	 * @param length the number of bytes to skip.
	 * @throws IOException if an I/O error occurs.
	 */
	public void skip(long length) throws IOException {
		Check.isTrue(length >= 0);

		if (this.in instanceof SeekableByteChannel) {
			SeekableByteChannel channel = (SeekableByteChannel) this.in;

			channel.position(channel.position() + length);
			this.totalRead += length;
		} else {
			long remaining = length;

			while (remaining > 0) {
				int readLimit = (int) Math.min(remaining, Defaults.DEFAULT_BUFFER_SIZE);

				this.decodeBuffer.clear().limit(readLimit);
				readFully();
				remaining -= readLimit;
			}
		}
	}

	/**
	 * Reads a single byte value.
	 * <p>
	 * In contrast to the decode functions this function handles EOF gracefully by returning {@code -1}.
	 * </p>
	 *
	 * @return the read byte value or {@code -1} if EOF has been reached.
	 * @throws IOException if an I/O error occurs.
	 */
	public int read() throws IOException {
		ensureDecodeBufferCapacity(Byte.BYTES);
		this.decodeBuffer.clear().limit(Byte.BYTES);

		int read = readAny();

		this.decodeBuffer.flip();
		return (read > 0 ? Byte.toUnsignedInt(this.decodeBuffer.get()) : -1);
	}

	/**
	 * Pushes the given bytes back to the buffer to be re-read on following read or decode calls.
	 * <p>
	 * The submitted byte array is used directly without copying it.
	 * </p>
	 *
	 * @param bytes the bytes to re-read.
	 * @param offset the offset of the first byte to re-read.
	 * @param length the number of bytes to re-read.
	 */
	public void reread(byte[] bytes, int offset, int length) {
		this.rereadBuffer = ByteBuffer.wrap(bytes, offset, length).order(this.decodeBuffer.order());
		this.totalRead -= length;
	}

	/**
	 * Decodes a byte value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public byte decodeI8() throws IOException {
		ensureDecodeBufferCapacity(Byte.BYTES);
		this.decodeBuffer.clear().limit(Byte.BYTES);
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.get();
	}

	/**
	 * Decodes an array of byte values.
	 *
	 * @param length the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI8Array(int length) throws IOException {
		ensureDecodeBufferCapacity(length * (Byte.BYTES));
		this.decodeBuffer.clear().limit(length * (Byte.BYTES));
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.asReadOnlyBuffer();
	}

	/**
	 * Decodes a byte array from a buffer returned from {@linkplain #decodeI8Array(int)}.
	 *
	 * @param buffer the buffer to decode.
	 * @return the decoded byte array.
	 */
	public static byte[] toI8Array(ByteBuffer buffer) {
		int length = buffer.remaining();
		byte[] array = new byte[length];

		buffer.get(array);
		return array;
	}

	/**
	 * Decodes the given magic byte.
	 *
	 * @param expectedMagic the expected magic byte.
	 * @throws IOException if the decoded byte does not match the expected magic byte or if an I/O error occurs.
	 */
	public void decodeMagic(byte expectedMagic) throws IOException {
		byte actualMagic = decodeI8();

		if (expectedMagic != actualMagic) {
			throw new IOException(MESSAGE_UNEXPECTED_MAGIC_VALUE + actualMagic);
		}
	}

	/**
	 * Decodes a short value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public short decodeI16() throws IOException {
		ensureDecodeBufferCapacity(Short.BYTES);
		this.decodeBuffer.clear().limit(Short.BYTES);
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.getShort();
	}

	/**
	 * Decodes an array of short values.
	 *
	 * @param length the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI16Array(int length) throws IOException {
		ensureDecodeBufferCapacity(length * (Short.BYTES));
		this.decodeBuffer.clear().limit(length * (Short.BYTES));
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.asReadOnlyBuffer();
	}

	/**
	 * Decodes a short array from a buffer returned from {@linkplain #decodeI16Array(int)}.
	 *
	 * @param buffer the buffer to decode.
	 * @return the decoded byte array.
	 */
	public static short[] toI16Array(ByteBuffer buffer) {
		int length = buffer.remaining() / Short.BYTES;
		short[] array = new short[length];

		for (int index = 0; index < length; index++) {
			array[index] = buffer.getShort();
		}
		return array;
	}

	/**
	 * Decodes the given magic word.
	 *
	 * @param expectedMagic the expected magic word.
	 * @throws IOException if the decoded word does not match the expected magic word or if an I/O error occurs.
	 */
	public void decodeMagic(short expectedMagic) throws IOException {
		short actualMagic = decodeI16();

		if (expectedMagic != actualMagic) {
			throw new IOException(MESSAGE_UNEXPECTED_MAGIC_VALUE + actualMagic);
		}
	}

	/**
	 * Decodes an int value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public int decodeI32() throws IOException {
		ensureDecodeBufferCapacity(Integer.BYTES);
		this.decodeBuffer.clear().limit(Integer.BYTES);
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.getInt();
	}

	/**
	 * Decodes an array of int values.
	 *
	 * @param length the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI32Array(int length) throws IOException {
		ensureDecodeBufferCapacity(length * (Integer.BYTES));
		this.decodeBuffer.clear().limit(length * (Integer.BYTES));
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.asReadOnlyBuffer();
	}

	/**
	 * Decodes the given magic dword.
	 *
	 * @param expectedMagic the expected magic dword.
	 * @throws IOException if the decoded dword does not match the expected magic dword or if an I/O error occurs.
	 */
	public void decodeMagic(int expectedMagic) throws IOException {
		int actualMagic = decodeI32();

		if (expectedMagic != actualMagic) {
			throw new IOException(MESSAGE_UNEXPECTED_MAGIC_VALUE + actualMagic);
		}
	}

	/**
	 * Decodes a long value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public long decodeI64() throws IOException {
		ensureDecodeBufferCapacity(Long.BYTES);
		this.decodeBuffer.clear().limit(Long.BYTES);
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.getLong();
	}

	/**
	 * Decodes an array of long values.
	 *
	 * @param length the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI64Array(int length) throws IOException {
		ensureDecodeBufferCapacity(length * (Long.BYTES));
		this.decodeBuffer.clear().limit(length * (Long.BYTES));
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.asReadOnlyBuffer();
	}

	/**
	 * Decodes the given magic qword.
	 *
	 * @param expectedMagic the expected magic qword.
	 * @throws IOException if the decoded qword does not match the expected magic qword or if an I/O error occurs.
	 */
	public void decodeMagic(long expectedMagic) throws IOException {
		long actualMagic = decodeI64();

		if (expectedMagic != actualMagic) {
			throw new IOException(MESSAGE_UNEXPECTED_MAGIC_VALUE + actualMagic);
		}
	}

	/**
	 * Decodes a float value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public float decodeF32() throws IOException {
		ensureDecodeBufferCapacity(Float.BYTES);
		this.decodeBuffer.clear().limit(Float.BYTES);
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.getFloat();
	}

	/**
	 * Decodes a double byte value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public double decodeF64() throws IOException {
		ensureDecodeBufferCapacity(Double.BYTES);
		this.decodeBuffer.clear().limit(Double.BYTES);
		readFully();
		this.decodeBuffer.flip();
		return this.decodeBuffer.getDouble();
	}

	private void ensureDecodeBufferCapacity(int capacity) {
		if (this.decodeBuffer.capacity() < capacity) {
			this.decodeBuffer = ByteBuffer.allocate(capacity).order(this.decodeBuffer.order());
		}
	}

	private int readAny() throws IOException {
		int read = 0;
		int lastRead = 0;

		while (lastRead >= 0 && this.decodeBuffer.hasRemaining()) {
			ByteBuffer checkedRereadBuffer = checkRereadBuffer();

			if (checkedRereadBuffer != null) {
				this.decodeBuffer.put(checkedRereadBuffer.get());
				lastRead = 1;
				read++;
			} else {
				lastRead = this.in.read(this.decodeBuffer);
				if (lastRead > 0) {
					read += lastRead;
				}
			}
		}
		this.totalRead += read;
		return read;
	}

	private void readFully() throws IOException {
		readFully(this.decodeBuffer);
	}

	private void readFully(ByteBuffer buffer) throws IOException {
		while (buffer.hasRemaining()) {
			ByteBuffer checkedRereadBuffer = checkRereadBuffer();

			if (checkedRereadBuffer != null) {
				this.decodeBuffer.put(checkedRereadBuffer.get());
				this.totalRead++;
			} else {
				int read = this.in.read(buffer);

				if (read < 0) {
					throw new EOFException();
				}
				this.totalRead += read;
			}
		}
	}

	@Nullable
	private ByteBuffer checkRereadBuffer() {
		ByteBuffer checkedRereadBuffer = null;

		if (this.rereadBuffer != null) {
			if (this.rereadBuffer.hasRemaining()) {
				checkedRereadBuffer = this.rereadBuffer;
			} else {
				this.rereadBuffer = null;
			}
		}
		return checkedRereadBuffer;
	}

}
