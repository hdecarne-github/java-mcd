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
package de.carne.mcd.jvmdecoder.classfile.attribute;

import java.io.IOException;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.constant.Utf8Constant;
import de.carne.util.Check;

/**
 * Signature attribute.
 */
public class SignatureAttribute extends Attribute {

	/**
	 * The Signature attribute name.
	 */
	public static final String NAME = "Signature";

	private final int signatureIndex;

	/**
	 * Constructs a new {@linkplain SignatureAttribute} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this attribute is part of.
	 * @param signatureIndex the signature index.
	 */
	public SignatureAttribute(ClassInfo classInfo, int signatureIndex) {
		super(classInfo);
		this.signatureIndex = signatureIndex;
	}

	/**
	 * Gets the signature string stored via this attribute.
	 *
	 * @return the signature string stored via this attribute.
	 * @throws IOException if the attribute cannot be resolved.
	 */
	public String getValue() throws IOException {
		return this.classInfo.resolveConstant(this.signatureIndex, Utf8Constant.class).getValue();
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public String toString() {
		return "#" + this.signatureIndex;
	}

}
