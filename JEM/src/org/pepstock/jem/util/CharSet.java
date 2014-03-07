/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.util;

import java.nio.charset.Charset;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public final class CharSet {
	
	/**
	 * Is the default char set use by JEM to wirte files
	 */
	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
	
	
	/**
	 * Load UTF-8 char set. All text file are written by UTF-8 
	 */
	public static final Charset DEFAULT = Charset.forName(DEFAULT_CHARSET_NAME);

	/**
	 * To avoid any instantiation
	 */
	private CharSet() {
	}

}
