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
package org.pepstock.jem.node.resources.impl.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.node.resources.impl.CommonKeys;


/**
 * Contains all information necessary to create a datasource job by JNDI.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * @see org.apache.commons.dbcp.BasicDataSourceFactory
 */
@SuppressWarnings("javadoc")
public final class JdbcResourceKeys {
	
	public static final String DEFAULT_AUTOCOMMIT = "defaultAutoCommit";
	public static final String DEFAULT_READONLY = "defaultReadOnly";
	public static final String DEFAULT_TRANSACTION_ISOLATION = "defaultTransactionIsolation";
	public static final String DRIVER_CLASS_NAME = "driverClassName";

	
	static final String PROP_CONNECTIONPROPERTIES = "connectionProperties";
	static final String PROP_USERNAME = "username";
	
    static final String PROP_MAXACTIVE = "maxActive";
    static final String PROP_MAXIDLE = "maxIdle";
    static final String PROP_MINIDLE = "minIdle";
    static final String PROP_INITIALSIZE = "initialSize";
    
	/**
	 * List of mandatory properties
	 */
	public static final List<String> PROPERTIES_MANDATORY = Collections.unmodifiableList(Arrays.asList(
			CommonKeys.URL,
			CommonKeys.USERID,
			CommonKeys.PASSWORD,
			DRIVER_CLASS_NAME
	));
	
	/**
	 * List of all configuration properties.
	 */
	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(
			CommonKeys.URL,
			CommonKeys.USERID,
			CommonKeys.PASSWORD,
			DRIVER_CLASS_NAME, 
			DEFAULT_AUTOCOMMIT,
			DEFAULT_READONLY,
			DEFAULT_TRANSACTION_ISOLATION,
		    PROP_MAXACTIVE,
		    PROP_MAXIDLE,
		    PROP_MINIDLE,
		    PROP_INITIALSIZE
	));
	/**
	 * To avoid any instantiation
	 */
	private JdbcResourceKeys() {
	}
}