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
package de.carne.mcd.jvmdecoder.classfile.attribute.annotation;

import java.io.IOException;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.constant.IntegerConstant;
import de.carne.util.Strings;

/**
 * Character annotation element.
 */
public class CharAnnotationElement extends AbstractConstantValueAnnotationElement {

	/**
	 * Character annotation element tag.
	 */
	public static final int TAG = 'C';

	/**
	 * Constructs a new {@linkplain CharAnnotationElement}} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param valueIndex the annotation element value index.
	 */
	public CharAnnotationElement(ClassInfo classInfo, int valueIndex) {
		super(classInfo, valueIndex);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		char value = (char) this.classInfo.resolveConstant(this.valueIndex, IntegerConstant.class).getValue();

		out.printValue("'" + Strings.encode(Character.toString(value)) + "'");
	}

}
