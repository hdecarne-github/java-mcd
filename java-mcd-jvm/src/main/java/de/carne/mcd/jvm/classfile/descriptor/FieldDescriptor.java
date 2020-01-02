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
package de.carne.mcd.jvm.classfile.descriptor;

import java.io.IOException;
import java.util.Objects;

import de.carne.mcd.jvm.classfile.ClassContext;
import de.carne.mcd.jvm.classfile.ClassPrinter;
import de.carne.mcd.jvm.classfile.Printable;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser.FieldDescriptorContext;

/**
 * Single field or type descriptor.
 */
public class FieldDescriptor extends Descriptor implements Printable {

	private final FieldTypeDescriptor fieldType;

	FieldDescriptor(FieldDescriptorContext ctx, String classPackage) {
		this.fieldType = new FieldTypeDescriptor(Objects.requireNonNull(ctx.fieldType()), classPackage);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		this.fieldType.print(out, context);
	}

}
