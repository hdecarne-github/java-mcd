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
import de.carne.mcd.jvmdecoder.classfile.PrintBuffer;
import de.carne.mcd.jvmdecoder.classfile.PrintSeparator;
import de.carne.mcd.jvmdecoder.classfile.decl.DeclDecoder;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedMethodDescriptor;

/**
 * InterfaceMethodRef constant.
 */
public class InterfaceMethodRefConstant extends AbstractRefConstant {

	/**
	 * InterfaceMethodRef constant tag.
	 */
	public static final int TAG = 11;

	/**
	 * Constructs a new {@linkplain InterfaceMethodRefConstant} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this constant is part of.
	 * @param classIndex the class constant name index.
	 * @param nameAndTypeIndex the referenced name and type index.
	 */
	public InterfaceMethodRefConstant(ClassInfo classInfo, int classIndex, int nameAndTypeIndex) {
		super(classInfo, classIndex, nameAndTypeIndex);
	}

	@Override
	protected String decodeNameAndDescriptor(String className, String name, String descriptor, String classPackage) {
		DecodedMethodDescriptor method = DeclDecoder.decodeMethodDescriptor(descriptor, classPackage);
		StringBuilder buffer = new StringBuilder();

		buffer.append(method.returnType()).append(' ').append(className).append('.').append(name).append('(');

		PrintSeparator separator = new PrintSeparator();

		for (PrintBuffer parameter : method.parameterTypes()) {
			buffer.append(separator.next());
			buffer.append(parameter);
		}
		buffer.append(')');
		return buffer.toString();
	}

}
