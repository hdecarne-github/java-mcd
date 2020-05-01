/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.mcd.x86.test;

import java.nio.file.Path;

import de.carne.nio.file.FileUtil;
import de.carne.test.helper.io.RemoteTestFile;
import de.carne.test.helper.io.TestFile;

/**
 * This class provides access to external test files used for the decode tests.
 */
public final class TestFiles {

	private static final Path TEST_FILE_DIR = FileUtil.userHomeDir().resolve(".tests")
			.resolve(TestFiles.class.getPackage().getName());

	private TestFiles() {
		// Prevent instantiation
	}

	/**
	 * https://github.com/hdecarne/certmgr/releases/download/v1.1.1/certmgr_windows_1_1_1.exe
	 */
	public static final TestFile WINDOWS_EXE = new RemoteTestFile(TEST_FILE_DIR,
			"https://github.com/hdecarne/certmgr/releases/download/v1.1.1/certmgr_windows_1_1_1.exe",
			"certmgr_windows_1_1_1.exe", "a68a83bd49cd58d3f6dd83f60bbbbcc24bba9110b8f75abae49097040f63d435");

	/**
	 * https://github.com/hdecarne/certmgr/releases/download/v1.1.1/certmgr_windows-x64_1_1_1.exe
	 */
	public static final TestFile WINDOWS64_EXE = new RemoteTestFile(TEST_FILE_DIR,
			"https://github.com/hdecarne/certmgr/releases/download/v1.1.1/certmgr_windows-x64_1_1_1.exe",
			"certmgr_windows-x64_1_1_1.exe", "05807f11f31cf1825bc478c02d56c99f512656342284add7fa8a1f70145b6107");

}
