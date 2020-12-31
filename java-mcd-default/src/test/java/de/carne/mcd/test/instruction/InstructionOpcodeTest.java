/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.test.instruction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.carne.mcd.instruction.InstructionOpcode;

/**
 * Test {@linkplain InstructionOpcode} class.
 */
class InstructionOpcodeTest {

	private static final byte[] TEST_BYTES_A = {};
	private static final byte[] TEST_BYTES_B = { 10, 11, 12, 13, 14, 15, 16 };
	private static final byte[] TEST_BYTES_C = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private static final byte[] TEST_BYTES_D = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

	private static final int ENCODED_BYTES = 32;

	@Test
	void testEquals() {
		InstructionOpcode bsAll = InstructionOpcode.wrap(TEST_BYTES_D);
		InstructionOpcode bs3 = InstructionOpcode.wrap(TEST_BYTES_A);

		Assertions.assertTrue(bsAll.equals(bsAll));
		Assertions.assertTrue(bs3.equals(InstructionOpcode.EMPTY));
	}

	@Test
	void testCompare() {
		InstructionOpcode opcodeA = InstructionOpcode.wrap(TEST_BYTES_A);
		InstructionOpcode opcodeB = InstructionOpcode.wrap(TEST_BYTES_B);
		InstructionOpcode opcodeC = InstructionOpcode.wrap(TEST_BYTES_C);
		InstructionOpcode opcodeD = InstructionOpcode.wrap(TEST_BYTES_D);

		Assertions.assertEquals(0, opcodeA.compareTo(opcodeA));
		Assertions.assertEquals(-1, opcodeA.compareTo(opcodeB));
		Assertions.assertEquals(-1, opcodeA.compareTo(opcodeC));
		Assertions.assertEquals(-1, opcodeA.compareTo(opcodeD));

		Assertions.assertEquals(1, opcodeB.compareTo(opcodeA));
		Assertions.assertEquals(0, opcodeB.compareTo(opcodeB));
		Assertions.assertEquals(-1, opcodeB.compareTo(opcodeC));
		Assertions.assertEquals(-1, opcodeB.compareTo(opcodeD));

		Assertions.assertEquals(1, opcodeC.compareTo(opcodeA));
		Assertions.assertEquals(1, opcodeC.compareTo(opcodeB));
		Assertions.assertEquals(0, opcodeC.compareTo(opcodeC));
		Assertions.assertEquals(-1, opcodeC.compareTo(opcodeD));

		Assertions.assertEquals(1, opcodeD.compareTo(opcodeA));
		Assertions.assertEquals(1, opcodeD.compareTo(opcodeB));
		Assertions.assertEquals(1, opcodeD.compareTo(opcodeC));
		Assertions.assertEquals(0, opcodeD.compareTo(opcodeD));
	}

	@Test
	void testByteAccess() {
		InstructionOpcode opcodeB = InstructionOpcode.wrap(TEST_BYTES_B);

		Assertions.assertArrayEquals(TEST_BYTES_B, opcodeB.bytes());
		Assertions.assertEquals(TEST_BYTES_B.length, opcodeB.length());
		for (int n = 0; n < TEST_BYTES_B.length; n++) {
			Assertions.assertEquals(TEST_BYTES_B[n], opcodeB.byteAt(n));
		}
	}

	@Test
	void testEncodeDecode() {
		InstructionOpcode opcodeD = InstructionOpcode.wrap(TEST_BYTES_D);
		byte[] encoded = opcodeD.encode(ENCODED_BYTES);

		Assertions.assertEquals(ENCODED_BYTES, encoded.length);

		InstructionOpcode decoded = InstructionOpcode.decode(encoded);

		Assertions.assertEquals(opcodeD, decoded);
	}

	@Test
	void testToString() {
		InstructionOpcode opcodeD = InstructionOpcode.wrap(TEST_BYTES_D);

		Assertions.assertEquals("00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10", opcodeD.toString());
		Assertions.assertArrayEquals(TEST_BYTES_D, InstructionOpcode.parse(opcodeD.toString()));
	}

}
