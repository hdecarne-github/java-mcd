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

/**
 * Name and descriptor index pair used reference fields and methods.
 */
public final class NameDescriptorIndex {

	private final int nameIndex;
	private final int descriptorIndex;

	/**
	 * Constructs an new {@linkplain NameDescriptorIndex}.
	 *
	 * @param nameIndex the name index.
	 * @param descriptorIndex the descriptor index.
	 */
	public NameDescriptorIndex(int nameIndex, int descriptorIndex) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	/**
	 * Gets the name index.
	 * 
	 * @return the name index.
	 */
	public int nameIndex() {
		return this.nameIndex;
	}

	/**
	 * Gets the descriptor index.
	 * 
	 * @return the descriptor index.
	 */
	public int descriptorIndex() {
		return this.descriptorIndex;
	}

	@Override
	public String toString() {
		return "#" + this.nameIndex + ":" + this.descriptorIndex;
	}

}
