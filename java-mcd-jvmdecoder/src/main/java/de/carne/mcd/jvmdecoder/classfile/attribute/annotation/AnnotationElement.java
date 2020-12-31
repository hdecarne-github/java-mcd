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
package de.carne.mcd.jvmdecoder.classfile.attribute.annotation;

import java.io.IOException;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassInfoElement;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.constant.Utf8Constant;

/**
 * A single annotation element.
 */
public class AnnotationElement extends ClassInfoElement {

	private final int nameIndex;
	private final AnnotationElementValue value;

	/**
	 * Constructs a new {@linkplain AnnotationElement} instance.
	 * 
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param nameIndex the element name index.
	 * @param value the element value.
	 */
	public AnnotationElement(ClassInfo classInfo, int nameIndex, AnnotationElementValue value) {
		super(classInfo);
		this.nameIndex = nameIndex;
		this.value = value;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		String elementName = this.classInfo.resolveConstant(this.nameIndex, Utf8Constant.class).getValue();

		out.print(elementName);
		out.print(" ");
		out.printOperator("=");
		out.print(" ");
		this.value.print(out, context);
	}

}
