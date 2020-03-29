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

import java.util.List;

import de.carne.mcd.jvm.classfile.ClassInfo;

/**
 * A type annotation element.
 */
public class TypeAnnotation extends Annotation {

	private final TypeAnnotationTarget target;
	private final TypeAnnotationPath path;

	/**
	 * Constructs a new {@linkplain TypeAnnotation} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param typeIndex the annotation type index.
	 * @param elements the annotation elements.
	 * @param target the annotation target.
	 * @param path the annotation path.
	 */
	public TypeAnnotation(ClassInfo classInfo, int typeIndex, List<AnnotationElement> elements,
			TypeAnnotationTarget target, TypeAnnotationPath path) {
		super(classInfo, typeIndex, elements);
		this.target = target;
		this.path = path;
	}

	public boolean matchTargetType(int targetType) {
		return this.target.type() == targetType;
	}

}
