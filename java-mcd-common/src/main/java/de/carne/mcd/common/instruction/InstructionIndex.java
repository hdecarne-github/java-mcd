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
package de.carne.mcd.common.instruction;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.logging.Log;
import de.carne.io.IOUtil;
import de.carne.mcd.common.bootstrap.InstructionIndexBuilder;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;

/**
 * Loads and searches a persistent index for mapping opcode bytes to {@linkplain Instruction} instances.
 */
public final class InstructionIndex implements InstructionIndexParameters, Closeable {

	private static final Log LOG = new Log();

	private final InstructionFactory instructionFactory;
	private final int entryCount;
	private final int entryBytes;
	private final int opcodeBytes;
	private final byte[] lookupTable;
	private final FileChannel dataFile;
	private final Map<Integer, SoftReference<Instruction>> instructionCache = new HashMap<>();

	private InstructionIndex(InstructionFactory instructionFactory, int entryCount, int entryBytes, int opcodeBytes,
			byte[] lookupTable, FileChannel dataFile) {
		this.instructionFactory = instructionFactory;
		this.entryCount = entryCount;
		this.entryBytes = entryBytes;
		this.opcodeBytes = opcodeBytes;
		this.lookupTable = lookupTable;
		this.dataFile = dataFile;
	}

	/**
	 * Opens an {@linkplain InstructionIndex} previously created via the {@linkplain InstructionIndexBuilder} class.
	 *
	 * @param instructionFactory the {@linkplain InstructionFactory} to use for this index.
	 * @param url the {@linkplain URL} to use for index data access.
	 * @return the opened {@linkplain InstructionIndex} instance.
	 * @throws IOException if an I/O error occurs.
	 */
	public static InstructionIndex open(InstructionFactory instructionFactory, URL url) throws IOException {
		LOG.info("Opening index: ''{0}''...", url);

		InstructionIndex index;

		try (DataInputStream indexStream = new DataInputStream(url.openStream())) {
			int parameters = indexStream.readInt();
			int entryCount = (parameters >> 8) & 0xffffff;
			int opcodeBytes = 1 + (parameters >> 4) & 0xf;
			int positionBytes = 1 + (parameters & 0xf);

			LOG.debug(" Index parameters: {0}/{1}/{2}", entryCount, opcodeBytes, positionBytes);

			int entryBytes = opcodeBytes + positionBytes;
			byte[] lookupTable = new byte[entryCount * entryBytes];

			indexStream.readFully(lookupTable);

			Path dataFilePath = Files.createTempFile(InstructionIndex.class.getSimpleName(), null);

			LOG.debug(" Data file: ''{0}''", dataFilePath);

			IOUtil.copyStream(dataFilePath.toFile(), indexStream);
			index = new InstructionIndex(instructionFactory, entryCount, entryBytes, opcodeBytes, lookupTable,
					FileChannel.open(dataFilePath, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE));
		}
		return index;
	}

	/**
	 * Index lookup result.
	 */
	public static final class LookupResult {

		private final InstructionOpcode opcode;
		private final Instruction instruction;

		LookupResult(byte[] opcodeBytes, int offset, int length, Instruction instruction) {
			this.opcode = InstructionOpcode.wrap(opcodeBytes, offset, length);
			this.instruction = instruction;
		}

		/**
		 * Gets the matching {@linkplain InstructionOpcode} of the lookup.
		 *
		 * @return the matching {@linkplain InstructionOpcode} of the lookup.
		 */
		public InstructionOpcode opcode() {
			return this.opcode;
		}

		/**
		 * Gets the matching {@linkplain Instruction} of the lookup.
		 *
		 * @return the matching {@linkplain Instruction} of the lookup.
		 */
		public Instruction instruction() {
			return this.instruction;
		}

		/**
		 * Invokes the {@linkplain Instruction#decode(long, InstructionOpcode, MCDInputBuffer, MCDOutputBuffer)}
		 * function for this lookup result.
		 *
		 * @param ip the current instruction pointer.
		 * @param in the {@linkplain MCDInputBuffer} to read any additional instruction data from.
		 * @param out the {@linkplain MCDOutputBuffer} to decode to.
		 * @throws IOException if an I/O error occurs.
		 */
		public void decode(long ip, MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
			this.instruction.decode(ip, this.opcode, in, out);
		}

	}

	/**
	 * Looks up the next {@linkplain Instruction} instance corresponding to the opcode bytes provided via the given
	 * {@linkplain MCDInputBuffer} instance.
	 *
	 * @param buffer the {@linkplain MCDInputBuffer} to read the opcode bytes from.
	 * @param eager whether to match the maximum length opcode or not.
	 * @return the resolved {@linkplain Instruction} instance (or {@code null} if EOF is reached).
	 * @throws IOException if an I/O error occurs.
	 */
	@SuppressWarnings("squid:S3776")
	@Nullable
	public LookupResult lookupNextInstruction(MCDInputBuffer buffer, boolean eager) throws IOException {
		LookupResult lookupResult = null;
		int opcodeByte = buffer.read();

		if (opcodeByte >= 0) {
			byte[] opcode = new byte[this.opcodeBytes];
			int opcodeOffset = 0;
			int opcodeLength = 0;
			int previousMatch = -1;

			while (lookupResult == null) {
				int match = -1;

				if (opcodeByte >= 0) {
					opcode[opcodeOffset + opcodeLength] = (byte) opcodeByte;
					opcodeLength++;
					match = matchOpcode(opcode, opcodeOffset, opcodeLength);
				}
				if (match >= 0) {
					if (opcodeOffset == 0) {
						if (!eager) {
							lookupResult = new LookupResult(opcode, 0, opcodeLength, loadInstruction(match));
						} else {
							previousMatch = match;
							opcodeByte = buffer.read();
						}
					} else {
						lookupResult = new LookupResult(opcode, 0, opcodeOffset,
								this.instructionFactory.getDefaultInstruction());
						buffer.discard(-opcodeLength);
					}
				} else if (previousMatch >= 0) {
					if (opcodeByte >= 0) {
						int previousOpcodeLength = opcodeLength - 1;

						lookupResult = new LookupResult(opcode, 0, previousOpcodeLength,
								loadInstruction(previousMatch));
						buffer.discard(-1);
					} else {
						lookupResult = new LookupResult(opcode, 0, opcodeLength, loadInstruction(previousMatch));
					}
				} else if (opcodeByte >= 0 && opcodeLength < this.opcodeBytes - 1) {
					opcodeByte = buffer.read();
				} else if (opcodeOffset < 1) {
					opcodeOffset++;
					buffer.discard(-Math.max(0, opcodeLength - 1));
					opcodeLength = 0;
					opcodeByte = buffer.read();
				} else if (opcodeLength > 0) {
					lookupResult = new LookupResult(opcode, 0, opcodeOffset,
							this.instructionFactory.getDefaultInstruction());
					buffer.discard(-opcodeLength);
				} else {
					lookupResult = new LookupResult(opcode, 0, opcodeOffset + opcodeLength,
							this.instructionFactory.getDefaultInstruction());
				}
			}
		}
		return lookupResult;
	}

	private int matchOpcode(byte[] bytes, int offset, int length) {
		int matchStart = 0;
		int matchEnd = this.entryCount;
		int match = -1;

		while (matchStart < matchEnd && match < 0) {
			int matchNext = matchStart + (matchEnd - matchStart) / 2;
			int lookupTableOffset = matchNext * this.entryBytes;
			int comparision = InstructionOpcode.compareTo(bytes, offset, length, this.lookupTable,
					lookupTableOffset + 1, this.lookupTable[lookupTableOffset] & 0xff);

			if (comparision < 0) {
				matchEnd = matchNext;
			} else if (comparision > 0) {
				matchStart = matchNext + 1;
			} else {
				match = matchNext;
			}
		}
		return match;
	}

	private synchronized Instruction loadInstruction(int lookupTableIndex) throws IOException {
		Integer dataCacheKey = Integer.valueOf(lookupTableIndex);
		SoftReference<Instruction> instructionReference = this.instructionCache.get(dataCacheKey);
		Instruction instruction = (instructionReference != null ? instructionReference.get() : null);

		if (instruction == null) {
			int positionBytes = positionBytes();
			long dataPosition = getDecodedPosition(this.lookupTable,
					(lookupTableIndex * this.entryBytes) + this.opcodeBytes, positionBytes);

			this.dataFile.position(dataPosition);
			instruction = this.instructionFactory
					.loadInstruction(new DataInputStream(Channels.newInputStream(this.dataFile)));
			this.instructionCache.put(dataCacheKey, new SoftReference<>(instruction));
		}
		return instruction;
	}

	private long getDecodedPosition(byte[] bytes, int offset, int length) {
		long decoded = 0;

		for (int decodeIndex = length - 1; decodeIndex >= 0; decodeIndex--) {
			decoded = (decoded << 8) | Byte.toUnsignedInt(bytes[offset + decodeIndex]);
		}
		return decoded;
	}

	@Override
	public int entryCount() {
		return this.entryCount;
	}

	@Override
	public int opcodeBytes() {
		return this.opcodeBytes;
	}

	@Override
	public int positionBytes() {
		return this.entryBytes - this.opcodeBytes;
	}

	@Override
	public void close() throws IOException {
		this.dataFile.close();
	}

}
