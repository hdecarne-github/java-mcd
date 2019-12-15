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
import java.util.List;

import de.carne.mcd.common.MCDOutput;

class EnumClassPrinter extends ClassPrinter {

	public EnumClassPrinter(MCDOutput out, ClassInfo classInfo) {
		super(out, classInfo);
	}

	@Override
	public void print() throws IOException {
		printlnClassComment();
		printlnClassPackage();
		printlnAnnotations(this.classInfo.attributes());
		printClassAccessFLagsKeywords();
		printClassAccessFlagsComment();
		this.out.printKeyword(S_ENUM).print(" ").print(this.classInfo.thisClass().getSimpleName());

		ClassName superClass = this.classInfo.superClass();

		if (!superClass.isObject() && !superClass.isEnum()) {
			this.out.print(" ").printKeyword(S_EXTENDS).print(" ").print(superClass.getName(this.classPackage));
		}

		List<ClassName> interfaces = this.classInfo.interfaces();

		if (!interfaces.isEmpty()) {
			boolean first = true;
			for (ClassName interfaceName : interfaces) {
				if (first) {
					this.out.print(" ").printKeyword(S_IMPLEMENTS).print(" ");
					first = false;
				} else {
					this.out.print(", ");
				}
				this.out.print(interfaceName.getName(this.classPackage));
			}
		}
		this.out.println(" {");
		printlnFields();
		printlnMethods();
		this.out.println().println("}");
	}

}
