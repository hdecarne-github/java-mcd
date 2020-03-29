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
package de.carne.mcd.jvm.classfile.attribute.annotation;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.carne.mcd.jvm.classfile.ClassContext;
import de.carne.mcd.jvm.classfile.ClassInfo;
import de.carne.mcd.jvm.classfile.ClassInfoElement;
import de.carne.mcd.jvm.classfile.ClassPrinter;
import de.carne.mcd.jvm.classfile.PrintSeparator;
import de.carne.mcd.jvm.classfile.constant.Utf8Constant;
import de.carne.mcd.jvm.classfile.decl.DeclDecoder;
import de.carne.mcd.jvm.classfile.decl.DecodedFieldDescriptor;

/**
 * A single annotation.
 */
public class Annotation extends ClassInfoElement {

	private final int typeIndex;
	private final List<AnnotationElement> elements;

	/**
	 * Constructs a new {@linkplain Annotation} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param typeIndex the annotation type index.
	 * @param elements the annotation elements.
	 */
	public Annotation(ClassInfo classInfo, int typeIndex, List<AnnotationElement> elements) {
		super(classInfo);
		this.typeIndex = typeIndex;
		this.elements = Collections.unmodifiableList(elements);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		String typeName = this.classInfo.resolveConstant(this.typeIndex, Utf8Constant.class).getValue();
		DecodedFieldDescriptor annotationType = DeclDecoder.decodeFieldDescriptor(typeName,
				this.classInfo.thisClass().getPackageName());

		out.printLabel("@");
		annotationType.type().print(out, ClassContext.ANNOTATION);

		int elementsSize = this.elements.size();

		if (elementsSize > 0) {
			out.print("(");

			PrintSeparator separator = new PrintSeparator();

			for (AnnotationElement element : this.elements) {
				separator.print(out, context);
				element.print(out, context);
			}
			out.print(")");
		}
		if (context.isOneOf(ClassContext.CLASS, ClassContext.METHOD)) {
			out.println();
		} else if (context != ClassContext.ANNOTATION) {
			out.print(" ");
		}
	}

}
