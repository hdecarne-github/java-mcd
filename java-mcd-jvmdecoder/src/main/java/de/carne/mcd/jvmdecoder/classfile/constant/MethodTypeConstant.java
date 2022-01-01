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
package de.carne.mcd.jvmdecoder.classfile.constant;

import java.io.IOException;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.PrintBuffer;
import de.carne.mcd.jvmdecoder.classfile.PrintSeparator;
import de.carne.mcd.jvmdecoder.classfile.decl.DeclDecoder;
import de.carne.mcd.jvmdecoder.classfile.decl.DecodedMethodDescriptor;
import de.carne.util.Check;

/**
 * MethodType constant.
 */
public class MethodTypeConstant extends Constant {

	/**
	 * MethodType constant tag.
	 */
	public static final int TAG = 16;

	private final int descriptorIndex;

	/**
	 * Constructs a new {@linkplain MethodTypeConstant} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this constant is part of.
	 * @param descriptorIndex the method descriptor index.
	 */
	public MethodTypeConstant(ClassInfo classInfo, int descriptorIndex) {
		super(classInfo);
		this.descriptorIndex = descriptorIndex;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public String resolveSymbol() throws IOException {
		String descriptor = this.classInfo.resolveConstant(this.descriptorIndex, Utf8Constant.class).getValue();
		DecodedMethodDescriptor method = DeclDecoder.decodeMethodDescriptor(descriptor,
				this.classInfo.thisClass().getPackageName());
		StringBuilder buffer = new StringBuilder();

		buffer.append(method.returnType()).append(' ').append("::").append('(');

		PrintSeparator separator = new PrintSeparator();

		for (PrintBuffer parameter : method.parameterTypes()) {
			buffer.append(separator.next());
			buffer.append(parameter);
		}
		buffer.append(')');
		return buffer.toString();
	}

	@Override
	public String toString() {
		return "#" + this.descriptorIndex;
	}

}
