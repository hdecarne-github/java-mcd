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
package de.carne.mcd.common.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.mcd.common.ByteSequence;

/**
 * Test {@linkplain ByteSequence} class.
 */
class ByteSequenceTest {

	private static final byte[] TEST_BYTES_ALL = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	private static final byte[] TEST_BYTES_1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private static final byte[] TEST_BYTES_2 = { 10, 11, 12, 13, 14, 15, 16 };
	private static final byte[] TEST_BYTES_2_1 = { 10, 11, 12, 13 };
	private static final byte[] TEST_BYTES_2_2 = { 14, 15, 16 };
	private static final byte[] TEST_BYTES_3 = {};

	@Test
	void testSlicing() {
		ByteSequence bsAll = ByteSequence.wrap(TEST_BYTES_ALL);
		ByteSequence bs1 = bsAll.slice(0, TEST_BYTES_1.length);
		ByteSequence bs2 = bsAll.slice(TEST_BYTES_1.length, TEST_BYTES_2.length);
		ByteSequence bs3 = bsAll.slice(TEST_BYTES_1.length + TEST_BYTES_2.length, TEST_BYTES_3.length);

		Assertions.assertArrayEquals(TEST_BYTES_ALL, bsAll.slice(0, TEST_BYTES_ALL.length).toArray());
		Assertions.assertArrayEquals(TEST_BYTES_1, bs1.toArray());
		Assertions.assertArrayEquals(TEST_BYTES_2, bs2.toArray());
		Assertions.assertArrayEquals(TEST_BYTES_2_1, bs2.slice(0, TEST_BYTES_2_1.length).toArray());
		Assertions.assertArrayEquals(TEST_BYTES_2_2, bs2.slice(TEST_BYTES_2_1.length, TEST_BYTES_2_2.length).toArray());
		Assertions.assertArrayEquals(TEST_BYTES_3, bs3.toArray());
	}

	@Test
	void testToArray() {
		ByteSequence bsAll = ByteSequence.wrap(TEST_BYTES_ALL);

		Assertions.assertArrayEquals(TEST_BYTES_ALL, bsAll.toArray());

		byte[] bsAllBytes = new byte[TEST_BYTES_ALL.length];
		byte[] bs1Bytes = new byte[TEST_BYTES_1.length];
		byte[] bs2Bytes = new byte[TEST_BYTES_2.length];
		byte[] bs3Bytes = new byte[TEST_BYTES_3.length];

		bsAll.toArray(0, bsAllBytes, 0, bsAllBytes.length + 1);
		bsAll.toArray(0, bs1Bytes, 0, bs1Bytes.length);
		bsAll.toArray(bs1Bytes.length, bs2Bytes, 0, bs2Bytes.length);
		bsAll.toArray(bsAllBytes.length + 1, bs3Bytes, 0, bs3Bytes.length);
		Assertions.assertArrayEquals(TEST_BYTES_ALL, bsAllBytes);
		Assertions.assertArrayEquals(TEST_BYTES_1, bs1Bytes);
		Assertions.assertArrayEquals(TEST_BYTES_2, bs2Bytes);
		Assertions.assertArrayEquals(TEST_BYTES_3, bs3Bytes);
	}

	@Test
	void testEquals() {
		ByteSequence bsAll = ByteSequence.wrap(TEST_BYTES_ALL);
		ByteSequence bs1 = ByteSequence.wrap(TEST_BYTES_1);
		ByteSequence bs2 = ByteSequence.wrap(TEST_BYTES_2);
		ByteSequence bs3 = ByteSequence.wrap(TEST_BYTES_3);

		Assertions.assertTrue(bsAll.equals(bsAll));
		Assertions.assertTrue(bs1.equals(bsAll.slice(0, TEST_BYTES_1.length)));
		Assertions.assertTrue(bs2.equals(bsAll.slice(TEST_BYTES_1.length, TEST_BYTES_2.length)));
		Assertions.assertTrue(bs3.equals(ByteSequence.EMPTY));
	}

	@Test
	void testCompare() {
		ByteSequence bsAll = ByteSequence.wrap(TEST_BYTES_ALL);
		ByteSequence bs1 = ByteSequence.wrap(TEST_BYTES_1);
		ByteSequence bs2 = ByteSequence.wrap(TEST_BYTES_2);
		ByteSequence bs3 = ByteSequence.wrap(TEST_BYTES_3);

		Assertions.assertEquals(0, bsAll.compareTo(bsAll));
		Assertions.assertEquals(1, Integer.signum(bsAll.compareTo(bs1)));
		Assertions.assertEquals(-1, Integer.signum(bs1.compareTo(bsAll)));
		Assertions.assertEquals(-1, Integer.signum(bs1.compareTo(bs2)));
		Assertions.assertEquals(1, Integer.signum(bs2.compareTo(bs1)));
		Assertions.assertEquals(1, Integer.signum(bs2.compareTo(bs3)));
		Assertions.assertEquals(-1, Integer.signum(bs3.compareTo(bs2)));
	}

	@Test
	void testToString() {
		ByteSequence bsAll = ByteSequence.wrap(TEST_BYTES_ALL);

		Assertions.assertEquals("[0:17] = { 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10 }", bsAll.toString());
	}

}
