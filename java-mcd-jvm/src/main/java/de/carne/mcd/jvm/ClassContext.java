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
package de.carne.mcd.jvm;

import java.util.Arrays;

/**
 * Enum for context depending class element processing.
 */
public enum ClassContext {

	/**
	 * Class (global) context.
	 */
	CLASS,

	/**
	 * Annotation context.
	 */
	ANNOTATION,

	/**
	 * Field context.
	 */
	FIELD,

	/**
	 * Method context.
	 */
	METHOD,

	/**
	 * (Method) parameter context.
	 */
	PARAMETER;

	/**
	 * Checks whether this context is one of the listed ones.
	 *
	 * @param contexts the contexts to consider.
	 * @return {@code true} if this context is one of the listed ones.
	 */
	public boolean isOneOf(ClassContext... contexts) {
		return Arrays.asList(contexts).indexOf(this) >= 0;
	}

}
