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
package de.carne.mcd.jvm.classfile;

import java.io.IOException;

import de.carne.util.Strings;

class Utf8Constant extends Constant {

	public static final byte TAG = 1;

	private final String value;

	Utf8Constant(ClassInfo classInfo, String value) {
		super(classInfo);
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		out.printValue(toString());
	}

	@Override
	public String toString() {
		return "\"" + Strings.encode(this.value) + "\"";
	}

}
