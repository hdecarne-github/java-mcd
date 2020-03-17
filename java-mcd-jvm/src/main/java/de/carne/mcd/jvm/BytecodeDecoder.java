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
import java.util.Optional;

import de.carne.boot.logging.Log;
import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.common.instruction.InstructionIndex;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;
import de.carne.mcd.jvm.bytecode.BytecodeInstructionIndex;
import de.carne.mcd.jvm.classfile.ClassInfo;
import de.carne.text.HexFormat;
import de.carne.util.Late;

/**
 * Java bytecode decoder
 */
public class BytecodeDecoder extends MachineCodeDecoder {

	private static final Log LOG = new Log();

	/**
	 * Decoder name.
	 */
	@SuppressWarnings("squid:S1845")
	public static final String NAME = "Java bytecode";

	private static final Late<InstructionIndex> BYTECODE_INSTRUCTION_INDEX_HOLDER = new Late<>();

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

	/**
	 * Gets the {@linkplain ClassInfo} associated with this {@linkplain BytecodeDecoder} instance.
	 *
	 * @return the {@linkplain ClassInfo} associated with this {@linkplain BytecodeDecoder} instance.
	 */
	public ClassInfo getClassInfo() {
		return this.classInfo;
	}

	@Override
	protected long decode0(MCDInputBuffer in, MCDOutputBuffer out, long offset, long limit) throws IOException {
		out.printComment("// max_stack: ").printlnComment(Integer.toString(Short.toUnsignedInt(in.decodeI16())));
		out.printComment("// max_locals: ").printlnComment(Integer.toString(Short.toUnsignedInt(in.decodeI16())));

		long codeLength = Integer.toUnsignedLong(in.decodeI32());
		MCDInputBuffer codeBuffer = new MCDInputBuffer(in.slice(codeLength), byteOrder());
		InstructionIndex instructionIndex = getBytecodeInstructionIndex();
		InstructionIndex.LookupResult lookupResult;
		long pc = offset;

		while ((lookupResult = instructionIndex.lookupNextInstruction(codeBuffer, false)) != null) {
			String pcString = HexFormat.LOWER_CASE.format((short) pc) + ":";

			out.printLabel(pcString).print(" ");
			try {
				lookupResult.decode(pc, codeBuffer, out);
				pc = offset + codeBuffer.getTotalRead();
			} catch (IOException e) {
				String opcodeString = lookupResult.opcode().toString();

				LOG.warning(e, "Decode failure at {0} for opcode: {1}", pcString, opcodeString);

				out.printlnError(opcodeString);
			}
		}
		return in.getTotalRead();
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
