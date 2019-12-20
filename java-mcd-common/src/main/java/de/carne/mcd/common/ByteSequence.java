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
package de.carne.mcd.common;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.check.Check;
import de.carne.text.HexFormatter;

/**
 * Immutable byte sequence for efficient byte data processing.
 */
public final class ByteSequence implements Comparable<ByteSequence> {

	private final byte[] bytes;
	private final int offset;
	private final int length;

	private ByteSequence(byte[] bytes, int offset, int length) {
		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
	}

	/**
	 * The empty {@linkplain ByteSequence}.
	 */
	public static final ByteSequence EMPTY = new ByteSequence(new byte[0], 0, 0);

	/**
	 * Wraps a {@code byte} array into a {@linkplain ByteSequence} instance (without copying it).
	 *
	 * @param bytes the {@code byte} array backing up the created {@linkplain ByteSequence} instance.
	 * @return the {@linkplain ByteSequence} instance representing the wrapped bytes.
	 */
	public static ByteSequence wrap(byte[] bytes) {
		return wrap(bytes, 0, bytes.length);
	}

	/**
	 * Wraps a {@code byte} array into a {@linkplain ByteSequence} instance (without copying it).
	 *
	 * @param bytes the {@code byte} array backing up the created {@linkplain ByteSequence} instance.
	 * @param offset the offset of the first byte to wrap in the {@code byte} array.
	 * @param length the number of bytes to wrap in the {@code byte} array.
	 * @return the {@linkplain ByteSequence} instance representing the wrapped bytes.
	 */
	public static ByteSequence wrap(byte[] bytes, int offset, int length) {
		Check.isTrue(0 <= offset && offset <= bytes.length);
		Check.isTrue(0 <= length && offset + length <= bytes.length);

		return new ByteSequence(bytes, offset, length);
	}

	/**
	 * Gets the length of this {@linkplain ByteSequence} instance.
	 *
	 * @return the length of this {@linkplain ByteSequence} instance.
	 */
	public int length() {
		return this.length;
	}

	/**
	 * Creates a slice of this {@linkplain ByteSequence} instance.
	 *
	 * @param sliceOffset the offset of the first byte to slice.
	 * @param sliceLength the number of bytes to slice.
	 * @return the {@linkplain ByteSequence} instance representing the sliced bytes.
	 */
	public ByteSequence slice(int sliceOffset, int sliceLength) {
		Check.isTrue(0 <= sliceOffset && sliceOffset <= this.length);
		Check.isTrue(0 <= sliceLength && sliceOffset + sliceLength <= this.length);

		return (sliceOffset == 0 && sliceLength == this.length ? this
				: new ByteSequence(this.bytes, this.offset + sliceOffset, sliceLength));
	}

	/**
	 * Gets a copy of this {@linkplain ByteSequence} instance's bytes.
	 *
	 * @return a copy of this {@linkplain ByteSequence} instance's bytes.
	 */
	public byte[] toArray() {
		byte[] array = new byte[this.length];

		System.arraycopy(this.bytes, this.offset, array, 0, this.length);
		return array;
	}

	/**
	 * Copies this {@linkplain ByteSequence} instance's bytes to an array.
	 * <p>
	 * The bytes to copy are clamped automatically to the available range. Hence the number of copied bytes may be less
	 * than requested.
	 * </p>
	 *
	 * @param srcOffset the offset of the first byte to copy.
	 * @param dest the {@code byte} array to copy to.
	 * @param destOffset the offset to copy to.
	 * @param destLength the number of bytes to copy.
	 * @return the number of bytes copied.
	 */
	public int toArray(int srcOffset, byte[] dest, int destOffset, int destLength) {
		Check.isTrue(0 <= srcOffset);

		int copyOffset = this.offset + srcOffset;
		int copied = Math.min(Math.max(this.length - copyOffset, 0), destLength);

		if (copied > 0) {
			System.arraycopy(this.bytes, copyOffset, dest, destOffset, copied);
		}
		return copied;
	}

	@Override
	public int compareTo(ByteSequence o) {
		int comparison = 0;
		int compareLength = Math.min(this.length, o.length);

		for (int compareIndex = 0; compareIndex < compareLength; compareIndex++) {
			byte thisByte = this.bytes[this.offset + compareIndex];
			byte oByte = o.bytes[o.offset + compareIndex];

			comparison = Byte.compare(thisByte, oByte);
			if (comparison != 0) {
				break;
			}
		}
		if (comparison == 0) {
			comparison = Integer.compare(this.length, o.length);
		}
		return comparison;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.bytes, this.offset, this.length);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return (this == obj || (obj instanceof ByteSequence && compareTo((ByteSequence) obj) == 0));
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("[").append(this.offset).append(":").append(this.offset + this.length).append("] = {");

		HexFormatter formatter = new HexFormatter();

		for (int byteIndex = this.offset; byteIndex < this.offset + this.length; byteIndex++) {
			buffer.append(" ");
			formatter.format(buffer, this.bytes[byteIndex]);
		}
		buffer.append(" }");
		return buffer.toString();
	}

}
