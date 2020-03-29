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
package de.carne.mcd.jvm.classfile.attribute.annotation;

import java.io.IOException;

public abstract class TypeAnnotationTarget {

	private final int targetType;

	protected TypeAnnotationTarget(int targetType) {
		this.targetType = targetType;
	}

	public int type() {
		return this.targetType;
	}

	public TypeParameter typeParameter() throws IOException {
		throw targetTypeMismatch(TypeParameter.class);
	}

	public Supertype supertype() throws IOException {
		throw targetTypeMismatch(Supertype.class);
	}

	public TypeParameterBound typeParameterBound() throws IOException {
		throw targetTypeMismatch(TypeParameterBound.class);
	}

	public FormalParameter formalParameter() throws IOException {
		throw targetTypeMismatch(FormalParameter.class);
	}

	public ThrowsType throwsType() throws IOException {
		throw targetTypeMismatch(ThrowsType.class);
	}

	public Localvar localvar() throws IOException {
		throw targetTypeMismatch(Localvar.class);
	}

	public CatchType catchType() throws IOException {
		throw targetTypeMismatch(CatchType.class);
	}

	public Offset offset() throws IOException {
		throw targetTypeMismatch(Offset.class);
	}

	public TypeArgument typeArgument() throws IOException {
		throw targetTypeMismatch(TypeArgument.class);
	}

	private IOException targetTypeMismatch(Class<? extends TypeAnnotationTarget> targetClass) {
		return new IOException(
				"Target type " + this.targetType + " does not match " + targetClass.getSimpleName() + " target info");
	}

	public static class Empty extends TypeAnnotationTarget {

		public Empty(int targetType) {
			super(targetType);
		}

	}

	public static class TypeParameter extends TypeAnnotationTarget {

		private final int parameterIndex;

		public TypeParameter(int targetType, int parameterIndex) {
			super(targetType);
			this.parameterIndex = parameterIndex;
		}

		public int parameterIndex() {
			return this.parameterIndex;
		}

		@Override
		public TypeParameter typeParameter() throws IOException {
			return this;
		}

	}

	public static class Supertype extends TypeAnnotationTarget {

		private final int supertypeIndex;

		public Supertype(int targetType, int supertypeIndex) {
			super(targetType);
			this.supertypeIndex = supertypeIndex;
		}

	}

	public static class TypeParameterBound extends TypeAnnotationTarget {

		private final int parameterIndex;
		private final int boundIndex;

		public TypeParameterBound(int targetType, int parameterIndex, int boundIndex) {
			super(targetType);
			this.parameterIndex = parameterIndex;
			this.boundIndex = boundIndex;
		}

	}

	public static class FormalParameter extends TypeAnnotationTarget {

		private final int parameterIndex;

		public FormalParameter(int targetType, int parameterIndex) {
			super(targetType);
			this.parameterIndex = parameterIndex;
		}

	}

	public static class ThrowsType extends TypeAnnotationTarget {

		private final int throwsIndex;

		public ThrowsType(int targetType, int throwsIndex) {
			super(targetType);
			this.throwsIndex = throwsIndex;
		}

	}

	public static class Localvar extends TypeAnnotationTarget {

		private final short[] table;

		public Localvar(int targetType, short[] table) {
			super(targetType);
			this.table = table;
		}

	}

	public static class CatchType extends TypeAnnotationTarget {

		private final int exceptionIndex;

		public CatchType(int targetType, int exceptionIndex) {
			super(targetType);
			this.exceptionIndex = exceptionIndex;
		}

	}

	public static class Offset extends TypeAnnotationTarget {

		private final int offset;

		public Offset(int targetType, int offset) {
			super(targetType);
			this.offset = offset;
		}

	}

	public static class TypeArgument extends TypeAnnotationTarget {

		private final int offset;
		private final int argumentIndex;

		public TypeArgument(int targetType, int offset, int argumentIndex) {
			super(targetType);
			this.offset = offset;
			this.argumentIndex = argumentIndex;
		}

	}

}
