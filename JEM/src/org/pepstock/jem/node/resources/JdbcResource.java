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
package org.pepstock.jem.node.resources;




/**
 * Contains all information necessary to create a datasource job by JNDI.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * @see org.apache.commons.dbcp.BasicDataSourceFactory
 */
@SuppressWarnings("javadoc")
public class JdbcResource extends Resource {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_AUTOCOMMIT = "defaultAutoCommit";
	public static final String DEFAULT_READONLY = "defaultReadOnly";
	public static final String DEFAULT_TRANSACTION_ISOLATION = "defaultTransactionIsolation";
	public static final String DRIVER_CLASS_NAME = "driverClassName";
	public static final String URL = "url";

	
    private static final String PROP_MAXACTIVE = "maxActive";
    private static final String PROP_MAXIDLE = "maxIdle";
    private static final String PROP_MINIDLE = "minIdle";
    private static final String PROP_INITIALSIZE = "initialSize";
	
	/**
	 * Type for JDBC data sources
	 */
	public static final String TYPE = "jdbc";

	/**
	 *  * 
	 * Constructs the object adding user name, password, driver and URL as unchangeable properties.
	 */
	public JdbcResource() {
		setType(TYPE);
		
		// avoid to have a pool on batch
		getProperties().put(PROP_INITIALSIZE, createResourceProperty(PROP_INITIALSIZE, "1"));
		getProperties().put(PROP_MAXACTIVE, createResourceProperty(PROP_MAXACTIVE, "1"));
		getProperties().put(PROP_MAXIDLE, createResourceProperty(PROP_MAXIDLE, "1"));
		getProperties().put(PROP_MINIDLE, createResourceProperty(PROP_MINIDLE, "1"));
	}
	
	private ResourceProperty createResourceProperty(String key, String value){
		ResourceProperty rp = new ResourceProperty();
		rp.setName(key);
		rp.setValue(value);
		return rp;
	}

}