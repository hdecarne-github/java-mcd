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
import de.carne.mcd.jvm.ClassContext;
import de.carne.mcd.jvm.ClassInfo;
import de.carne.mcd.jvm.ClassPrinter;

/**
 * Source file attribute: "SourceFile"
 */
public class SourceFileAttribute extends Attribute {

	/**
	 * Attribute name: "SourceFile"
	 */
	public static final String NAME = "SourceFile";

	private final int sourceFileIndex;

	SourceFileAttribute(ClassInfo classInfo, int sourceFileIndex) {
		super(classInfo);
		this.sourceFileIndex = sourceFileIndex;
	}

	/**
	 * Gets the source file name stored via this attribute.
	 *
	 * @return the source file name stored via this attribute.
	 * @throws IOException if the attribute cannot be resolved.
	 */
	public String getValue() throws IOException {
		return this.classInfo.resolveConstant(this.sourceFileIndex, Utf8Constant.class).getValue();
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public String toString() {
		return "#" + this.sourceFileIndex;
	}

}
