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
import de.carne.mcd.jvmdecoder.classfile.ClassName;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.util.Check;

abstract class AbstractRefConstant extends Constant {

	private final int classIndex;
	private final int nameAndTypeIndex;

	AbstractRefConstant(ClassInfo classInfo, int classIndex, int nameAndTypeIndex) {
		super(classInfo);
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public String resolveSymbol() throws IOException {
		String classPackage = this.classInfo.thisClass().getPackageName();
		String className = ClassName.fromConstant(this.classInfo.resolveConstant(this.classIndex, ClassConstant.class))
				.getEffectiveName(classPackage);
		NameAndTypeConstant nameAndTypeValue = this.classInfo.resolveConstant(this.nameAndTypeIndex,
				NameAndTypeConstant.class);
		String name = nameAndTypeValue.getNameValue();
		String descriptor = nameAndTypeValue.getDescriptorValue();

		return decodeNameAndDescriptor(className, name, descriptor, classPackage);
	}

	protected abstract String decodeNameAndDescriptor(String className, String name, String descriptor,
			String classPackage);

	@Override
	public String toString() {
		return "#" + this.classIndex + ":" + this.nameAndTypeIndex;
	}

}
