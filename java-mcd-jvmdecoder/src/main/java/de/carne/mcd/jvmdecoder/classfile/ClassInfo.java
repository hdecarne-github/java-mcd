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
package de.carne.mcd.jvmdecoder.classfile;

import java.io.IOException;
import java.util.List;

import de.carne.mcd.jvmdecoder.classfile.attribute.Attribute;
import de.carne.mcd.jvmdecoder.classfile.constant.Constant;

/**
 * Class information interface (see <a href="https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html">Class File
 * Format specification</a>)
 */
public interface ClassInfo {

	/**
	 * Gets the class file's major version.
	 *
	 * @return the class file's major version.
	 */
	int majorVersion();

	/**
	 * Gets the class file's minor version.
	 *
	 * @return the class file's minor version.
	 */
	int minorVersion();

	/**
	 * Resolves a constant from the class file's constant pool.
	 *
	 * @param <T> the actual type of the constant to resolve.
	 * @param index the constant pool index to resolve.
	 * @param type the type of the constant to resolve.
	 * @return the resolved constant.
	 * @throws IOException if the constant cannot be resolved.
	 */
	<T extends Constant> T resolveConstant(int index, Class<T> type) throws IOException;

	/**
	 * Resolves a run-time constant pool index to it's corresponding symbol.
	 * 
	 * @param index the run-time constant pool index to resolve.
	 * @return the resolved symbol.
	 */
	String resolveRuntimeSymbol(int index);

	/**
	 * Gets the class' access flags.
	 *
	 * @return the class' access flags.
	 */
	int accessFlags();

	/**
	 * Gets the class' name.
	 *
	 * @return the class' name.
	 */
	ClassName thisClass();

	/**
	 * Gets the class' super class.
	 *
	 * @return the class' super class.
	 */
	ClassName superClass();

	/**
	 * Gets the class' super interfaces.
	 *
	 * @return the class' super interfaces.
	 */
	List<ClassName> interfaces();

	/**
	 * Gets the class' field informations.
	 *
	 * @return the class' field informations.
	 */
	List<FieldInfo> fields();

	/**
	 * Gets the class' method informations.
	 *
	 * @return the class' method informations.
	 */
	List<MethodInfo> methods();

	/**
	 * Gets the class' attributes.
	 *
	 * @return the class' attributes.
	 */
	List<Attribute> attributes();

}
