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
package de.carne.mcd.jvm.classfile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.carne.mcd.jvm.classfile.descriptor.Descriptor;
import de.carne.mcd.jvm.classfile.descriptor.FieldDescriptor;
import de.carne.mcd.jvm.util.PrintSeparator;

/**
 * Annotation
 */
public class Annotation extends ClassElement {

	private final int typeIndex;
	private final List<AnnotationElement> elements;

	Annotation(ClassInfo classInfo, int typeIndex, List<AnnotationElement> elements) {
		super(classInfo);
		this.typeIndex = typeIndex;
		this.elements = Collections.unmodifiableList(elements);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		String typeName = this.classInfo.resolveConstant(this.typeIndex, Utf8Constant.class).getValue();
		FieldDescriptor annotationType = Descriptor.decodeFieldDescriptor(typeName,
				this.classInfo.thisClass().getPackageName());

		out.printLabel("@");
		annotationType.print(out, ClassContext.ANNOTATION);

		int elementsSize = this.elements.size();

		if (elementsSize > 0) {
			out.print("(");

			PrintSeparator elementSeparator = new PrintSeparator();

			for (AnnotationElement element : this.elements) {
				elementSeparator.print(out, context);
				element.print(out, context);
			}
			out.print(")");
		}
		if (context.isOneOf(ClassContext.CLASS, ClassContext.METHOD)) {
			out.println();
		} else {
			out.print(" ");
		}
	}

}
