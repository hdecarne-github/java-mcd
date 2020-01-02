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
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.carne.mcd.common.Instruction;
import de.carne.mcd.common.InstructionFactory;
import de.carne.mcd.common.InstructionIndex;
import de.carne.mcd.common.InstructionIndexBuilder;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.common.Opcode;
import de.carne.test.api.io.TempFile;
import de.carne.test.extension.TempPathExtension;
import de.carne.text.HexFormatter;

/**
 * Test {@linkplain InstructionIndex} class.
 */
@ExtendWith(TempPathExtension.class)
class InstructionIndexTest {

	private static final byte[] OPCODE_00 = { 0x00 };
	private static final byte[] OPCODE_0101 = { 0x01, 0x01 };
	private static final byte[] OPCODE_0102 = { 0x01, 0x02 };
	private static final byte[] OPCODE_0201 = { 0x02, 0x01 };
	private static final byte[] OPCODE_0202 = { 0x02, 0x02 };

	private static final byte[] OPCODE_UNKNOWN1 = { 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d,
			0x0e, 0x0f, 0x10, 0x11, 0x12 };
	private static final byte[] OPCODE_UNKNOWN2 = { 0x13, 0x14 };

	private static final byte[] TEST_CODE = { 0x00, 0x01, 0x01, 0x02, 0x01, 0x01, 0x02, 0x02, 0x02, 0x03, 0x04, 0x05,
			0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x00 };

	private static final InstructionFactory INSTRUCTION_FACTORY = new InstructionFactory() {

		@Override
		public Instruction loadInstruction(DataInput in) throws IOException {
			int opcodeLength = in.readInt();
			byte[] opcode = new byte[opcodeLength];

			in.readFully(opcode);
			return new TestInstruction(opcode);
		}

		@Override
		public Instruction getDefaultInstruction(byte[] opcode, int offset, int length) throws IOException {
			return new TestInstruction(opcode, offset, length);
		}

	};

	@Test
	void testBuilder() throws IOException {
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		bootstrapInstructionIndex(builder);

		Assertions.assertEquals(0x0531, builder.parameters());
		Assertions.assertEquals(5, builder.entryCount());
		Assertions.assertEquals(4, builder.opcodeBytes());
		Assertions.assertEquals(2, builder.positionBytes());
	}

	@Test
	void testStoreAndOpen(@TempFile Path indexFile) throws IOException {
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		bootstrapInstructionIndex(builder);
		storeIndex(indexFile, builder);

		try (InstructionIndex index = InstructionIndex.open(INSTRUCTION_FACTORY, indexFile.toUri().toURL())) {
			Assertions.assertEquals(builder.parameters(), index.parameters());
			Assertions.assertEquals(builder.entryCount(), index.entryCount());
			Assertions.assertEquals(builder.opcodeBytes(), index.opcodeBytes());
			Assertions.assertEquals(builder.positionBytes(), index.positionBytes());
		}
	}

	@Test
	void testLookup(@TempFile Path indexFile) throws IOException {
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		bootstrapInstructionIndex(builder);
		storeIndex(indexFile, builder);

		try (InstructionIndex index = InstructionIndex.open(INSTRUCTION_FACTORY, indexFile.toUri().toURL());
				ReadableByteChannel testCodeChannel = Channels.newChannel(new ByteArrayInputStream(TEST_CODE))) {
			MCDDecodeBuffer buffer = new MCDDecodeBuffer(testCodeChannel, ByteOrder.nativeOrder());

			Assertions.assertArrayEquals(OPCODE_00, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertArrayEquals(OPCODE_0101, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertArrayEquals(OPCODE_0201, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertArrayEquals(OPCODE_0102, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertArrayEquals(OPCODE_0202, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertArrayEquals(OPCODE_UNKNOWN1, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertArrayEquals(OPCODE_UNKNOWN2, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertArrayEquals(OPCODE_00, getOpcode(index.lookupNextInstruction(buffer)));
			Assertions.assertNull(index.lookupNextInstruction(buffer));
			Assertions.assertNull(index.lookupNextInstruction(buffer));
		}
	}

	private byte[] getOpcode(@Nullable Instruction instruction) {
		return (instruction instanceof TestInstruction ? ((TestInstruction) instruction).opcode() : new byte[0]);
	}

	private void bootstrapInstructionIndex(InstructionIndexBuilder builder) throws IOException {
		builder.add(Opcode.wrap(OPCODE_00), new TestInstruction(OPCODE_00));
		builder.add(Opcode.wrap(OPCODE_0101), new TestInstruction(OPCODE_0101));
		builder.add(Opcode.wrap(OPCODE_0102), new TestInstruction(OPCODE_0102));
		builder.add(Opcode.wrap(OPCODE_0201), new TestInstruction(OPCODE_0201));
		builder.add(Opcode.wrap(OPCODE_0202), new TestInstruction(OPCODE_0202));
	}

	private void storeIndex(Path indexFile, InstructionIndexBuilder builder) throws IOException {
		try (DataOutputStream indexOut = new DataOutputStream(
				Files.newOutputStream(indexFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))) {
			builder.store(indexOut);
		}
	}

	private static class TestInstruction implements Instruction {

		private final byte[] opcode;
		private final int offset;
		private final int length;

		TestInstruction(byte[] opcode) {
			this(opcode, 0, opcode.length);
		}

		TestInstruction(byte[] opcode, int offset, int length) {
			this.opcode = opcode;
			this.offset = offset;
			this.length = length;
		}

		public byte[] opcode() {
			byte[] opcodeBytes = new byte[this.length];

			System.arraycopy(this.opcode, this.offset, opcodeBytes, 0, this.length);
			return opcodeBytes;
		}

		@Override
		public void store(DataOutput out) throws IOException {
			out.writeInt(this.length);
			out.write(this.opcode, this.offset, this.length);
		}

		@Override
		public void decode(int pc, MCDDecodeBuffer buffer, MCDOutput out) throws IOException {
			out.printlnValue(HexFormatter.UPPER_CASE.format(this.opcode, this.offset, this.length));
		}

	}

}
