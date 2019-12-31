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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.check.Check;
import de.carne.text.HexFormatter;

/**
 * Immutable byte sequence representing an opcode.
 */
public class Opcode implements Comparable<Opcode> {

	private final byte[] bytes;
	private final int offset;
	private final int length;

	private Opcode(byte[] bytes, int offset, int length) {
		Check.isTrue(length < 256);

		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
	}

	/**
	 * The empty {@linkplain Opcode}.
	 */
	public static final Opcode EMPTY = new Opcode(new byte[0], 0, 0);

	/**
	 * Wraps a {@code byte} array into a {@linkplain Opcode} instance (without copying it).
	 *
	 * @param bytes the {@code byte} array backing up the created {@linkplain Opcode} instance.
	 * @return the {@linkplain Opcode} instance representing the wrapped bytes.
	 */
	public static Opcode wrap(byte[] bytes) {
		return wrap(bytes, 0, bytes.length);
	}

	/**
	 * Wraps a {@code byte} array into a {@linkplain Opcode} instance (without copying it).
	 *
	 * @param bytes the {@code byte} array backing up the created {@linkplain Opcode} instance.
	 * @param offset the offset of the first byte to wrap in the {@code byte} array.
	 * @param length the number of bytes to wrap in the {@code byte} array.
	 * @return the {@linkplain Opcode} instance representing the wrapped bytes.
	 */
	public static Opcode wrap(byte[] bytes, int offset, int length) {
		return new Opcode(bytes, offset, length);
	}

	/**
	 * Decodes a {@linkplain Opcode} previously encoded via {@linkplain #encode(int)}.
	 *
	 * @param encoded the encoded {@linkplain Opcode} as returned by {@linkplain #encode(int)}.
	 * @return the decoded {@linkplain Opcode}.
	 */
	public static Opcode decode(byte[] encoded) {
		return Opcode.wrap(encoded, 1, Byte.toUnsignedInt(encoded[0]));
	}

	/**
	 * Gets the length of this {@linkplain Opcode} instance.
	 *
	 * @return the length of this {@linkplain Opcode} instance.
	 */
	public int length() {
		return this.length;
	}

	/**
	 * Encodes this {@linkplain Opcode} for later decoding via {@linkplain #decode(byte[])}.
	 *
	 * @param encodedBytes the encoding length to use.
	 * @return the encoded {@linkplain Opcode}.
	 */
	public byte[] encode(int encodedBytes) {
		Check.isTrue(this.length < encodedBytes);

		byte[] encoded = new byte[encodedBytes];

		encoded[0] = (byte) this.length;
		System.arraycopy(this.bytes, 0, encoded, 1, this.length);
		return encoded;
	}

	@Override
	public int compareTo(Opcode o) {
		return compareTo(this.bytes, this.offset, this.length, o.bytes, o.offset, o.length);
	}

	/**
	 * See {@linkplain #compareTo(Opcode)}
	 *
	 * @param bytes1 left hands opcode's bytes.
	 * @param offset1 left hands opcode's offset.
	 * @param length1 left hands opcode's length.
	 * @param bytes2 right hands opcode's bytes.
	 * @param offset2 right hands opcode's offset.
	 * @param length2 right hands opcode's length.
	 * @return See {@linkplain #compareTo(Opcode)}
	 */
	public static int compareTo(byte[] bytes1, int offset1, int length1, byte[] bytes2, int offset2, int length2) {
		int comparison = Integer.compare(length1, length2);

		if (comparison == 0) {
			for (int compareIndex = 0; compareIndex < length1; compareIndex++) {
				byte byte1 = bytes1[offset1 + compareIndex];
				byte byte2 = bytes2[offset2 + compareIndex];

				comparison = Byte.compare(byte1, byte2);
				if (comparison != 0) {
					break;
				}
			}
		}
		return comparison;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.bytes, this.offset, this.length);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return (this == obj || (obj instanceof Opcode && compareTo((Opcode) obj) == 0));
	}

	@Override
	public String toString() {
		return toString(this.bytes, this.offset, this.length);
	}

	/**
	 * Returns the string representation of the given byte array.
	 *
	 * @param bytes the bytes to format.
	 * @return the string representation of the given byte array.
	 */
	public static String toString(byte[] bytes) {
		return toString(bytes, 0, bytes.length);
	}

	/**
	 * Returns the string representation of the given byte range.
	 *
	 * @param bytes the bytes to format.
	 * @param offset the offset of the first byte to format.
	 * @param length the number of bytes to format.
	 * @return the string representation of the given byte range.
	 */
	public static String toString(byte[] bytes, int offset, int length) {
		HexFormatter formatter = new HexFormatter(true);

		return formatter.format(bytes, offset, length);
	}

	/**
	 * Parses a byte array's string representation as returned by {@linkplain #toString()} and it's derivates.
	 *
	 * @param s the string to parse.
	 * @return the parsed byte array.
	 * @throws NumberFormatException if the string cannot be parsed.
	 */
	public static byte[] parse(String s) {
		StringTokenizer byteStrings = new StringTokenizer(s, " ");
		List<Byte> bytesList = new ArrayList<>();

		while (byteStrings.hasMoreElements()) {
			String byteString = byteStrings.nextToken();
			int byteValue = Integer.parseUnsignedInt(byteString, 16);

			if (byteValue > 255) {
				throw new NumberFormatException("Invalid byte value: " + byteString);
			}
			bytesList.add((byte) (byteValue & 0xff));
		}

		byte[] bytesArray = new byte[bytesList.size()];
		int byteIndex = 0;

		for (Byte byteValue : bytesList) {
			bytesArray[byteIndex] = byteValue.byteValue();
			byteIndex++;
		}
		return bytesArray;
	}

}
