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

final class PrintSeparator implements Printable {

	private boolean first = true;
	private final Printer printer;
	private final String separator;

	public PrintSeparator() {
		this(ClassPrinter::print, ", ");
	}

	public PrintSeparator(Printer printer, String separator) {
		this.printer = printer;
		this.separator = separator;
	}

	@Override
	public void print(ClassPrinter out, ClassContext context) throws IOException {
		if (!this.first) {
			this.printer.print(out, this.separator);
		} else {
			this.first = false;
		}
	}

}
