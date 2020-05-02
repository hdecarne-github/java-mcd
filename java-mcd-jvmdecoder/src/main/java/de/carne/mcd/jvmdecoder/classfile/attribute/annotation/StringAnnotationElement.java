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
import de.carne.mcd.jvmdecoder.classfile.constant.Utf8Constant;
import de.carne.util.Strings;

/**
 * String annotation element.
 */
public class StringAnnotationElement extends AbstractConstantValueAnnotationElement {

	/**
	 * String annotation element tag.
	 */
	public static final int TAG = 's';

	/**
	 * Constructs a new {@linkplain StringAnnotationElement}} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param valueIndex the annotation element value index.
	 */
	public StringAnnotationElement(ClassInfo classInfo, int valueIndex) {
		super(classInfo, valueIndex);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		out.printValue(
				"\"" + Strings.encode(this.classInfo.resolveConstant(this.valueIndex, Utf8Constant.class).getValue())
						+ "\"");
	}

}
