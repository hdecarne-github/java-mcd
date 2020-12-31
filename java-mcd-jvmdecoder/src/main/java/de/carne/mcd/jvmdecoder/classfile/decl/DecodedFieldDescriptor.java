/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.jvmdecoder.classfile.decl;

import java.util.Objects;

import de.carne.mcd.jvmdecoder.classfile.PrintBuffer;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.FieldDescriptorContext;

/**
 * Field descriptor.
 */
public class DecodedFieldDescriptor extends DeclDecoder {

	private final PrintBuffer type;

	DecodedFieldDescriptor(FieldDescriptorContext ctx, String classPackage) {
		this.type = decodeDescriptorType(Objects.requireNonNull(ctx.descriptorType()), classPackage);
	}

	/**
	 * Gets this field's type.
	 *
	 * @return this field's type.
	 */
	public PrintBuffer type() {
		return this.type;
	}

}
