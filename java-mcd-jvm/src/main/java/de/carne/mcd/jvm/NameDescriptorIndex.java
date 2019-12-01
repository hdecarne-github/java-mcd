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

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

final class NameDescriptorIndex {

	private final int nameIndex;
	private final int descriptorIndex;

	public NameDescriptorIndex(int nameIndex, int descriptorIndex) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public int nameIndex() {
		return this.nameIndex;
	}

	public int descriptorIndex() {
		return this.descriptorIndex;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.nameIndex, this.descriptorIndex);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return this == obj || (obj instanceof NameDescriptorIndex && equalsHelper((NameDescriptorIndex) obj));
	}

	private boolean equalsHelper(NameDescriptorIndex o) {
		return this.nameIndex == o.nameIndex && this.descriptorIndex == o.descriptorIndex;
	}

	@Override
	public String toString() {
		return "#" + this.nameIndex + ":" + this.descriptorIndex;
	}

}
