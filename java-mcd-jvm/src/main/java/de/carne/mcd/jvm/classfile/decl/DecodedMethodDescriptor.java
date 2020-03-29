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
package de.carne.mcd.jvm.classfile.decl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.carne.mcd.jvm.classfile.PrintBuffer;
import de.carne.mcd.jvm.classfile.decl.grammar.DeclParser.MethodDescriptorContext;
import de.carne.mcd.jvm.classfile.decl.grammar.DeclParser.ParameterDescriptorContext;

/**
 * Method descriptor.
 */
public class DecodedMethodDescriptor extends DeclDecoder {

	private final PrintBuffer returnType;
	private final List<PrintBuffer> parameterTypes;

	DecodedMethodDescriptor(MethodDescriptorContext ctx, String classPackage) {
		this.returnType = decodeDescriptorType(Objects.requireNonNull(ctx.returnDescriptor().descriptorType()),
				classPackage);

		@SuppressWarnings("null") List<ParameterDescriptorContext> parameterCtxs = ctx.parameterDescriptor();

		if (parameterCtxs != null) {
			List<PrintBuffer> parameterTypes0 = new ArrayList<>(parameterCtxs.size());

			for (ParameterDescriptorContext parameterCtx : parameterCtxs) {
				parameterTypes0
						.add(decodeDescriptorType(Objects.requireNonNull(parameterCtx.descriptorType()), classPackage));
			}
			this.parameterTypes = Collections.unmodifiableList(parameterTypes0);
		} else {
			this.parameterTypes = Collections.emptyList();
		}
	}

	/**
	 * Gets this method's return type.
	 *
	 * @return this method's return type.
	 */
	public PrintBuffer returnType() {
		return this.returnType;
	}

	/**
	 * Gets this method's parameter types.
	 *
	 * @return this method's parameter types.
	 */
	public List<PrintBuffer> parameterTypes() {
		return this.parameterTypes;
	}

}
