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
package de.carne.mcd.jvmdecoder.classfile.attribute;

import java.io.IOException;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.PrintSeparator;
import de.carne.mcd.jvmdecoder.classfile.constant.ClassConstant;

/**
 * ConstantValue attribute.
 */
public class ExceptionsAttribute extends Attribute {

	/**
	 * The Exceptions attribute name.
	 */
	public static final String NAME = "Exceptions";

	private final int[] exceptions;

	/**
	 * Constructs a new {@linkplain ExceptionsAttribute} instance.
	 * 
	 * @param classInfo the {@linkplain ClassInfo} instance this attribute is part of.
	 * @param exceptions the exception indexes
	 */
	public ExceptionsAttribute(ClassInfo classInfo, int[] exceptions) {
		super(classInfo);
		this.exceptions = exceptions;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		if (this.exceptions.length > 0) {
			out.print(" ").printKeyword(ClassPrinter.S_THROWS).print(" ");

			PrintSeparator separator = new PrintSeparator();

			for (int exception : this.exceptions) {
				separator.print(out, context);
				this.classInfo.resolveConstant(exception, ClassConstant.class).print(out, context);
			}
		}
	}

}
