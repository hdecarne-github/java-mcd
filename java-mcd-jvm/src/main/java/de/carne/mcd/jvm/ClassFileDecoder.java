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
package de.carne.mcd.jvm;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

import de.carne.mcd.common.MCDOutput;
import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.jvm.decode.DecodedClassInfo;

/**
 * Java Bytecode decoder.
 */
public class ClassFileDecoder extends MachineCodeDecoder {

	private static final String NAME = "Java Bytecode";

	/**
	 * Constructs a new {@linkplain ClassFileDecoder} instance.
	 */
	public ClassFileDecoder() {
		super(NAME, ByteOrder.BIG_ENDIAN);
	}

	@Override
	public void decode(ReadableByteChannel in, MCDOutput out) throws IOException {
		DecodedClassInfo decoded = DecodedClassInfo.decode(newDecodeBuffer(in));

		ClassPrinter.getInstance(out, decoded).print();
	}

}
