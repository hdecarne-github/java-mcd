/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.jvmdecoder.classfile;

import java.io.IOException;
import java.util.List;

import de.carne.mcd.jvmdecoder.classfile.attribute.Attribute;

/**
 * Class field information interface (see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html">Class File Format specification</a>)
 */
public interface FieldInfo {

	/**
	 * Gets the field's access flags.
	 *
	 * @return the field's access flags.
	 */
	int accessFlags();

	/**
	 * Gets the field's type descriptor.
	 *
	 * @return the field's type descriptor.
	 * @throws IOException if the descriptor cannot be resolved.
	 */
	String descriptor() throws IOException;

	/**
	 * Gets the field's name.
	 *
	 * @return the field's name.
	 * @throws IOException if the name cannot be resolved.
	 */
	String name() throws IOException;

	/**
	 * Gets the field's attributes.
	 *
	 * @return the field's attributes.
	 */
	List<Attribute> attributes();

}
