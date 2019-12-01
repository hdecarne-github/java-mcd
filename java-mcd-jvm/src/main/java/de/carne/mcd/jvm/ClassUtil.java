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

final class ClassUtil {

	private ClassUtil() {
		// Prevent instantiation
	}

	public static boolean isPublic(ClassInfo classInfo) {
		return isPublic(classInfo.accessFlags());
	}

	public static boolean isPublic(int accessFlags) {
		return (accessFlags & 0x0001) == 0x0001;
	}

	public static boolean isPrivate(ClassInfo classInfo) {
		return isPrivate(classInfo.accessFlags());
	}

	public static boolean isPrivate(int accessFlags) {
		return (accessFlags & 0x0002) == 0x0002;
	}

	public static boolean isFinal(ClassInfo classInfo) {
		return isFinal(classInfo.accessFlags());
	}

	public static boolean isFinal(int accessFlags) {
		return (accessFlags & 0x0010) == 0x0010;
	}

	public static boolean isInterface(ClassInfo classInfo) {
		return isInterface(classInfo.accessFlags());
	}

	public static boolean isInterface(int accessFlags) {
		return (accessFlags & (0x0200 | 0x2000 | 0x8000)) == 0x0200;
	}

	public static boolean isAbstract(ClassInfo classInfo) {
		return isAbstract(classInfo.accessFlags());
	}

	public static boolean isAbstract(int accessFlags) {
		return (accessFlags & (0x0200 | 0x0400)) == 0x0400;
	}

	public static boolean isAnnotation(ClassInfo classInfo) {
		return isAnnotation(classInfo.accessFlags());
	}

	public static boolean isAnnotation(int accessFlags) {
		return (accessFlags & 0x2000) == 0x2000;
	}

	public static boolean isEnum(ClassInfo classInfo) {
		return isEnum(classInfo.accessFlags());
	}

	public static boolean isEnum(int accessFlags) {
		return (accessFlags & 0x4000) == 0x4000;
	}

	public static boolean isModule(ClassInfo classInfo) {
		return isModule(classInfo.accessFlags());
	}

	public static boolean isModule(int accessFlags) {
		return (accessFlags & 0x8000) == 0x8000;
	}

	public static boolean isPackageInfo(ClassInfo classInfo) {
		return isPackageInfo(classInfo.thisClass());
	}

	public static boolean isPackageInfo(ClassName className) {
		return className.isPackageInfo();
	}

	public static boolean isModuleInfo(ClassInfo classInfo) {
		return isModuleInfo(classInfo.thisClass());
	}

	public static boolean isModuleInfo(ClassName className) {
		return className.isModuleInfo();
	}

}
