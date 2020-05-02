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
package de.carne.mcd.jvmdecoder.classfile.constant;

import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.PrintBuffer;
import de.carne.mcd.jvmdecoder.classfile.PrintSeparator;
import de.carne.mcd.jvmdecoder.classfile.decl.DeclDecoder;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedMethodDescriptor;

/**
 * InvokeDynamic constant.
 */
public class InvokeDynamicConstant extends AbstractDynamicConstant {

	/**
	 * InvokeDynamic constant tag.
	 */
	public static final int TAG = 18;

	/**
	 * Constructs a new {@linkplain InvokeDynamicConstant} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this constant is part of.
	 * @param bootstrapMethodAttrIndex bootstrap method index.
	 * @param nameAndTypeIndex the referenced name and type index.
	 */
	public InvokeDynamicConstant(ClassInfo classInfo, int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		super(classInfo, bootstrapMethodAttrIndex, nameAndTypeIndex);
	}

	@Override
	protected String decodeNameAndDescriptor(String constantName, String name, String descriptor, String classPackage) {
		DecodedMethodDescriptor method = DeclDecoder.decodeMethodDescriptor(descriptor, classPackage);
		StringBuilder buffer = new StringBuilder();

		buffer.append(method.returnType()).append(' ').append(constantName).append('.').append(name).append('(');

		PrintSeparator separator = new PrintSeparator();

		for (PrintBuffer parameter : method.parameterTypes()) {
			buffer.append(separator.next());
			buffer.append(parameter);
		}
		buffer.append(')');
		return buffer.toString();
	}

}
