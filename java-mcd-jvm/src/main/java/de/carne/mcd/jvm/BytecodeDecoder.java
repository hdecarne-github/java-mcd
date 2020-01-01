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
package de.carne.mcd.jvm;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

import de.carne.mcd.common.Instruction;
import de.carne.mcd.common.InstructionIndex;
import de.carne.mcd.common.MCDDecodeBuffer;
import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.jvm.bytecode.BytecodeInstructionIndex;
import de.carne.text.HexFormatter;
import de.carne.util.Late;

/**
 * Java bytecode decoder
 */
public class BytecodeDecoder extends MachineCodeDecoder {

	private static final String NAME = "Java bytecode";

	private static final Late<InstructionIndex> BYTECODE_INSTRUCTION_INDEX_HOLDER = new Late<>();

	private static final HexFormatter PC_FORMATTER = new HexFormatter(false);

	private final ClassInfo classInfo;

	/**
	 * Constructs a new {@linkplain BytecodeDecoder} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} of the surrounding class.
	 */
	public BytecodeDecoder(ClassInfo classInfo) {
		super(NAME, ByteOrder.BIG_ENDIAN);
		this.classInfo = classInfo;
	}

	@Override
	public void decode(ReadableByteChannel in, MCDOutput out) throws IOException {
		MCDDecodeBuffer buffer = newDecodeBuffer(in);

		out.printComment("// max_stack: ").printlnComment(Integer.toString(Short.toUnsignedInt(buffer.decodeI16())));
		out.printComment("// max_locals: ").printlnComment(Integer.toString(Short.toUnsignedInt(buffer.decodeI16())));

		long codeLength = Integer.toUnsignedLong(buffer.decodeI32());
		MCDDecodeBuffer codeBuffer = newDecodeBuffer(buffer.slice(codeLength));
		InstructionIndex instructionIndex = getBytecodeInstructionIndex();
		Instruction instruction;
		long instructionPc = 0;

		while ((instruction = instructionIndex.lookupNextInstruction(codeBuffer)) != null) {
			out.printLabel(PC_FORMATTER.format((short) instructionPc)).printLabel(":").print(" ");
			instruction.decode(buffer, out);
		}
	}

	@SuppressWarnings("resource")
	private static InstructionIndex getBytecodeInstructionIndex() throws IOException {
		InstructionIndex instructionIndex;

		synchronized (BYTECODE_INSTRUCTION_INDEX_HOLDER) {
			Optional<InstructionIndex> instructionIndexHolder = BYTECODE_INSTRUCTION_INDEX_HOLDER.getOptional();

			if (instructionIndexHolder.isPresent()) {
				instructionIndex = instructionIndexHolder.get();
			} else {
				instructionIndex = BYTECODE_INSTRUCTION_INDEX_HOLDER.set(BytecodeInstructionIndex.open());
			}
		}
		return instructionIndex;
	}

}
