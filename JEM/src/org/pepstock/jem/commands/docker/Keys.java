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

	public static final String JEM_ENVIRONMENT_VARIABLE = "JEM_ENVIRONMENT";
	
	public static final String JEM_DOMAIN_VARIABLE = "JEM_DOMAIN";
	
	public static final String JEM_AFFINITY_VARIABLE = "JEM_AFFINITY";
	
	public static final String JEM_DB_PORT_VARIABLE = "JEMDB_PORT";
	
	public static final String CREATE_NODE_PROPERTIES = "org/pepstock/jem/commands/docker/create_node_docker.properties";
	
	public static final String MYSQL_URL_FORMAT = "jdbc:mysql://{0}:{1}/jem";
	
	public static final String MONGO_URL_FORMAT = "mongodb://{0}:{1}/jem";

	/**
	 * To avoid any instantiation
	 */
	private Keys() {
	}
}
