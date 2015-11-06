/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.rest.paths;

/**
 * Contains all labels for all services to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class CommonPaths {
	
	/**
	 * Query parameter name for filters
	 */
	public static final String FILTER_QUERY_STRING = "filter";

	/**
	 * Default value of filter parmater if missing
	 */
	public static final String DEFAULT_FILTER = "*";

	/**
	 * String to define a query string separator
	 */
	public static final String PATH_SEPARATOR = "/";
	
	/**
	 * false value for default
	 */
	public static final String FALSE = "false";
	
	/**
	 * To avoid any instantiation
	 */
	private CommonPaths() {
		
	}

}
