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
package de.carne.mcd.instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.text.HexFormat;
import de.carne.util.Check;

/**
 * Immutable byte sequence representing an opcode.
 */
public class InstructionOpcode implements Comparable<InstructionOpcode> {

	private final byte[] bytes;
	private final int offset;
	private final int length;

	private InstructionOpcode(byte[] bytes, int offset, int length) {
		Check.isTrue(length < 256);

		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
	}

	/**
	 * The empty {@linkplain InstructionOpcode}.
	 */
	public static final InstructionOpcode EMPTY = new InstructionOpcode(new byte[0], 0, 0);

	/**
	 * Wraps a {@code byte} array into a {@linkplain InstructionOpcode} instance (without copying it).
	 *
	 * @param bytes the {@code byte} array backing up the created {@linkplain InstructionOpcode} instance.
	 * @return the {@linkplain InstructionOpcode} instance representing the wrapped bytes.
	 */
	public static InstructionOpcode wrap(byte[] bytes) {
		return wrap(bytes, 0, bytes.length);
	}

	/**
	 * Wraps a {@code byte} array into a {@linkplain InstructionOpcode} instance (without copying it).
	 *
	 * @param bytes the {@code byte} array backing up the created {@linkplain InstructionOpcode} instance.
	 * @param offset the offset of the first byte to wrap in the {@code byte} array.
	 * @param length the number of bytes to wrap in the {@code byte} array.
	 * @return the {@linkplain InstructionOpcode} instance representing the wrapped bytes.
	 */
	public static InstructionOpcode wrap(byte[] bytes, int offset, int length) {
		return new InstructionOpcode(bytes, offset, length);
	}

	/**
	 * Decodes a {@linkplain InstructionOpcode} previously encoded via {@linkplain #encode(int)}.
	 *
	 * @param encoded the encoded {@linkplain InstructionOpcode} as returned by {@linkplain #encode(int)}.
	 * @return the decoded {@linkplain InstructionOpcode}.
	 */
	public static InstructionOpcode decode(byte[] encoded) {
		return InstructionOpcode.wrap(encoded, 1, Byte.toUnsignedInt(encoded[0]));
	}

	/**
	 * Gets the number of bytes represented by this {@linkplain InstructionOpcode} instance.
	 *
	 * @return the number of bytes represented by this {@linkplain InstructionOpcode} instance.
	 */
	public int length() {
		return this.length;
	}

	/**
	 * Gets the nth opcode byte.
	 *
	 * @param n the offset of the byte to get.
	 * @return the nth opcode byte.
	 */
	public byte byteAt(int n) {
		return this.bytes[this.offset + n];
	}

	/**
	 * Gets the byte array represented by this opcode.
	 *
	 * @return the byte array represented by this opcode.
	 */
	public byte[] bytes() {
		byte[] opcodesBytes = new byte[this.length];

		System.arraycopy(this.bytes, this.offset, opcodesBytes, 0, this.length);
		return opcodesBytes;
	}

	/**
	 * Encodes this {@linkplain InstructionOpcode} for later decoding via {@linkplain #decode(byte[])}.
	 *
	 * @param encodedBytes the encoding length to use.
	 * @return the encoded {@linkplain InstructionOpcode}.
	 */
	public byte[] encode(int encodedBytes) {
		Check.isTrue(this.length < encodedBytes);

		byte[] encoded = new byte[encodedBytes];

		encoded[0] = (byte) this.length;
		System.arraycopy(this.bytes, 0, encoded, 1, this.length);
		return encoded;
	}

	@Override
	public int compareTo(InstructionOpcode o) {
		return compareTo(this.bytes, this.offset, this.length, o.bytes, o.offset, o.length);
	}

	/**
	 * See {@linkplain #compareTo(InstructionOpcode)}
	 *
	 * @param bytes1 left hands opcode's bytes.
	 * @param offset1 left hands opcode's offset.
	 * @param length1 left hands opcode's length.
	 * @param bytes2 right hands opcode's bytes.
	 * @param offset2 right hands opcode's offset.
	 * @param length2 right hands opcode's length.
	 * @return See {@linkplain #compareTo(InstructionOpcode)}
	 */
	public static int compareTo(byte[] bytes1, int offset1, int length1, byte[] bytes2, int offset2, int length2) {
		int comparison = Integer.compare(length1, length2);

		if (comparison == 0) {
			for (int compareIndex = 0; compareIndex < length1; compareIndex++) {
				int byte1 = Byte.toUnsignedInt(bytes1[offset1 + compareIndex]);
				int byte2 = Byte.toUnsignedInt(bytes2[offset2 + compareIndex]);

				comparison = Integer.compare(byte1, byte2);
				if (comparison != 0) {
					break;
				}
			}
		}
		return comparison;
	}

	@Override
	public int hashCode() {
		int hashCode = this.length;

		for (int byteIndex = this.offset + Math.min(this.length - 1, 3); byteIndex >= this.offset; byteIndex--) {
			hashCode |= Byte.toUnsignedInt(this.bytes[byteIndex]);
		}
		return hashCode;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return (this == obj || (obj instanceof InstructionOpcode && compareTo((InstructionOpcode) obj) == 0));
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
		return HexFormat.UPPER_CASE.format(bytes, offset, length);
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
