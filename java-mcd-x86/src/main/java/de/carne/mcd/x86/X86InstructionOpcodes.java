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
package de.carne.mcd.x86;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.mcd.instruction.InstructionOpcode;

/**
 * Specific X86 opcodes.
 */
public final class X86InstructionOpcodes {

	private static final InstructionOpcode PREFIX_LOCK = InstructionOpcode.wrap(new byte[] { (byte) 0xf0 });
	private static final InstructionOpcode PREFIX_REPNX = InstructionOpcode.wrap(new byte[] { (byte) 0xf2 });
	private static final InstructionOpcode PREFIX_REPX = InstructionOpcode.wrap(new byte[] { (byte) 0xf3 });
	private static final InstructionOpcode PREFIX_CS = InstructionOpcode.wrap(new byte[] { (byte) 0x2e });
	private static final InstructionOpcode PREFIX_SS = InstructionOpcode.wrap(new byte[] { (byte) 0x36 });
	private static final InstructionOpcode PREFIX_DS = InstructionOpcode.wrap(new byte[] { (byte) 0x3e });
	private static final InstructionOpcode PREFIX_ES = InstructionOpcode.wrap(new byte[] { (byte) 0x26 });
	private static final InstructionOpcode PREFIX_FS = InstructionOpcode.wrap(new byte[] { (byte) 0x64 });
	private static final InstructionOpcode PREFIX_GS = InstructionOpcode.wrap(new byte[] { (byte) 0x65 });
	private static final InstructionOpcode PREFIX_OSO = InstructionOpcode.wrap(new byte[] { (byte) 0x66 });
	private static final InstructionOpcode PREFIX_ASO = InstructionOpcode.wrap(new byte[] { (byte) 0x67 });
	private static final InstructionOpcode PREFIX_REX = InstructionOpcode.wrap(new byte[] { (byte) 0x40 });
	private static final InstructionOpcode PREFIX_REX_B = InstructionOpcode.wrap(new byte[] { (byte) 0x41 });
	private static final InstructionOpcode PREFIX_REX_X = InstructionOpcode.wrap(new byte[] { (byte) 0x42 });
	private static final InstructionOpcode PREFIX_REX_XB = InstructionOpcode.wrap(new byte[] { (byte) 0x43 });
	private static final InstructionOpcode PREFIX_REX_R = InstructionOpcode.wrap(new byte[] { (byte) 0x44 });
	private static final InstructionOpcode PREFIX_REX_RB = InstructionOpcode.wrap(new byte[] { (byte) 0x45 });
	private static final InstructionOpcode PREFIX_REX_RX = InstructionOpcode.wrap(new byte[] { (byte) 0x46 });
	private static final InstructionOpcode PREFIX_REX_RXB = InstructionOpcode.wrap(new byte[] { (byte) 0x47 });
	private static final InstructionOpcode PREFIX_REX_W = InstructionOpcode.wrap(new byte[] { (byte) 0x48 });
	private static final InstructionOpcode PREFIX_REX_WB = InstructionOpcode.wrap(new byte[] { (byte) 0x49 });
	private static final InstructionOpcode PREFIX_REX_WX = InstructionOpcode.wrap(new byte[] { (byte) 0x4a });
	private static final InstructionOpcode PREFIX_REX_WXB = InstructionOpcode.wrap(new byte[] { (byte) 0x4b });
	private static final InstructionOpcode PREFIX_REX_WR = InstructionOpcode.wrap(new byte[] { (byte) 0x4c });
	private static final InstructionOpcode PREFIX_REX_WRB = InstructionOpcode.wrap(new byte[] { (byte) 0x4d });
	private static final InstructionOpcode PREFIX_REX_WRX = InstructionOpcode.wrap(new byte[] { (byte) 0x4e });
	private static final InstructionOpcode PREFIX_REX_WRXB = InstructionOpcode.wrap(new byte[] { (byte) 0x4f });

	private static final Map<InstructionOpcode, PrefixDecoder> PREFIX_DECODE_MAP = new HashMap<>();

	static {
		PREFIX_DECODE_MAP.put(PREFIX_LOCK, PrefixDecoder.LOCK);
		PREFIX_DECODE_MAP.put(PREFIX_REPNX, PrefixDecoder.REPNX);
		PREFIX_DECODE_MAP.put(PREFIX_REPX, PrefixDecoder.REPX);
		PREFIX_DECODE_MAP.put(PREFIX_CS, PrefixDecoder.CS);
		PREFIX_DECODE_MAP.put(PREFIX_SS, PrefixDecoder.SS);
		PREFIX_DECODE_MAP.put(PREFIX_DS, PrefixDecoder.DS);
		PREFIX_DECODE_MAP.put(PREFIX_ES, PrefixDecoder.ES);
		PREFIX_DECODE_MAP.put(PREFIX_FS, PrefixDecoder.FS);
		PREFIX_DECODE_MAP.put(PREFIX_GS, PrefixDecoder.GS);
		PREFIX_DECODE_MAP.put(PREFIX_OSO, PrefixDecoder.OSO);
		PREFIX_DECODE_MAP.put(PREFIX_ASO, PrefixDecoder.ASO);
		PREFIX_DECODE_MAP.put(PREFIX_REX, PrefixDecoder.REX);
		PREFIX_DECODE_MAP.put(PREFIX_REX_B, PrefixDecoder.REX_B);
		PREFIX_DECODE_MAP.put(PREFIX_REX_X, PrefixDecoder.REX_X);
		PREFIX_DECODE_MAP.put(PREFIX_REX_XB, PrefixDecoder.REX_XB);
		PREFIX_DECODE_MAP.put(PREFIX_REX_R, PrefixDecoder.REX_R);
		PREFIX_DECODE_MAP.put(PREFIX_REX_RB, PrefixDecoder.REX_RB);
		PREFIX_DECODE_MAP.put(PREFIX_REX_RX, PrefixDecoder.REX_RX);
		PREFIX_DECODE_MAP.put(PREFIX_REX_RXB, PrefixDecoder.REX_RXB);
		PREFIX_DECODE_MAP.put(PREFIX_REX_W, PrefixDecoder.REX_W);
		PREFIX_DECODE_MAP.put(PREFIX_REX_WB, PrefixDecoder.REX_WB);
		PREFIX_DECODE_MAP.put(PREFIX_REX_WX, PrefixDecoder.REX_WX);
		PREFIX_DECODE_MAP.put(PREFIX_REX_WXB, PrefixDecoder.REX_WXB);
		PREFIX_DECODE_MAP.put(PREFIX_REX_WR, PrefixDecoder.REX_WR);
		PREFIX_DECODE_MAP.put(PREFIX_REX_WRB, PrefixDecoder.REX_WRB);
		PREFIX_DECODE_MAP.put(PREFIX_REX_WRX, PrefixDecoder.REX_WRX);
		PREFIX_DECODE_MAP.put(PREFIX_REX_WRXB, PrefixDecoder.REX_WRXB);
	}

	private X86InstructionOpcodes() {
		// Prevent instantiation
	}

	/**
	 * Checks whether the given opcode is a prefix opcode.
	 *
	 * @param opcode the opcode to check.
	 * @return {@code true} if the given opcode is a prefix opcode.
	 */
	public static boolean isPrefix(InstructionOpcode opcode) {
		return PREFIX_DECODE_MAP.containsKey(opcode);
	}

	/**
	 * Gets the {@linkplain PrefixDecoder} instance matching the given opcode.
	 *
	 * @param opcode the opcode to get the decoder for.
	 * @return the {@linkplain PrefixDecoder} instance matching the given opcode or {@code null} if the opcode is not a
	 * prefix opcode.
	 */
	public static @Nullable PrefixDecoder getPrefixDecoder(InstructionOpcode opcode) {
		return PREFIX_DECODE_MAP.get(opcode);
	}

}
