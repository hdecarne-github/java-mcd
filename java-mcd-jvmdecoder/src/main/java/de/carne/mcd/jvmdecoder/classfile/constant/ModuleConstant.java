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

/**
 * Module constant.
 */
public class ModuleConstant extends NameConstant {

	/**
	 * Module constant tag.
	 */
	public static final byte TAG = 19;

	/**
	 * Constructs a new {@linkplain ModuleConstant} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this constant is part of.
	 * @param nameIndex the module constant name index.
	 */
	public ModuleConstant(ClassInfo classInfo, int nameIndex) {
		super(classInfo, nameIndex);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		out.print(resolveSymbol());
	}

}
