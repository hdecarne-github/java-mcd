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
package de.carne.mcd.jvm.decode;

import java.io.IOException;

import de.carne.mcd.jvm.ClassInfo;

/**
 * Base class for all kinds of name constants.
 */
public abstract class AbstractNameConstant extends Constant {

	private final int nameIndex;

	AbstractNameConstant(ClassInfo classInfo, int nameIndex) {
		super(classInfo);
		this.nameIndex = nameIndex;
	}

	/**
	 * Resolves this name constant's value.
	 *
	 * @return this name constant's value.
	 * @throws IOException if the constant cannot be resolved.
	 */
	public String getNameValue() throws IOException {
		return getName().getValue();
	}

	/**
	 * Resolves this name constant.
	 *
	 * @return this name constant.
	 * @throws IOException if the constant cannot be resolved.
	 */
	public Utf8Constant getName() throws IOException {
		return this.classInfo.resolveConstant(this.nameIndex, Utf8Constant.class);
	}

	@Override
	public String toString() {
		return "#" + this.nameIndex;
	}

}
