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
package de.carne.mcd.jvm.classfile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.carne.boot.check.Check;
import de.carne.mcd.jvm.classfile.attribute.Attribute;
import de.carne.mcd.jvm.classfile.constant.Utf8Constant;

class Method extends ClassInfoElement implements MethodInfo {

	private final int accessFlags;
	private final NameDescriptorIndex nameDescriptorIndex;
	private final List<Attribute> attributes;

	Method(ClassInfo classInfo, int accessFlags, int nameIndex, int descriptorIndex, List<Attribute> attributes) {
		super(classInfo);
		this.accessFlags = accessFlags;
		this.nameDescriptorIndex = new NameDescriptorIndex(nameIndex, descriptorIndex);
		this.attributes = Collections.unmodifiableList(attributes);
	}

	@Override
	public int accessFlags() {
		return this.accessFlags;
	}

	@Override
	public String descriptor() throws IOException {
		return this.classInfo.resolveConstant(this.nameDescriptorIndex.descriptorIndex(), Utf8Constant.class)
				.getValue();
	}

	@Override
	public String name() throws IOException {
		return this.classInfo.resolveConstant(this.nameDescriptorIndex.nameIndex(), Utf8Constant.class).getValue();
	}

	@Override
	public List<Attribute> attributes() {
		return this.attributes;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		// Should never be called
		Check.fail();
	}

	@Override
	public String toString() {
		return this.nameDescriptorIndex.toString();
	}

}
