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
package de.carne.mcd.jvmdecoder.classfile.attribute;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.attribute.module.ModuleInfoElement;
import de.carne.mcd.jvmdecoder.classfile.constant.ModuleConstant;
import de.carne.mcd.jvmdecoder.classfile.constant.Utf8Constant;

/**
 * Module attribute.
 */
public class ModuleAttribute extends Attribute {

	/**
	 * The Module attribute name.
	 */
	public static final String NAME = "Module";

	private static final Map<Integer, String> MODULE_FLAG_SYMBOLS = new LinkedHashMap<>();

	static {
		MODULE_FLAG_SYMBOLS.put(0x0020, ClassPrinter.S_OPEN);
		MODULE_FLAG_SYMBOLS.put(0x1000, ClassPrinter.S_SYNTHETIC);
		MODULE_FLAG_SYMBOLS.put(0x8000, ClassPrinter.S_MANDATED);
	}

	private final int moduleNameIndex;
	private final int moduleFlags;
	private final int moduleVersionIndex;
	private List<ModuleInfoElement> elements;

	/**
	 * Constructs a new {@linkplain ModuleAttribute} instance.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this attribute is part of.
	 * @param moduleNameIndex module name index.
	 * @param moduleFlags module flags.
	 * @param moduleVersionIndex module version index.
	 * @param elements the module-info elements.
	 */
	public ModuleAttribute(ClassInfo classInfo, int moduleNameIndex, int moduleFlags, int moduleVersionIndex,
			List<ModuleInfoElement> elements) {
		super(classInfo);
		this.moduleNameIndex = moduleNameIndex;
		this.moduleFlags = moduleFlags;
		this.moduleVersionIndex = moduleVersionIndex;
		this.elements = Collections.unmodifiableList(elements);
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		out.printKeyword(ClassPrinter.S_MODULE).print(" ");
		out.printFlagsComment(MODULE_FLAG_SYMBOLS, this.moduleFlags);
		this.classInfo.resolveConstant(this.moduleNameIndex, ModuleConstant.class).print(out, context);
		if (this.moduleVersionIndex != 0) {
			String versionValue = this.classInfo.resolveConstant(this.moduleVersionIndex, Utf8Constant.class)
					.getValue();

			out.print(" ").printComment("/* ").printComment(versionValue).printComment(" */");
		}
		out.println(" {");
		out.output().increaseIndent();
		for (ModuleInfoElement element : this.elements) {
			element.print(out, context);
		}
		out.output().decreaseIndent();
		out.println("}");
	}

}
