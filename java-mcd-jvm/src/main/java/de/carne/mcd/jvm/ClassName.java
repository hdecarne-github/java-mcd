/*
 * Copyright (c) 2019 Holger de Carne and contributors, All Rights Reserved.
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

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.mcd.jvm.classfile.NameConstant;

/**
 * A class name.
 */
public class ClassName {

	private static final String JAVA_LANG_PACKAGE = Object.class.getPackage().getName();

	private final String fullName;
	private final String packageName;
	private final String simpleName;

	private ClassName(String fullName, String packageName, String simpleName) {
		this.fullName = fullName;
		this.packageName = packageName;
		this.simpleName = simpleName;
	}

	/**
	 * Decodes an internal or so called binary class name to it's standard form.
	 *
	 * @param internal the class name to decode.
	 * @return the decoded class name.
	 */
	public static String decode(String internal) {
		return internal.replace('/', '.');
	}

	/**
	 * Gets the effective class name by removing any unnecessary package prefix.
	 *
	 * @param name the class name to get the effective name for.
	 * @param classPackage the class package the class name is accessed in.
	 * @return the effective class name.
	 */
	public static String effectiveName(String name, String classPackage) {
		String effectiveName;

		if (isPackage(name, classPackage)) {
			effectiveName = name.substring(classPackage.length() + 1);
		} else if (isPackage(name, JAVA_LANG_PACKAGE)) {
			effectiveName = name.substring(JAVA_LANG_PACKAGE.length() + 1);
		} else {
			effectiveName = name;
		}
		return effectiveName;
	}

	private static boolean isPackage(String name, String classPackage) {
		int classPackageLength = classPackage.length();

		return name.startsWith(classPackage) && name.lastIndexOf('.') == classPackageLength;
	}

	/**
	 * Constructs a new {@linkplain ClassName} instance from an internal class name.
	 *
	 * @param internal the internal class name to create the {@linkplain ClassName} instance from.
	 * @return the {@linkplain ClassName} instance representing the submitted class name.
	 */
	public static ClassName fromInternalName(String internal) {
		String name = decode(internal);
		int baseIndex = name.lastIndexOf('.');
		String packageName;
		String simpleName;

		if (baseIndex >= 0) {
			packageName = name.substring(0, baseIndex);
			simpleName = name.substring(baseIndex + 1);
		} else {
			packageName = "";
			simpleName = name;
		}
		return new ClassName(name, packageName, simpleName);
	}

	/**
	 * Constructs a new {@linkplain ClassName} instance from a name constant.
	 *
	 * @param nameConstant the name constant to create the {@linkplain ClassName} instance from (may be {@code null} to
	 * point to class {@linkplain Object}).
	 * @return the {@linkplain ClassName} instance representing the submitted name constant.
	 * @throws IOException if the name constant cannot be resolved.
	 */
	public static ClassName fromConstant(@Nullable NameConstant nameConstant) throws IOException {
		return fromInternalName(nameConstant != null ? nameConstant.getNameValue() : Object.class.getName());
	}

	/**
	 * Checks whether this instance denotes a package info class.
	 *
	 * @return {@code true} if this instance denotes a package info class.
	 */
	public boolean isPackageInfo() {
		return this.simpleName.equals("package-info");
	}

	/**
	 * Checks whether this instance denotes a module info class.
	 *
	 * @return {@code true} if this instance denotes a module info class.
	 */
	public boolean isModuleInfo() {
		return this.simpleName.equals("module-info");
	}

	/**
	 * Checks whether this instance denotes the class {@linkplain Object}.
	 *
	 * @return {@code true} if this instance denotes the class {@linkplain Object}.
	 */
	public boolean isObject() {
		return Object.class.getName().equals(this.fullName);
	}

	/**
	 * Checks whether this instance denotes the class {@linkplain Enum}.
	 *
	 * @return {@code true} if this instance denotes the class {@linkplain Enum}.
	 */
	public boolean isEnum() {
		return Enum.class.getName().equals(this.fullName);
	}

	/**
	 * Gets the full class name.
	 *
	 * @return the full class name.
	 */
	public String getName() {
		return this.fullName;
	}

	/**
	 * Gets the effective class name by removing any unnecessary package prefix.
	 *
	 * @param classPackage the class package the class name is accessed in.
	 * @return the effective class name.
	 */
	public String getEffectiveName(String classPackage) {
		return (this.packageName.equals(classPackage) || this.packageName.equals(JAVA_LANG_PACKAGE) ? this.simpleName
				: this.fullName);
	}

	/**
	 * Gets the package name.
	 *
	 * @return the package name.
	 */
	public String getPackageName() {
		return this.packageName;
	}

	@Override
	public String toString() {
		return this.fullName;
	}

}
