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
package org.pepstock.jem.commands.docker;

/**
 * Contains all keys used to creates the JEM node inside Docker.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class Keys {

	/**
	 * Environment variable to set the JEM environment
	 */
	public static final String JEM_ENVIRONMENT_VARIABLE = "JEM_ENVIRONMENT";

	/**
	 * Environment variable to set the JEM domain
	 */
	public static final String JEM_DOMAIN_VARIABLE = "JEM_DOMAIN";
	
	/**
	 * Environment variable to set the JEM affinity, comma separated
	 */
	public static final String JEM_AFFINITY_VARIABLE = "JEM_AFFINITY";

	/**
	 * Environment variable to get the JEM database link. This is set by Docker engine
	 * using the --link parameter
	 */
	public static final String JEM_DB_PORT_VARIABLE = "JEMDB_PORT";
	
	/**
	 * Environment variable to set the JEM DB url. This is used to get userid and password.
	 * With Docker-compose it represents the URL to use
	 */
	public static final String JEM_DB_URL_VARIABLE = "JEM_DB_URL";
	
	/**
	 * The standard properties to configure a JEM node into Docker 
	 */
	public static final String CREATE_NODE_PROPERTIES = "org/pepstock/jem/commands/docker/create_node_docker.properties";
	
	/**
	 * MySql URL to connect JEM
	 */
	public static final String MYSQL_URL_FORMAT = "jdbc:mysql://{0}/jem";
	
	/**
	 * Mongo DB URL to connect JEM
	 */
	public static final String MONGO_URL_FORMAT = "mongodb://{0}/jem";

	/**
	 * To avoid any instantiation
	 */
	private Keys() {
	}
}
