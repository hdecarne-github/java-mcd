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
package de.carne.mcd.jvm.classfile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.carne.mcd.jvm.ClassContext;
import de.carne.mcd.jvm.ClassInfo;
import de.carne.mcd.jvm.ClassPrinter;

/**
 * Base class for all kinds of runtime visible annotations attributes.
 */
public abstract class RuntimeAnnotationsAttribute extends Attribute {

	private final List<Annotation> annotations;

	RuntimeAnnotationsAttribute(ClassInfo classInfo, List<Annotation> annotations) {
		super(classInfo);
		this.annotations = Collections.unmodifiableList(annotations);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		for (Annotation annotation : this.annotations) {
			annotation.print(out, context);
		}
	}

}
