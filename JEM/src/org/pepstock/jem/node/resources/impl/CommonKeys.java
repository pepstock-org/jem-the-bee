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
package org.pepstock.jem.node.resources.impl;

/**
 * Utility class with the most popular properties names
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class CommonKeys {

	/**
	 * User id property name
	 */
	public static final String USERID = "userid";
    /**
     * Password property name
     */
    public static final String PASSWORD = "password";
    /**
     * Url property name
     */
    public static final String URL = "url";

    /**
     * Url property name
     */
    public static final String RESOURCE_CUSTOM_PROPERTIES = "jem.resource.custom.properties";

    
	/**
	 * To avoid any instantiation
	 */
	private CommonKeys() {
	}
    
}
