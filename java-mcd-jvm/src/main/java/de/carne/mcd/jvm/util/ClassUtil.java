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
package de.carne.mcd.jvm.util;

import de.carne.mcd.jvm.ClassInfo;
import de.carne.mcd.jvm.ClassName;

/**
 * Utility class providing class information related functions.
 */
public final class ClassUtil {

	private ClassUtil() {
		// Prevent instantiation
	}

	/**
	 * Determines whether the given class is public.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is public.
	 */
	public static boolean isPublic(ClassInfo classInfo) {
		return isPublic(classInfo.accessFlags());
	}

	/**
	 * Determines whether the given access flags have the public flag set.
	 *
	 * @param accessFlags the access flags to examine.
	 * @return {@code true} if the public flag is set.
	 */
	public static boolean isPublic(int accessFlags) {
		return (accessFlags & 0x0001) == 0x0001;
	}

	/**
	 * Determines whether the given class is private.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is private.
	 */
	public static boolean isPrivate(ClassInfo classInfo) {
		return isPrivate(classInfo.accessFlags());
	}

	/**
	 * Determines whether the given access flags have the private flag set.
	 *
	 * @param accessFlags the access flags to examine.
	 * @return {@code true} if the public flag is set.
	 */
	public static boolean isPrivate(int accessFlags) {
		return (accessFlags & 0x0002) == 0x0002;
	}

	/**
	 * Determines whether the given class is final.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is final.
	 */
	public static boolean isFinal(ClassInfo classInfo) {
		return isFinal(classInfo.accessFlags());
	}

	/**
	 * Determines whether the given access flags have the final flag set.
	 *
	 * @param accessFlags the access flags to examine.
	 * @return {@code true} if the final flag is set.
	 */
	public static boolean isFinal(int accessFlags) {
		return (accessFlags & 0x0010) == 0x0010;
	}

	/**
	 * Determines whether the given class is an interface.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is an interface.
	 */
	public static boolean isInterface(ClassInfo classInfo) {
		return isInterface(classInfo.accessFlags());
	}

	/**
	 * Determines whether the given access flags indicate an interface class.
	 *
	 * @param accessFlags the access flags to examine.
	 * @return {@code true} if the interface class flags are set.
	 */
	public static boolean isInterface(int accessFlags) {
		return (accessFlags & (0x0200 | 0x2000 | 0x8000)) == 0x0200;
	}

	/**
	 * Determines whether the given class is abstract.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is abstract.
	 */
	public static boolean isAbstract(ClassInfo classInfo) {
		return isAbstract(classInfo.accessFlags());
	}

	/**
	 * Determines whether the given access flags indicate an abstract class.
	 *
	 * @param accessFlags the access flags to examine.
	 * @return {@code true} if the abstract class flags are set.
	 */
	public static boolean isAbstract(int accessFlags) {
		return (accessFlags & (0x0200 | 0x0400)) == 0x0400;
	}

	/**
	 * Determines whether the given class is an annotation.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is an annotation.
	 */
	public static boolean isAnnotation(ClassInfo classInfo) {
		return isAnnotation(classInfo.accessFlags());
	}

	/**
	 * Determines whether the given access flags indicate an annotation class.
	 *
	 * @param accessFlags the access flags to examine.
	 * @return {@code true} if the annotation class flags are set.
	 */
	public static boolean isAnnotation(int accessFlags) {
		return (accessFlags & 0x2000) == 0x2000;
	}

	/**
	 * Determines whether the given class is an enum.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is an enum.
	 */
	public static boolean isEnum(ClassInfo classInfo) {
		return isEnum(classInfo.accessFlags());
	}

	/**
	 * Determines whether the given access flags indicate an enum class.
	 *
	 * @param accessFlags the access flags to examine.
	 * @return {@code true} if the enum class flags are set.
	 */
	public static boolean isEnum(int accessFlags) {
		return (accessFlags & 0x4000) == 0x4000;
	}

	/**
	 * Determines whether the given class is a package-info.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is a package-info.
	 */
	public static boolean isPackageInfo(ClassInfo classInfo) {
		return isPackageInfo(classInfo.thisClass());
	}

	/**
	 * Determines whether the given class name indicates a package-info class.
	 *
	 * @param className the class name to examine.
	 * @return {@code true} if the class name indicates a package-info class.
	 */
	public static boolean isPackageInfo(ClassName className) {
		return className.isPackageInfo();
	}

	/**
	 * Determines whether the given class is a module-info.
	 *
	 * @param classInfo the class info to examine.
	 * @return {@code true} if the class is a module-info.
	 */
	public static boolean isModuleInfo(ClassInfo classInfo) {
		return isModuleInfo(classInfo.thisClass());
	}

	/**
	 * Determines whether the given class name indicates a module-info class.
	 *
	 * @param className the class name to examine.
	 * @return {@code true} if the class name indicates a module-info class.
	 */
	public static boolean isModuleInfo(ClassName className) {
		return className.isModuleInfo();
	}

}
