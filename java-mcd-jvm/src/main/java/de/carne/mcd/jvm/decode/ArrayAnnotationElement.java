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
package de.carne.mcd.jvm.decode;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.carne.mcd.jvm.ClassContext;
import de.carne.mcd.jvm.ClassInfo;
import de.carne.mcd.jvm.ClassPrinter;
import de.carne.mcd.jvm.util.PrintSeparator;

class ArrayAnnotationElement extends AnnotationElementValue {

	public static final int TAG = '[';

	private final List<AnnotationElementValue> elementValues;

	ArrayAnnotationElement(ClassInfo classInfo, List<AnnotationElementValue> elementValues) {
		super(classInfo);
		this.elementValues = Collections.unmodifiableList(elementValues);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		int elementValuesSize = this.elementValues.size();

		if (elementValuesSize > 1) {
			out.print("{ ");
		}

		PrintSeparator elementSeparator = new PrintSeparator();

		for (AnnotationElementValue elementValue : this.elementValues) {
			elementSeparator.print(out, context);
			elementValue.print(out, context);
		}
		if (elementValuesSize > 1) {
			out.print(" }");
		}
	}

}