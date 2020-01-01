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

import de.carne.mcd.jvm.ClassContext;
import de.carne.mcd.jvm.ClassInfo;
import de.carne.mcd.jvm.ClassPrinter;

/**
 * Signature attribute: "Signature"
 */
public class SignatureAttribute extends Attribute {

	/**
	 * Attribute name: "Signature"
	 */
	public static final String NAME = "Signature";

	private final int signatureIndex;

	SignatureAttribute(ClassInfo classInfo, int signatureIndex) {
		super(classInfo);
		this.signatureIndex = signatureIndex;
	}

	/**
	 * Gets the signature stored via this attribute.
	 *
	 * @return the signature stored via this attribute.
	 * @throws IOException if the attribute cannot be resolved.
	 */
	public String getValue() throws IOException {
		return this.classInfo.resolveConstant(this.signatureIndex, Utf8Constant.class).getValue();
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		out.printComment("// Signature: ").printlnComment(getValue());
	}

	@Override
	public String toString() {
		return "#" + this.signatureIndex;
	}

}
