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
package de.carne.mcd.jvmdecoder.classfile.constant;

import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.decl.DeclDecoder;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedFieldDescriptor;

/**
 * Dynamic constant.
 */
public class DynamicConstant extends AbstractDynamicConstant {

	/**
	 * Dynamic constant tag.
	 */
	public static final int TAG = 17;

	/**
	 * Constructs a new {@linkplain DynamicConstant} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this constant is part of.
	 * @param bootstrapMethodAttrIndex bootstrap method index.
	 * @param nameAndTypeIndex the referenced name and time index.
	 */
	public DynamicConstant(ClassInfo classInfo, int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		super(classInfo, bootstrapMethodAttrIndex, nameAndTypeIndex);
	}

	@Override
	protected String decodeNameAndDescriptor(String constantName, String name, String descriptor, String classPackage) {
		DecodedFieldDescriptor field = DeclDecoder.decodeFieldDescriptor(descriptor, classPackage);
		StringBuilder buffer = new StringBuilder();

		buffer.append(field.type()).append(' ').append(constantName).append('.').append(name);
		return buffer.toString();
	}

}
