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

class ClassName {

	private final String fullName;
	private final String simpleName;

	private ClassName(String fullName, String simpleName) {
		this.fullName = fullName;
		this.simpleName = simpleName;
	}

	private static ClassName fromName(String name) {
		String externalName = name.replace('/', '.');
		int baseIndex = externalName.lastIndexOf('.');
		String simpleName = (baseIndex >= 0 ? externalName.substring(baseIndex + 1) : "");

		return new ClassName(externalName, simpleName);
	}

	public static ClassName fromConstant(@Nullable AbstractNameConstant nameConstant) throws IOException {
		return fromName(nameConstant != null ? nameConstant.getNameValue() : Object.class.getName());
	}

	public static ClassName fromDescriptor(String descriptor, int position) throws IOException {
		int nextPosition = descriptor.indexOf(';', position);

		if (nextPosition < 0) {
			throw new IOException("Unexpected class name descriptor: " + descriptor + "(" + position + ")");
		}
		return fromName(descriptor.substring(position, nextPosition));
	}

	public boolean isPackageInfo() {
		return "package-info".equals(getSimpleName());
	}

	public boolean isModuleInfo() {
		return "module-info".equals(getSimpleName());
	}

	public boolean isObject() {
		return Object.class.getName().equals(this.fullName);
	}

	public boolean isEnum() {
		return Enum.class.getName().equals(this.fullName);
	}

	public String getName() {
		return this.fullName;
	}

	public String getName(String packageName) {
		return (isPackage(packageName) || isPackage("java.lang") ? this.simpleName : this.fullName);
	}

	private boolean isPackage(String packageName) {
		return this.fullName.startsWith(packageName)
				&& packageName.length() + 1 + this.simpleName.length() == this.fullName.length();
	}

	public String getSimpleName() {
		return this.simpleName;
	}

	public String getPackage() {
		int baseIndex = this.fullName.lastIndexOf('.');

		return (baseIndex >= 0 ? this.fullName.substring(0, baseIndex) : "");
	}

}
