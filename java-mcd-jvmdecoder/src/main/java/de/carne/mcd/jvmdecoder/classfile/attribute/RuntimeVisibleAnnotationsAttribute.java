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
package de.carne.mcd.jvmdecoder.classfile.attribute;

import java.util.List;

import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.attribute.annotation.Annotation;

/**
 * RuntimeVisibleAnnotations attribute.
 */
public class RuntimeVisibleAnnotationsAttribute extends RuntimeAnnotationsAttribute {

	/**
	 * The RuntimeVisibleAnnotations attribute name.
	 */
	public static final String NAME = "RuntimeVisibleAnnotations";

	/**
	 * Constructs a new {@linkplain RuntimeVisibleAnnotationsAttribute} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this attribute is part of.
	 * @param annotations the attribute's annotations.
	 */
	public RuntimeVisibleAnnotationsAttribute(ClassInfo classInfo, List<Annotation> annotations) {
		super(classInfo, annotations);
	}

}
