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
package de.carne.mcd.jvm;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class Annotation extends ClassElement {

	private final int typeIndex;
	private final List<AnnotationElement> elements;

	public Annotation(ClassInfo classInfo, int typeIndex, List<AnnotationElement> elements) {
		super(classInfo);
		this.typeIndex = typeIndex;
		this.elements = Collections.unmodifiableList(elements);
	}

	public String typeName() throws IOException {
		return this.classInfo.resolveConstant(this.typeIndex, Utf8Constant.class).getValue();
	}

	public List<AnnotationElement> elements() {
		return this.elements;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		out.printAnnotation(typeName(), this.elements, context);
	}

}
