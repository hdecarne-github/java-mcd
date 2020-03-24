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

import de.carne.boot.check.Check;

class MethodHandleConstant extends Constant {

	public static final int TAG = 15;

	private final ReferenceKind referenceKind;
	private final int referenceIndex;

	MethodHandleConstant(ClassInfo classInfo, ReferenceKind referenceKind, int referenceIndex) {
		super(classInfo);
		this.referenceKind = referenceKind;
		this.referenceIndex = referenceIndex;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public String resolveSymbol() throws IOException {
		MethodRefConstant reference = this.classInfo.resolveConstant(this.referenceIndex, MethodRefConstant.class);

		return this.referenceKind.symbol() + " " + reference.resolveSymbol();
	}

	@Override
	public String toString() {
		return this.referenceKind.symbol() + " #" + this.referenceIndex;
	}

}
