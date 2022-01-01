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
package de.carne.mcd.jvmdecoder.classfile.attribute.annotation;

import java.io.IOException;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.constant.Utf8Constant;
import de.carne.mcd.jvmdecoder.classfile.decl.DeclDecoder;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedFieldDescriptor;

/**
 * Enum annotation element.
 */
public class EnumAnnotationElement extends AnnotationElementValue {

	/**
	 * Enum annotation element tag.
	 */
	public static final int TAG = 'e';

	private final int nameIndex;
	private final int valueIndex;

	/**
	 * Constructs a new {@linkplain EnumAnnotationElement}} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this annotation attribute is part of.
	 * @param nameIndex the annotation element name index.
	 * @param valueIndex the annotation element value index.
	 */
	public EnumAnnotationElement(ClassInfo classInfo, int nameIndex, int valueIndex) {
		super(classInfo);
		this.nameIndex = nameIndex;
		this.valueIndex = valueIndex;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		DecodedFieldDescriptor name = DeclDecoder.decodeFieldDescriptor(
				this.classInfo.resolveConstant(this.nameIndex, Utf8Constant.class).getValue(),
				this.classInfo.thisClass().getPackageName());
		String value = this.classInfo.resolveConstant(this.valueIndex, Utf8Constant.class).getValue();

		name.type().print(out, context);
		out.print(".").printValue(value);
	}

}
