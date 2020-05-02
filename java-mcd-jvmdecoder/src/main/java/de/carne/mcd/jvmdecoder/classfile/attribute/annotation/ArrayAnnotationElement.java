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
import java.util.Collections;
import java.util.List;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.PrintSeparator;

/**
 * Array annotation element.
 */
public class ArrayAnnotationElement extends AnnotationElementValue {

	/**
	 * Array annotation element tag.
	 */
	public static final int TAG = '[';

	private final List<AnnotationElementValue> elementValues;

	/**
	 * Constructs a new {@linkplain ArrayAnnotationElement} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param elementValues the array annotation elements.
	 */
	public ArrayAnnotationElement(ClassInfo classInfo, List<AnnotationElementValue> elementValues) {
		super(classInfo);
		this.elementValues = Collections.unmodifiableList(elementValues);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		int elementValuesSize = this.elementValues.size();

		if (elementValuesSize > 1) {
			out.print("{ ");
		}

		PrintSeparator separator = new PrintSeparator();

		for (AnnotationElementValue elementValue : this.elementValues) {
			separator.print(out, context);
			elementValue.print(out, context);
		}
		if (elementValuesSize > 1) {
			out.print(" }");
		}
	}

}
