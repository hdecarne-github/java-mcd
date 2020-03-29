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
import de.carne.mcd.jvm.classfile.constant.Utf8Constant;
import de.carne.mcd.jvm.classfile.decl.DeclDecoder;
import de.carne.mcd.jvm.classfile.decl.DecodedFieldDescriptor;

public class ClassAnnotationElement extends AnnotationElementValue {

	public static final int TAG = 'c';

	private final int descriptorIndex;

	public ClassAnnotationElement(ClassInfo classInfo, int descriptorIndex) {
		super(classInfo);
		this.descriptorIndex = descriptorIndex;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		DecodedFieldDescriptor descriptor = DeclDecoder.decodeFieldDescriptor(
				this.classInfo.resolveConstant(this.descriptorIndex, Utf8Constant.class).getValue(),
				this.classInfo.thisClass().getPackageName());

		descriptor.type().print(out, context);
		out.print(".").printKeyword(ClassPrinter.S_CLASS);
	}

}
