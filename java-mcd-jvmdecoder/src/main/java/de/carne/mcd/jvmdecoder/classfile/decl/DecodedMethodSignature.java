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
package de.carne.mcd.jvmdecoder.classfile.decl;

import java.util.Collections;
import java.util.List;

import de.carne.mcd.jvmdecoder.classfile.PrintBuffer;
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.MethodSignatureContext;

/**
 * Method signature.
 */
public class DecodedMethodSignature extends DeclDecoder {

	private final List<PrintBuffer> typeParameters;
	private final PrintBuffer returnType;
	private final List<PrintBuffer> parameterTypes;
	private final List<PrintBuffer> throwsTypes;

	@SuppressWarnings("null")
	DecodedMethodSignature(MethodSignatureContext ctx, String classPackage) {
		this.typeParameters = Collections.unmodifiableList(decodeTypeParameters(ctx.typeParameters(), classPackage));
		this.returnType = decodeReturnType(ctx.returnType(), classPackage);
		this.parameterTypes = Collections
				.unmodifiableList(decodeJavaTypeSignatures(ctx.javaTypeSignature(), classPackage));
		this.throwsTypes = Collections.unmodifiableList(decodeThrowsSignature(ctx.throwsSignature(), classPackage));
	}

	/**
	 * Gets this method's type parameters.
	 *
	 * @return this method's type parameters.
	 */
	public List<PrintBuffer> typeParameters() {
		return this.typeParameters;
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

	/**
	 * Gets this method's exception types.
	 *
	 * @return this method's exception types.
	 */
	public List<PrintBuffer> throwsTypes() {
		return this.throwsTypes;
	}

}
