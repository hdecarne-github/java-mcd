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

import de.carne.mcd.jvm.classfile.ClassContext;
import de.carne.mcd.jvm.classfile.ClassInfo;
import de.carne.mcd.jvm.classfile.ClassPrinter;

/**
 * Annotation annotation element.
 */
public class AnnotationAnnotationElement extends AnnotationElementValue {

	/**
	 * Annotation annotation element tag.
	 */
	public static final int TAG = '@';

	private final Annotation annotation;

	/**
	 * Constructs a new {@linkplain AnnotationAnnotationElement}} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param annotation the annotation annotation element.
	 */
	public AnnotationAnnotationElement(ClassInfo classInfo, Annotation annotation) {
		super(classInfo);
		this.annotation = annotation;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		this.annotation.print(out, ClassContext.ANNOTATION);
	}

}
