/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Andrea "Stock" Stocchero
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
package org.pepstock.jem.annotations;

/**
 * List of modes, available in JEM, to load a resource template.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class Mode {
	
	/**
	 * Constant used to set the load of resource template from classpath
	 */
	public static final String FROM_CLASSPATH = "classpath";
	
	/**
	 * Constant used to set the load of resource template from filesystem
	 */
	public static final String FROM_FILESYSTEM = "filesystem";
	
	/**
	 * Constant used to set the load of resource template from url
	 */
	public static final String FROM_URL = "url";
	
	/**
	 * to avoid any instantiation
	 */
	private Mode() {
		
	}
}
