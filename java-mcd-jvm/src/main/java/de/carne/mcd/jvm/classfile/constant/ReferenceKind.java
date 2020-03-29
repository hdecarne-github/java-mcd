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
package de.carne.mcd.jvm.classfile.constant;

@SuppressWarnings("squid:S00115")
public enum ReferenceKind {

	REF_getField(1),

	REF_getStatic(2),

	REF_putField(3),

	REF_putStatic(4),

	REF_invokeVirtual(5),

	REF_invokeStatic(6),

	REF_invokeSpecial(7),

	REF_newInvokeSpecial(8),

	REF_invokeInterface(9);

	private final int value;
	private final String symbol;

	private ReferenceKind(int value) {
		this.value = value;
		this.symbol = name().substring(4);
	}

	public int value() {
		return this.value;
	}

	public String symbol() {
		return this.symbol;
	}

}
