/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.x86decoder;

final class ModRM {

	public static final ModRM NOT_PRESENT = new ModRM(-1);

	private final int value;

	ModRM(int value) {
		this.value = value;
	}

	public int regOrOpcodeIndex() {
		return (this.value >> 3) & 0b111;
	}

	public int modRMIndex() {
		return (this.value >> 3 & 0b11000) | (this.value & 0b00111);
	}

	public int rmIndex() {
		return (this.value & 0b00111);
	}

}
