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
package de.carne.mcd.jvmdecoder.classfile;

import java.io.IOException;
import java.util.List;

import de.carne.mcd.jvmdecoder.classfile.attribute.Attribute;

/**
 * Class method information interface (see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html">Class File Format specification</a>)
 */
public interface MethodInfo {

	/**
	 * Gets the method's access flags.
	 *
	 * @return the method's access flags.
	 */
	int accessFlags();

	/**
	 * Gets the method's type descriptor.
	 *
	 * @return the method's type descriptor.
	 * @throws IOException if the descriptor cannot be resolved.
	 */
	String descriptor() throws IOException;

	/**
	 * Gets the method's name.
	 *
	 * @return the method's name.
	 * @throws IOException if the name cannot be resolved.
	 */
	String name() throws IOException;

	/**
	 * Gets the method's attributes.
	 *
	 * @return the method's attributes.
	 */
	List<Attribute> attributes();

}
