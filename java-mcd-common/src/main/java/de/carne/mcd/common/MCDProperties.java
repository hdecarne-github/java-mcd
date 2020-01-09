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
package de.carne.mcd.common;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Interface used to access decode properties.
 */
public interface MCDProperties {

	/**
	 * Gets a property.
	 *
	 * @param key the key of the property to get.
	 * @return the property value or {@code null} if the property is not set.
	 */
	@Nullable
	String getProperty(String key);

	/**
	 * Gets a property.
	 *
	 * @param key the key of the property to get.
	 * @param defaultValue the default value to return in case the property is not set.
	 * @return the property value or the given default value if the property is not set.
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * Sets a property.
	 * 
	 * @param key the key of the property to set.
	 * @param value the value to set.
	 */
	void setProperty(String key, String value);

}
