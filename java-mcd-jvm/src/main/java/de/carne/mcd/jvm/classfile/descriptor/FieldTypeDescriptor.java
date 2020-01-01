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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.carne.boot.check.Check;
import de.carne.mcd.jvm.ClassContext;
import de.carne.mcd.jvm.ClassName;
import de.carne.mcd.jvm.ClassPrinter;
import de.carne.mcd.jvm.Printable;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser.ArrayTypeContext;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser.FieldTypeContext;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser.IntegralTypeContext;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser.ObjectTypeContext;

/**
 * Field type descriptor.
 */
public class FieldTypeDescriptor extends Descriptor implements Printable {

	private final Printable[] elements;

	FieldTypeDescriptor(FieldTypeContext ctx, String classPackage) {
		this.elements = getElements(ctx, classPackage);
	}

	private static Printable[] getElements(FieldTypeContext ctx, String classPackage) {
		List<Printable> elements = new ArrayList<>();

		collectElements(elements, ctx, classPackage);
		return elements.toArray(new Printable[elements.size()]);
	}

	private static void collectElements(List<Printable> elements, FieldTypeContext ctx, String classPackage) {
		IntegralTypeContext integralTypeCtx;
		ObjectTypeContext objectTypeCtx;
		ArrayTypeContext arrayTypeCtx;

		if ((integralTypeCtx = ctx.integralType()) != null) {
			String integralType = getIntegralType(Objects.requireNonNull(integralTypeCtx.getText()));

			elements.add((out, context) -> out.printKeyword(integralType));
		} else if ((objectTypeCtx = ctx.objectType()) != null) {
			String objectType = ClassName.effectiveName(objectTypeCtx.identifier().getText().replace('/', '.'),
					classPackage);

			elements.add((out, context) -> {
				if (context == ClassContext.ANNOTATION) {
					out.printLabel(objectType);
				} else {
					out.print(objectType);
				}
			});
		} else if ((arrayTypeCtx = ctx.arrayType()) != null) {
			collectElements(elements, Objects.requireNonNull(arrayTypeCtx.componentType().fieldType()), classPackage);
			elements.add((out, context) -> out.print("[]"));
		} else {
			// Should never happen
			Check.fail();
		}
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		for (Printable element : this.elements) {
			element.print(out, context);
		}
	}

}
