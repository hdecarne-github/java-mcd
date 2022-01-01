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
package de.carne.mcd.jvmdecoder.classfile.attribute.module;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import de.carne.mcd.jvmdecoder.classfile.ClassContext;
import de.carne.mcd.jvmdecoder.classfile.ClassInfo;
import de.carne.mcd.jvmdecoder.classfile.ClassInfoElement;
import de.carne.mcd.jvmdecoder.classfile.ClassPrinter;
import de.carne.mcd.jvmdecoder.classfile.constant.ClassConstant;
import de.carne.mcd.jvmdecoder.classfile.constant.ModuleConstant;
import de.carne.mcd.jvmdecoder.classfile.constant.PackageConstant;
import de.carne.mcd.jvmdecoder.classfile.constant.Utf8Constant;

/**
 * A single module-info element.
 */
public abstract class ModuleInfoElement extends ClassInfoElement {

	private static final Map<Integer, String> FLAG_COMMENT_SYMBOLS = new LinkedHashMap<>();

	static {
		FLAG_COMMENT_SYMBOLS.put(0x1000, ClassPrinter.S_SYNTHETIC);
		FLAG_COMMENT_SYMBOLS.put(0x8000, ClassPrinter.S_MANDATED);
	}

	private static final Map<Integer, String> REQUIRES_FLAG_KEYWORD_SYMBOLS = new LinkedHashMap<>();

	static {
		REQUIRES_FLAG_KEYWORD_SYMBOLS.put(0x0020, ClassPrinter.S_TRANSITIVE);
		REQUIRES_FLAG_KEYWORD_SYMBOLS.put(0x0040, ClassPrinter.S_STATIC);
	}

	ModuleInfoElement(ClassInfo classInfo) {
		super(classInfo);
	}

	/**
	 * Constructs a "requires" module-info element.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this element is part of.
	 * @param requiresIndex the required module's name index.
	 * @param requiresFlags the requires flags.
	 * @param requiresVersionIndex the required module's version index.
	 * @return the created module-info element.
	 */
	public static ModuleInfoElement requires(ClassInfo classInfo, int requiresIndex, int requiresFlags,
			int requiresVersionIndex) {
		return new ModuleInfoElement(classInfo) {

			@Override
			public void print(ClassPrinter out, ClassContext context) throws IOException {
				out.printKeyword(ClassPrinter.S_REQUIRES).print(" ");
				out.printFlagsComment(FLAG_COMMENT_SYMBOLS, requiresFlags);
				out.printFlagsKeyword(REQUIRES_FLAG_KEYWORD_SYMBOLS, requiresFlags);
				this.classInfo.resolveConstant(requiresIndex, ModuleConstant.class).print(out, context);
				if (requiresVersionIndex != 0) {
					String versionValue = this.classInfo.resolveConstant(requiresVersionIndex, Utf8Constant.class)
							.getValue();

					out.print(" ").printComment("/* ").printComment(versionValue).printComment(" */");
				}
				out.println(";");
			}

		};
	}

	/**
	 * Constructs a "exports" module-info element.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this element is part of.
	 * @param exportsIndex the exported packages's name index.
	 * @param exportsFlags the exports flags.
	 * @param exportsToIndexes the name indexes of the modules to export to.
	 * @return the created module-info element.
	 */
	public static ModuleInfoElement exports(ClassInfo classInfo, int exportsIndex, int exportsFlags,
			int[] exportsToIndexes) {
		return new ModuleInfoElement(classInfo) {

			@Override
			public void print(ClassPrinter out, ClassContext context) throws IOException {
				out.printKeyword(ClassPrinter.S_EXPORTS).print(" ");
				out.printFlagsComment(FLAG_COMMENT_SYMBOLS, exportsFlags);
				this.classInfo.resolveConstant(exportsIndex, PackageConstant.class).print(out, context);
				if (exportsToIndexes.length > 0) {
					out.print(" ").printKeyword(ClassPrinter.S_TO).println();
					out.output().increaseIndent();
					for (int exportsToIndex : exportsToIndexes) {
						this.classInfo.resolveConstant(exportsToIndex, ModuleConstant.class).print(out, context);
						if (exportsToIndex < exportsToIndexes.length) {
							out.println(",");
						}
					}
					out.output().decreaseIndent();
				}
				out.println(";");
			}

		};
	}

	/**
	 * Constructs a "opens" module-info element.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this element is part of.
	 * @param opensIndex the opened packages's name index.
	 * @param opensFlags the opens flags.
	 * @param opensToIndexes the name indexes of the modules to open to.
	 * @return the created module-info element.
	 */
	public static ModuleInfoElement opens(ClassInfo classInfo, int opensIndex, int opensFlags, int[] opensToIndexes) {
		return new ModuleInfoElement(classInfo) {

			@Override
			public void print(ClassPrinter out, ClassContext context) throws IOException {
				out.printKeyword(ClassPrinter.S_OPENS).print(" ");
				out.printFlagsComment(FLAG_COMMENT_SYMBOLS, opensFlags);
				this.classInfo.resolveConstant(opensIndex, PackageConstant.class).print(out, context);
				if (opensToIndexes.length > 0) {
					out.print(" ").printKeyword(ClassPrinter.S_TO).println();
					out.output().increaseIndent();
					for (int opensToIndex : opensToIndexes) {
						this.classInfo.resolveConstant(opensToIndex, ModuleConstant.class).print(out, context);
						if (opensToIndex < opensToIndexes.length) {
							out.println(",");
						}
					}
					out.output().decreaseIndent();
				}
				out.println(";");
			}

		};
	}

	/**
	 * Constructs a "uses" module-info element.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this element is part of.
	 * @param usesIndex the used class' name index.
	 * @return the created module-info element.
	 */
	public static ModuleInfoElement uses(ClassInfo classInfo, int usesIndex) {
		return new ModuleInfoElement(classInfo) {

			@Override
			public void print(ClassPrinter out, ClassContext context) throws IOException {
				out.printKeyword(ClassPrinter.S_USES).print(" ");
				this.classInfo.resolveConstant(usesIndex, ClassConstant.class).print(out, context);
				out.println(";");
			}

		};
	}

	/**
	 * Constructs a "provides" module-info element.
	 *
	 * @param classInfo the {@linkplain ClassInfo} instance this element is part of.
	 * @param providesIndex the provided class' name index.
	 * @param providesWithIndexes the name indexes of the providing classes.
	 * @return the created module-info element.
	 */
	public static ModuleInfoElement provides(ClassInfo classInfo, int providesIndex, int[] providesWithIndexes) {
		return new ModuleInfoElement(classInfo) {

			@Override
			public void print(ClassPrinter out, ClassContext context) throws IOException {
				out.printKeyword(ClassPrinter.S_PROVIDES).print(" ");
				this.classInfo.resolveConstant(providesIndex, ClassConstant.class).print(out, context);
				if (providesWithIndexes.length > 0) {
					out.print(" ").printKeyword(ClassPrinter.S_WITH).println();
					out.output().increaseIndent();
					for (int providesWithIndex : providesWithIndexes) {
						this.classInfo.resolveConstant(providesWithIndex, ClassConstant.class).print(out, context);
						if (providesWithIndex < providesWithIndexes.length) {
							out.println(",");
						}
					}
					out.output().decreaseIndent();
				}
				out.println(";");
			}

		};
	}

}
