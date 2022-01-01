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
import de.carne.mcd.jvmdecoder.classfile.decl.grammar.DeclParser.ClassSignatureContext;

/**
 * Class signature.
 */
public class DecodedClassSignature extends DeclDecoder {

	private final List<PrintBuffer> typeParameters;
	private final PrintBuffer superClass;
	private final List<PrintBuffer> superInterfaces;

	@SuppressWarnings("null")
	DecodedClassSignature(ClassSignatureContext ctx, String classPackage) {
		this.typeParameters = Collections.unmodifiableList(decodeTypeParameters(ctx.typeParameters(), classPackage));
		this.superClass = decodeSuperClassSignature(ctx.superClassSignature(), classPackage);
		this.superInterfaces = Collections
				.unmodifiableList(decodeSuperInterfaceSignatures(ctx.superInterfaceSignature(), classPackage));
	}

	/**
	 * Gets this class' type parameters.
	 *
	 * @return this class' type parameters.
	 */
	public List<PrintBuffer> typeParameters() {
		return this.typeParameters;
	}

	/**
	 * Gets this class' super class.
	 *
	 * @return this class' super class (empty in case of {@linkplain Object}).
	 */
	public PrintBuffer superClass() {
		return this.superClass;
	}

	/**
	 * Gets this class' super interfaces.
	 *
	 * @return this class' super interfaces.
	 */
	public List<PrintBuffer> superInterfaces() {
		return this.superInterfaces;
	}

}
