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
package de.carne.mcd.jvm.classfile.attribute;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

import de.carne.mcd.jvm.classfile.ClassContext;
import de.carne.mcd.jvm.classfile.ClassInfo;
import de.carne.mcd.jvm.classfile.ClassPrinter;
import de.carne.mcd.jvm.classfile.bytecode.BytecodeDecoder;

/**
 * Code attribute.
 */
public class CodeAttribute extends Attribute {

	/**
	 * The Code attribute name.
	 */
	public static final String NAME = "Code";

	private final SeekableByteChannel code;

	/**
	 * Constructs a new {@linkplain CodeAttribute} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this attribute is part of.
	 * @param code the bytecode data
	 */
	public CodeAttribute(ClassInfo classInfo, SeekableByteChannel code) {
		super(classInfo);
		this.code = code;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		BytecodeDecoder decoder = new BytecodeDecoder(this.classInfo);

		decoder.decode(this.code, out.output());
	}

}
