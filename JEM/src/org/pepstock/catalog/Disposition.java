/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.catalog;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains all possible dispositions.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public final class Disposition implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant that represents a sharing access
	 */
	public static final String SHR = "SHR";

	/**
	 * Constant that represents a exclusive access
	 */
	public static final String OLD = "OLD";

	/**
	 * Constant that represents a exclusive access for new data description
	 */
	public static final String NEW = "NEW";

	/**
	 * Constant that represents a exclusive access for updating an existing data
	 * description
	 */
	public static final String MOD = "MOD";

	/**
	 * Array with all disposition
	 */
	public static final List<String> VALUES = Collections.unmodifiableList(Arrays.asList(SHR, OLD, NEW, MOD));

	/**
	 * To avoid any instantiation
	 */
	private Disposition() {
	}

}