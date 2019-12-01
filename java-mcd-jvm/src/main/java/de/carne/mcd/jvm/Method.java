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
import java.util.Collections;
import java.util.List;

class Method extends ClassElement {

	private final int accessFlags;
	private final NameDescriptorIndex nameDescriptorIndex;
	private final List<Attribute> attributes;

	public Method(ClassInfo classInfo, int accessFlags, int nameIndex, int descriptorIndex,
			List<Attribute> attributes) {
		super(classInfo);
		this.accessFlags = accessFlags;
		this.nameDescriptorIndex = new NameDescriptorIndex(nameIndex, descriptorIndex);
		this.attributes = Collections.unmodifiableList(attributes);
	}

	@Override
	public void print(ClassPrinter out) throws IOException {
		String descriptor = this.classInfo
				.resolveConstant(this.nameDescriptorIndex.descriptorIndex(), Utf8Constant.class).getValue();
		String name = this.classInfo.resolveConstant(this.nameDescriptorIndex.nameIndex(), Utf8Constant.class)
				.getValue();

		out.printlnMethod(this.accessFlags, descriptor, name, this.attributes);
	}

	@Override
	public String toString() {
		return this.nameDescriptorIndex.toString();
	}

}
