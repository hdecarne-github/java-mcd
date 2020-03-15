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

import de.carne.mcd.common.MachineCodeDecoder;
import de.carne.mcd.common.io.MCDInputBuffer;
import de.carne.mcd.common.io.MCDOutputBuffer;
import de.carne.mcd.jvm.classfile.ClassPrinter;
import de.carne.mcd.jvm.classfile.DecodedClassInfo;

/**
 * Java class file decoder.
 */
public class ClassFileDecoder extends MachineCodeDecoder {

	/**
	 * Decoder name.
	 */
	@SuppressWarnings("squid:S1845")
	public static final String NAME = "Java class file";

	/**
	 * Constructs a new {@linkplain ClassFileDecoder} instance.
	 */
	public ClassFileDecoder() {
		super(NAME, ByteOrder.BIG_ENDIAN);
	}

	@Override
	protected void decode0(MCDInputBuffer in, MCDOutputBuffer out) throws IOException {
		DecodedClassInfo decoded = DecodedClassInfo.decode(in);

		ClassPrinter.getInstance(out, decoded).print();
	}

}
