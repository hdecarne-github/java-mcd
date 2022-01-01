/*
 * Copyright (c) 2019-2022 Holger de Carne and contributors, All Rights Reserved.
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

final class Rex {

	public static final Rex NOT_PRESENT = new Rex(-1);

	public static final Rex REX = new Rex(0x40);
	public static final Rex REX_B = new Rex(0x41);
	public static final Rex REX_X = new Rex(0x42);
	public static final Rex REX_XB = new Rex(0x43);
	public static final Rex REX_R = new Rex(0x44);
	public static final Rex REX_RB = new Rex(0x45);
	public static final Rex REX_RX = new Rex(0x46);
	public static final Rex REX_RXB = new Rex(0x47);
	public static final Rex REX_W = new Rex(0x48);
	public static final Rex REX_WB = new Rex(0x49);
	public static final Rex REX_WX = new Rex(0x4a);
	public static final Rex REX_WXB = new Rex(0x4b);
	public static final Rex REX_WR = new Rex(0x4c);
	public static final Rex REX_WRB = new Rex(0x4d);
	public static final Rex REX_WRX = new Rex(0x4e);
	public static final Rex REX_WRXB = new Rex(0x4f);

	private final int value;

	private Rex(int value) {
		this.value = value;
	}

	public boolean isPresent() {
		return (this.value & 0b111110000) == 0b001000000;
	}

	public boolean isB() {
		return (this.value & 0b111110001) == 0b001000001;
	}

	public boolean isX() {
		return (this.value & 0b111110010) == 0b001000010;
	}

	public boolean isR() {
		return (this.value & 0b111110100) == 0b001000100;
	}

	public boolean isW() {
		return (this.value & 0b111111000) == 0b001001000;
	}

}
