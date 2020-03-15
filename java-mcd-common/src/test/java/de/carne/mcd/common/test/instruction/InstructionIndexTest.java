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
package de.carne.mcd.common.test.instruction;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.carne.mcd.common.bootstrap.InstructionIndexBuilder;
import de.carne.mcd.common.instruction.Instruction;
import de.carne.mcd.common.instruction.InstructionFactory;
import de.carne.mcd.common.instruction.InstructionIndex;
import de.carne.mcd.common.instruction.InstructionOpcode;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;
import de.carne.test.api.io.TempFile;
import de.carne.test.extension.TempPathExtension;

/**
 * Test {@linkplain InstructionIndex} class.
 */
@ExtendWith(TempPathExtension.class)
class InstructionIndexTest {

	private static final InstructionOpcode OPCODE_00 = InstructionOpcode.wrap(new byte[] { 0x00 });
	private static final InstructionOpcode OPCODE_01 = InstructionOpcode.wrap(new byte[] { 0x01 });
	private static final InstructionOpcode OPCODE_02 = InstructionOpcode.wrap(new byte[] { 0x02 });
	private static final InstructionOpcode OPCODE_0101 = InstructionOpcode.wrap(new byte[] { 0x01, 0x01 });
	private static final InstructionOpcode OPCODE_0102 = InstructionOpcode.wrap(new byte[] { 0x01, 0x02 });
	private static final InstructionOpcode OPCODE_0201 = InstructionOpcode.wrap(new byte[] { 0x02, 0x01 });
	private static final InstructionOpcode OPCODE_0202 = InstructionOpcode.wrap(new byte[] { 0x02, 0x02 });

	private static final InstructionOpcode OPCODE_UNKNOWN1 = InstructionOpcode.wrap(new byte[] { 0x03, 0x04, 0x05, 0x06,
			0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12 });
	private static final InstructionOpcode OPCODE_UNKNOWN2 = InstructionOpcode.wrap(new byte[] { 0x13, 0x14 });

	private static final byte[] TEST_CODE = { 0x00, 0x01, 0x01, 0x02, 0x01, 0x01, 0x02, 0x02, 0x02, 0x03, 0x04, 0x05,
			0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x00 };

	private static final InstructionFactory INSTRUCTION_FACTORY = new InstructionFactory() {

		@Override
		public Instruction loadInstruction(DataInput in) throws IOException {
			int opcodeLength = in.readInt();
			byte[] opcode = new byte[opcodeLength];

			in.readFully(opcode);
			return new TestInstruction(InstructionOpcode.wrap(opcode));
		}

		@Override
		public Instruction getDefaultInstruction(byte[] opcode, int offset, int length) throws IOException {
			return new TestInstruction(InstructionOpcode.wrap(opcode, offset, length));
		}

	};

	@Test
	void testBuilder() throws IOException {
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		bootstrapInstructionIndex(builder);

		Assertions.assertEquals(0x0731, builder.parameters());
		Assertions.assertEquals(7, builder.entryCount());
		Assertions.assertEquals(4, builder.opcodeBytes());
		Assertions.assertEquals(2, builder.positionBytes());
	}

	@Test
	void testStoreAndOpen(@TempFile File indexFile) throws IOException {
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		bootstrapInstructionIndex(builder);
		builder.save(indexFile);

		try (InstructionIndex index = InstructionIndex.open(INSTRUCTION_FACTORY, indexFile.toPath().toUri().toURL())) {
			Assertions.assertEquals(builder.parameters(), index.parameters());
			Assertions.assertEquals(builder.entryCount(), index.entryCount());
			Assertions.assertEquals(builder.opcodeBytes(), index.opcodeBytes());
			Assertions.assertEquals(builder.positionBytes(), index.positionBytes());
		}
	}

	@Test
	void testNonEagerLookup(@TempFile File indexFile) throws IOException {
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		bootstrapInstructionIndex(builder);
		builder.save(indexFile);

		try (InstructionIndex index = InstructionIndex.open(INSTRUCTION_FACTORY, indexFile.toPath().toUri().toURL());
				ReadableByteChannel testCodeChannel = Channels.newChannel(new ByteArrayInputStream(TEST_CODE))) {
			MCDInputBuffer buffer = new MCDInputBuffer(testCodeChannel, ByteOrder.nativeOrder());

			buffer.setAutoCommit(false);
			Assertions.assertEquals(OPCODE_00,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_01,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_01,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_02,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_01,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_01,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_02,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_02,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_02,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_UNKNOWN1,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_UNKNOWN2,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertEquals(OPCODE_00,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, false)).opcode());
			Assertions.assertNull(index.lookupNextInstruction(buffer, true));
			Assertions.assertNull(index.lookupNextInstruction(buffer, true));
		}
	}

	@Test
	void testEagerLookup(@TempFile File indexFile) throws IOException {
		InstructionIndexBuilder builder = new InstructionIndexBuilder();

		bootstrapInstructionIndex(builder);
		builder.save(indexFile);

		try (InstructionIndex index = InstructionIndex.open(INSTRUCTION_FACTORY, indexFile.toPath().toUri().toURL());
				ReadableByteChannel testCodeChannel = Channels.newChannel(new ByteArrayInputStream(TEST_CODE))) {
			MCDInputBuffer buffer = new MCDInputBuffer(testCodeChannel, ByteOrder.nativeOrder());

			buffer.setAutoCommit(false);
			Assertions.assertEquals(OPCODE_00,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertEquals(OPCODE_0101,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertEquals(OPCODE_0201,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertEquals(OPCODE_0102,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertEquals(OPCODE_0202,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertEquals(OPCODE_UNKNOWN1,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertEquals(OPCODE_UNKNOWN2,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertEquals(OPCODE_00,
					Objects.requireNonNull(index.lookupNextInstruction(buffer, true)).opcode());
			Assertions.assertNull(index.lookupNextInstruction(buffer, true));
			Assertions.assertNull(index.lookupNextInstruction(buffer, true));
		}
	}

	private void bootstrapInstructionIndex(InstructionIndexBuilder builder) throws IOException {
		builder.add(OPCODE_00, new TestInstruction(OPCODE_00));
		builder.add(OPCODE_01, new TestInstruction(OPCODE_01));
		builder.add(OPCODE_02, new TestInstruction(OPCODE_02));
		builder.add(OPCODE_0101, new TestInstruction(OPCODE_0101));
		builder.add(OPCODE_0102, new TestInstruction(OPCODE_0102));
		builder.add(OPCODE_0201, new TestInstruction(OPCODE_0201));
		builder.add(OPCODE_0202, new TestInstruction(OPCODE_0202));
	}

	private static class TestInstruction implements Instruction {

		private final InstructionOpcode instructionOpcode;

		TestInstruction(InstructionOpcode opcode) {
			this.instructionOpcode = opcode;
		}

		@Override
		public void save(DataOutput out) throws IOException {
			byte[] opcodeBytes = this.instructionOpcode.encode(1 + this.instructionOpcode.length());

			out.writeInt(opcodeBytes.length);
			out.write(opcodeBytes);
		}

		@Override
		public void decode(long ip, InstructionOpcode opcode, MCDInputBuffer in, MCDOutputBuffer out)
				throws IOException {
			out.printlnValue(opcode.toString());
		}

	}

}
