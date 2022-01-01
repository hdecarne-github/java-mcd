/*
 * Copyright (c) 2019-2022 Holger de Carne and contributors, All Rights Reserved.
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.attribute.annotation.TypeAnnotation;
import de.carne.util.Check;

/**
 * Base class for runtime type annotation related attributes.
 */
public abstract class RuntimeTypeAnnotationsAttribute extends Attribute {

	private final List<TypeAnnotation> annotations;

	protected RuntimeTypeAnnotationsAttribute(ClassInfo classInfo, List<TypeAnnotation> annotations) {
		super(classInfo);
		this.annotations = Collections.unmodifiableList(annotations);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		// Should never be called
		Check.fail();
	}

	public List<TypeAnnotation> resolveTypeAnnotations(int targetType) {
		return this.annotations.stream().filter(annotation -> annotation.matchTargetType(targetType))
				.collect(Collectors.toList());
	}

}
