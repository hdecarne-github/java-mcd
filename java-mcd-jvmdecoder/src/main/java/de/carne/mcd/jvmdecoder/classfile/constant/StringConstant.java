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
package de.carne.mcd.jvmdecoder.classfile.constant;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.util.Strings;

/**
 * String constant.
 */
public class StringConstant extends Constant {

	/**
	 * String constant tag.
	 */
	public static final int TAG = 8;

	private int stringIndex;

	/**
	 * Constructs a new {@linkplain DoubleConstant} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this constant is part of.
	 * @param stringIndex the constant value index.
	 */
	public StringConstant(ClassInfo classInfo, int stringIndex) {
		super(classInfo);
		this.stringIndex = stringIndex;
	}

	/**
	 * Gets this constant's value.
	 * 
	 * @return this constant's value.
	 * @throws IOException if the value cannot be resolved.
	 */
	public String getValue() throws IOException {
		return this.classInfo.resolveConstant(this.stringIndex, Utf8Constant.class).getValue();
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		this.classInfo.resolveConstant(this.stringIndex, Utf8Constant.class).print(out, context);
	}

	@Override
	public @NonNull String resolveSymbol() throws IOException {
		return "\"" + Strings.encode(getValue()) + "\"";
	}

	@Override
	public String toString() {
		return "#" + this.stringIndex;
	}

}
