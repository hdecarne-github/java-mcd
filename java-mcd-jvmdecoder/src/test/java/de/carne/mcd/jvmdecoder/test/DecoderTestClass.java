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
package de.carne.mcd.jvmdecoder.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@DecoderTestAnnotation
@DecoderTestAnnotation
final class DecoderTestClass<@NonNull T extends InputStream & Comparable<? extends Cloneable> & Serializable> {

	@SuppressWarnings({ "null", "unused" })
	private List<@Nullable Optional<? extends List<T>>> field1;

	@Nullable
	private List<@Nullable Optional<? extends List<T>>> method1(
			@SuppressWarnings("unused") @Nullable List<@Nullable Optional<? extends List<T>>> arg1) throws IOException {
		throw new IOException();
	}

	@SuppressWarnings("unused")
	private void method2() throws IOException {
		method1(null);
	}

}
