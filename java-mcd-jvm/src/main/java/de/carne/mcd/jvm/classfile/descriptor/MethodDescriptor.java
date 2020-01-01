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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser.MethodDescriptorContext;
import de.carne.mcd.jvm.classfile.descriptor.grammar.DescriptorGrammarParser.ParameterDescriptorContext;

/**
 * Method descriptor.
 */
public class MethodDescriptor extends Descriptor {

	private final FieldTypeDescriptor returnType;
	private final List<FieldTypeDescriptor> parameterTypes;

	MethodDescriptor(MethodDescriptorContext ctx, String classPackage) {
		this.returnType = new FieldTypeDescriptor(Objects.requireNonNull(ctx.returnDescriptor().fieldType()),
				classPackage);

		@SuppressWarnings("null") List<ParameterDescriptorContext> parameterCtxs = ctx.parameterDescriptor();

		if (parameterCtxs != null) {
			List<FieldTypeDescriptor> parameterTypes0 = new ArrayList<>(parameterCtxs.size());

			for (ParameterDescriptorContext parameterCtx : parameterCtxs) {
				parameterTypes0
						.add(new FieldTypeDescriptor(Objects.requireNonNull(parameterCtx.fieldType()), classPackage));
			}
			this.parameterTypes = Collections.unmodifiableList(parameterTypes0);
		} else {
			this.parameterTypes = Collections.emptyList();
		}
	}

	/**
	 * Gets the method's return type.
	 *
	 * @return the method's return type.
	 */
	public FieldTypeDescriptor getReturnType() {
		return this.returnType;
	}

	/**
	 * Gets the method's parameter types.
	 *
	 * @return the method's parameter types.
	 */
	public List<FieldTypeDescriptor> getParameterTypes() {
		return this.parameterTypes;
	}

}
