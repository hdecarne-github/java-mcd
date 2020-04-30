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
package de.carne.mcd.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.carne.io.Defaults;
import de.carne.nio.file.FileUtil;
import de.carne.nio.file.attribute.FileAttributes;
import de.carne.util.Check;

/**
 * Buffered byte channel access during decoding.
 */
public final class MCDInputBuffer implements MCDBuffer {

	private static final String MESSAGE_ILLEGAL_DISCARD_LENGTH = "Illegal discard length {0}";

	private static final String MESSAGE_UNEXPECTED_MAGIC_VALUE = "Unexpected magic value: ";

	private final ReadableByteChannel in;
	private ByteBuffer inputBuffer;
	private long totalRead = 0;
	private int commitPosition = 0;
	private int uncommittedPosition = 0;
	private boolean autoCommit = true;
	private int sliceCount = 0;
	@Nullable
	private SeekableByteChannel sliceChannel = null;

	/**
	 * Constructs a new {@linkplain MCDInputBuffer} instance.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to read from.
	 * @param byteOrder the {@linkplain ByteOrder} to use for decoding.
	 */
	public MCDInputBuffer(ReadableByteChannel in, ByteOrder byteOrder) {
		this(in, byteOrder, Defaults.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Constructs a new {@linkplain MCDInputBuffer} instance.
	 *
	 * @param in the {@linkplain ReadableByteChannel} to read from.
	 * @param byteOrder the {@linkplain ByteOrder} to use for decoding.
	 * @param bufferSize the buffer size to use.
	 */
	public MCDInputBuffer(ReadableByteChannel in, ByteOrder byteOrder, int bufferSize) {
		this.in = in;
		this.inputBuffer = ByteBuffer.allocate(bufferSize).order(byteOrder);
	}

	/**
	 * Gets the total number of bytes read via this {@linkplain MCDInputBuffer} instance.
	 *
	 * @return the total number of bytes read via this {@linkplain MCDInputBuffer} instance.
	 */
	public long getTotalRead() {
		return this.totalRead;
	}

	@Override
	public boolean setAutoCommit(boolean autoCommit) {
		boolean previousAutoCommit = this.autoCommit;

		this.autoCommit = autoCommit;
		return previousAutoCommit;
	}

	@Override
	public void commit() throws IOException {
		if (this.uncommittedPosition == this.inputBuffer.position()) {
			this.uncommittedPosition = 0;
			this.inputBuffer.clear();
		}
		this.commitPosition = this.uncommittedPosition;
	}

	@Override
	public void discard() {
		this.totalRead -= (this.uncommittedPosition - this.commitPosition);
		this.uncommittedPosition = this.commitPosition;
	}

	/**
	 * Discards a specific number of uncommitted bytes.
	 * <p>
	 * If {@code length} is positive the corresponding number of bytes are kept as uncommitted and any following byte is
	 * discarded. If {@code length} is negative the last {@code -length} bytes are discarded and any other byte is kept
	 * as uncommitted.
	 * </p>
	 *
	 * @param length the amount of bytes to discard.
	 */
	public void discard(int length) {
		int uncomittedPositionAfterDiscard;

		if (length > 0) {
			uncomittedPositionAfterDiscard = this.commitPosition + length;

			Check.isTrue(uncomittedPositionAfterDiscard <= this.uncommittedPosition, MESSAGE_ILLEGAL_DISCARD_LENGTH,
					length);

			this.totalRead -= (this.uncommittedPosition - uncomittedPositionAfterDiscard);
		} else {
			uncomittedPositionAfterDiscard = this.uncommittedPosition + length;

			Check.isTrue(this.commitPosition <= uncomittedPositionAfterDiscard, MESSAGE_ILLEGAL_DISCARD_LENGTH, length);

			this.totalRead += length;
		}
		this.uncommittedPosition = uncomittedPositionAfterDiscard;
	}

	/**
	 * Slices the requested number of bytes from the byte channel and wraps them into a {@linkplain SeekableByteChannel}
	 * instance for later decoding.
	 * <p>
	 * This function requires this buffer instance to be committed prior to calling it.
	 * </p>
	 *
	 * @param length the number of bytes to slice.
	 * @return the {@linkplain SeekableByteChannel} providing access to the sliced bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public SeekableByteChannel slice(long length) throws IOException {
		Check.isTrue(length >= 0);

		Check.assertTrue(this.commitPosition == this.uncommittedPosition);

		SeekableByteChannel slice;

		if (this.in instanceof SeekableByteChannel) {
			SeekableByteChannel channel = (SeekableByteChannel) this.in;
			long position = channel.position();

			slice = new SlicedChannel(channel, position, length);
			channel.position(position + length);
		} else {
			SeekableByteChannel channel = allocateSliceChannel();
			long position = channel.size();

			channel.position(position);
			passThrough(length, channel::write);
			slice = new SlicedChannel(channel, position, length, this::releaseSliceChannel);
		}
		this.totalRead += length;
		return slice;
	}

	/**
	 * Skips the requested number of bytes from the byte channel.
	 * <p>
	 * This function requires this buffer instance to be committed prior to calling it.
	 * </p>
	 *
	 * @param length the number of bytes to skip.
	 * @throws IOException if an I/O error occurs.
	 */
	public void skip(long length) throws IOException {
		Check.isTrue(length >= 0);

		Check.assertTrue(this.commitPosition == this.uncommittedPosition);

		if (this.in instanceof SeekableByteChannel) {
			SeekableByteChannel channel = (SeekableByteChannel) this.in;

			channel.position(channel.position() + length);
		} else {
			passThrough(length, b -> b.position(b.position() + b.remaining()));
		}
		this.totalRead += length;
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
		feedInputBuffer(Byte.BYTES, false);

		int value = -1;

		if (this.uncommittedPosition < this.inputBuffer.position()) {
			value = Byte.toUnsignedInt(this.inputBuffer.get(this.uncommittedPosition));
			this.uncommittedPosition += Byte.BYTES;
			this.totalRead += Byte.BYTES;
			if (this.autoCommit) {
				commit();
			}
		}
		return value;
	}

	/**
	 * Decodes a {@code byte} value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public byte decodeI8() throws IOException {
		feedInputBuffer(Byte.BYTES, true);

		byte decoded = this.inputBuffer.get(this.uncommittedPosition);

		this.uncommittedPosition += Byte.BYTES;
		this.totalRead += Byte.BYTES;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes an array of {@code byte} values.
	 *
	 * @param arrayLength the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI8Array(int arrayLength) throws IOException {
		Check.isTrue(arrayLength >= 0);

		int length = arrayLength * Byte.BYTES;

		feedInputBuffer(length, true);

		ByteBuffer decoded = this.inputBuffer.asReadOnlyBuffer();
		int uncommittedPositionAfterDecode = this.uncommittedPosition + length;

		decoded.position(this.uncommittedPosition).limit(uncommittedPositionAfterDecode);
		this.uncommittedPosition = uncommittedPositionAfterDecode;
		this.totalRead += length;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes a {@code byte} array from a buffer returned from {@linkplain #decodeI8Array(int)}.
	 *
	 * @param buffer the buffer to decode.
	 * @return the decoded array.
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
	 * Decodes a {@code short} value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public short decodeI16() throws IOException {
		feedInputBuffer(Short.BYTES, true);

		short decoded = this.inputBuffer.getShort(this.uncommittedPosition);

		this.uncommittedPosition += Short.BYTES;
		this.totalRead += Short.BYTES;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes an array of {@code short} values.
	 *
	 * @param arrayLength the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI16Array(int arrayLength) throws IOException {
		Check.isTrue(arrayLength >= 0);

		int length = arrayLength * Short.BYTES;

		feedInputBuffer(length, true);

		ByteBuffer decoded = this.inputBuffer.asReadOnlyBuffer();
		int uncommittedPositionAfterDecode = this.uncommittedPosition + length;

		decoded.position(this.uncommittedPosition).limit(uncommittedPositionAfterDecode);
		this.uncommittedPosition = uncommittedPositionAfterDecode;
		this.totalRead += length;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes a {@code short} array from a buffer returned from {@linkplain #decodeI16Array(int)}.
	 *
	 * @param buffer the buffer to decode.
	 * @return the decoded array.
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
	 * Decodes an {@code int} value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public int decodeI32() throws IOException {
		feedInputBuffer(Integer.BYTES, true);

		int decoded = this.inputBuffer.getInt(this.uncommittedPosition);

		this.uncommittedPosition += Integer.BYTES;
		this.totalRead += Integer.BYTES;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes an array of {@code int} values.
	 *
	 * @param arrayLength the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI32Array(int arrayLength) throws IOException {
		Check.isTrue(arrayLength >= 0);

		int length = arrayLength * Integer.BYTES;

		feedInputBuffer(length, true);

		ByteBuffer decoded = this.inputBuffer.asReadOnlyBuffer();
		int uncommittedPositionAfterDecode = this.uncommittedPosition + length;

		decoded.position(this.uncommittedPosition).limit(uncommittedPositionAfterDecode);
		this.uncommittedPosition = uncommittedPositionAfterDecode;
		this.totalRead += length;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes an {@code int} array from a buffer returned from {@linkplain #decodeI32Array(int)}.
	 *
	 * @param buffer the buffer to decode.
	 * @return the decoded array.
	 */
	public static int[] toI32Array(ByteBuffer buffer) {
		int length = buffer.remaining() / Integer.BYTES;
		int[] array = new int[length];

		for (int index = 0; index < length; index++) {
			array[index] = buffer.getInt();
		}
		return array;
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
	 * Decodes a {@code long} value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public long decodeI64() throws IOException {
		feedInputBuffer(Long.BYTES, true);

		long decoded = this.inputBuffer.getLong(this.uncommittedPosition);

		this.uncommittedPosition += Long.BYTES;
		this.totalRead += Long.BYTES;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes an array of {@code long} values.
	 *
	 * @param arrayLength the array length to decode.
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer decodeI64Array(int arrayLength) throws IOException {
		Check.isTrue(arrayLength >= 0);

		int length = arrayLength * Long.BYTES;

		feedInputBuffer(length, true);

		ByteBuffer decoded = this.inputBuffer.asReadOnlyBuffer();
		int uncommittedPositionAfterDecode = this.uncommittedPosition + length;

		decoded.position(this.uncommittedPosition).limit(uncommittedPositionAfterDecode);
		this.uncommittedPosition = uncommittedPositionAfterDecode;
		this.totalRead += length;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes a {@code long} array from a buffer returned from {@linkplain #decodeI64Array(int)}.
	 *
	 * @param buffer the buffer to decode.
	 * @return the decoded array.
	 */
	public static long[] toI64Array(ByteBuffer buffer) {
		int length = buffer.remaining() / Long.BYTES;
		long[] array = new long[length];

		for (int index = 0; index < length; index++) {
			array[index] = buffer.getLong();
		}
		return array;
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
		feedInputBuffer(Float.BYTES, true);

		float decoded = this.inputBuffer.getFloat(this.uncommittedPosition);

		this.uncommittedPosition += Float.BYTES;
		this.totalRead += Float.BYTES;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	/**
	 * Decodes a double byte value.
	 *
	 * @return the decoded value.
	 * @throws IOException if an I/O error occurs.
	 */
	public double decodeF64() throws IOException {
		feedInputBuffer(Double.BYTES, true);

		double decoded = this.inputBuffer.getDouble(this.uncommittedPosition);

		this.uncommittedPosition += Double.BYTES;
		this.totalRead += Double.BYTES;
		if (this.autoCommit) {
			commit();
		}
		return decoded;
	}

	private void feedInputBuffer(int length, boolean fully) throws IOException {
		int inputBufferPosition = this.inputBuffer.position();
		int available = inputBufferPosition - this.uncommittedPosition;

		if (available < length) {
			int unavailable = length - available;

			if (this.inputBuffer.remaining() < unavailable) {
				if (inputBufferPosition == 0) {
					this.inputBuffer = ByteBuffer.allocate(length).order(this.inputBuffer.order());
				} else {
					throw new IOException("Insufficient input buffer capacity: " + this.uncommittedPosition + "/"
							+ available + "/" + unavailable);
				}
			}

			ByteBuffer buffer = this.inputBuffer.duplicate();

			buffer.limit(inputBufferPosition + unavailable);

			int read;

			if (fully) {
				read = readFully(buffer);
			} else {
				read = readAny(buffer);
			}
			this.inputBuffer.position(inputBufferPosition + read);
		}
	}

	private int readFully(ByteBuffer buffer) throws IOException {
		int read = 0;

		while (buffer.hasRemaining()) {
			int read0 = this.in.read(buffer);

			if (read0 < 0) {
				throw new EOFException();
			}
			read += read0;
		}
		return read;
	}

	private int readAny(ByteBuffer buffer) throws IOException {
		int read = 0;

		while (buffer.hasRemaining()) {
			int read0 = this.in.read(buffer);

			if (read0 < 0) {
				break;
			}
			read += read0;
		}
		return read;
	}

	private void passThrough(long length, PassThroughHandler handler) throws IOException {
		ByteBuffer readBuffer = ByteBuffer.allocate(Defaults.DEFAULT_BUFFER_SIZE);
		long remaining = length;

		while (remaining > 0) {
			int readLimit = (int) Math.min(remaining, readBuffer.capacity());

			readBuffer.clear().limit(readLimit);

			int read = this.in.read(readBuffer);

			if (read < 0) {
				throw new EOFException();
			}
			readBuffer.flip();
			while (readBuffer.hasRemaining()) {
				handler.accept(readBuffer);
			}
			remaining -= readLimit;
		}
	}

	@Override
	public @NonNull String toString() {
		return "commit: " + this.commitPosition + "; uncommitted: " + this.uncommittedPosition + "; total: "
				+ this.totalRead;
	}

	@FunctionalInterface
	private interface PassThroughHandler {

		void accept(ByteBuffer buffer) throws IOException;

	}

	@SuppressWarnings({ "null" })
	private SeekableByteChannel allocateSliceChannel() throws IOException {
		SeekableByteChannel channel;

		if (this.sliceCount == 0) {
			Path tempDir = FileUtil.tmpDir();
			Path channelPath = Files.createTempFile(tempDir, null, null, FileAttributes.userFileDefault(tempDir));

			channel = this.sliceChannel = Files.newByteChannel(channelPath, StandardOpenOption.READ,
					StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.DELETE_ON_CLOSE);
		} else {
			channel = this.sliceChannel;
		}
		this.sliceCount++;
		return channel;
	}

	@SuppressWarnings({ "null" })
	private void releaseSliceChannel() throws IOException {
		this.sliceCount++;
		if (this.sliceCount == 0) {
			this.sliceChannel.close();
			this.sliceChannel = null;
		}
	}

	@FunctionalInterface
	private interface CloseSliceChannelHandler {

		void close() throws IOException;

	}

	private class SlicedChannel implements SeekableByteChannel {

		private final SeekableByteChannel channel;
		private final long start;
		private final long length;
		private final CloseSliceChannelHandler closeHandler;
		private long position = 0;

		SlicedChannel(SeekableByteChannel channel, long start, long length) {
			this(channel, start, length, () -> {
				// default is to do no close at all
			});
		}

		SlicedChannel(SeekableByteChannel channel, long start, long length, CloseSliceChannelHandler closeHandler) {
			this.channel = channel;
			this.start = start;
			this.length = length;
			this.closeHandler = closeHandler;
		}

		@Override
		public boolean isOpen() {
			return this.channel.isOpen();
		}

		@Override
		public void close() throws IOException {
			this.closeHandler.close();
		}

		@Override
		public int read(@Nullable ByteBuffer dst) throws IOException {
			Objects.requireNonNull(dst);

			int readLimit = (int) Math.min(this.length - this.position, dst.remaining());
			int read = -1;

			if (readLimit > 0) {
				ByteBuffer limitedDst = dst.duplicate();

				limitedDst.limit(limitedDst.position() + readLimit);
				this.channel.position(this.start + this.position);
				read = this.channel.read(limitedDst);
				if (read > 0) {
					dst.position(dst.position() + read);
					this.position += read;
				}
			}
			return read;
		}

		@Override
		public int write(@Nullable ByteBuffer src) throws IOException {
			throw new NonWritableChannelException();
		}

		@Override
		public long position() throws IOException {
			return this.position;
		}

		@Override
		public SeekableByteChannel position(long newPosition) throws IOException {
			Check.isTrue(newPosition >= 0);

			if (newPosition > this.length) {
				throw new NonWritableChannelException();
			}
			this.position = newPosition;
			return this;
		}

		@Override
		public long size() throws IOException {
			return this.length;
		}

		@Override
		public SeekableByteChannel truncate(long size) throws IOException {
			throw new NonWritableChannelException();
		}

	}

}
