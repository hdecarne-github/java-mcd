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
package de.carne.mcd.jvm;

import java.io.IOException;

class StringConstant extends Constant {

	public static final int TAG = 8;

	private int stringIndex;

	public StringConstant(ClassInfo classInfo, int stringIndex) {
		super(classInfo);
		this.stringIndex = stringIndex;
	}

	public String getStringValue() throws IOException {
		return getString().getValue();
	}

	public Utf8Constant getString() throws IOException {
		return this.classInfo.resolveConstant(this.stringIndex, Utf8Constant.class);
	}

	@Override
	public void print(ClassPrinter out) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public String toString() {
		return "#" + this.stringIndex;
	}

}
