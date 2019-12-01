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

class MethodHandleConstant extends Constant {

	public static final int TAG = 15;

	private final ReferenceKind referenceKind;
	private final int referenceIndex;

	public MethodHandleConstant(ClassInfo classInfo, ReferenceKind referenceKind, int referenceIndex) {
		super(classInfo);
		this.referenceKind = referenceKind;
		this.referenceIndex = referenceIndex;
	}

	@Override
	public void print(ClassPrinter out) throws IOException {

	}

	@Override
	public String toString() {
		return this.referenceKind + " #" + this.referenceIndex;
	}

}
