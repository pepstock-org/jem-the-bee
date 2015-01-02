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
package org.pepstock.jem.node.resources.impl.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.node.resources.impl.CommonKeys;


/**
 * Contains all information necessary to create a REST conn by JNDI.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * @see org.apache.commons.dbcp.BasicDataSourceFactory
 */
public final class RestResourceKeys {
	
    static final String HTTP_BASIC_AUTHENTICATION = "basicAuthentication";
    
	/**
	 * List of mandatory properties
	 */
	public static final List<String> PROPERTIES_MANDATORY = Collections.unmodifiableList(Arrays.asList(
			CommonKeys.URL
	));
	
	/**
	 * List of all configuration properties.
	 */
	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(
			CommonKeys.URL,
			CommonKeys.USERID,
			CommonKeys.PASSWORD,
			HTTP_BASIC_AUTHENTICATION
	));
	/**
	 * To avoid any instantiation
	 */
	private RestResourceKeys() {
	}
}