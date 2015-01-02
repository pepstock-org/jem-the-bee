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
 * Contains all labels for nodes service to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class NodesManagerPaths {
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.QUERYSTRING_SEPARATOR +  "nodes";

	/**
	 * Key to define the path to bind get nodes list method
	 */
	public static final String LIST = CommonPaths.QUERYSTRING_SEPARATOR +  "list";

	/**
	 * Key to define the path to bind get swarm nodes list method
	 */
	public static final String SWARM_LIST = CommonPaths.QUERYSTRING_SEPARATOR +  "swarmList";
	
	/**
	 * Key to define the path to bind get nodes list method by filter
	 */
	public static final String LIST_BY_FILTER = CommonPaths.QUERYSTRING_SEPARATOR +  "listByFilter";
	
	/**
	 * Key to define the path to bind update node method
	 */
	public static final String UPDATE = CommonPaths.QUERYSTRING_SEPARATOR +  "update";
	
	/**
	 * Key to define the path to bind top command node method
	 */
	public static final String TOP = CommonPaths.QUERYSTRING_SEPARATOR +  "top";
	
	/**
	 * Key to define the path to bind log command node method
	 */
	public static final String LOG = CommonPaths.QUERYSTRING_SEPARATOR +  "log";
	
	/**
	 * Key to define the path to bind top command node method
	 */
	public static final String DISPLAY_CLUSTER = CommonPaths.QUERYSTRING_SEPARATOR +  "displayCluster";
	
	/**
	 * Key to define the path to bind get node config file method
	 */
	public static final String GET_NODE_CONFIG_FILE = CommonPaths.QUERYSTRING_SEPARATOR +  "getNodeConfigFile";

	/**
	 * Key to define the path to bind save node config file method
	 */
	public static final String SAVE_NODE_CONFIG_FILE = CommonPaths.QUERYSTRING_SEPARATOR +  "saveNodeConfigFile";

	/**
	 * Key to define the path to bind get environment config file method
	 */
	public static final String GET_ENV_CONFIG_FILE = CommonPaths.QUERYSTRING_SEPARATOR +  "getEnvConfigFile";
	
	/**
	 * Key to define the path to bind save environment config file method
	 */
	public static final String SAVE_ENV_CONFIG_FILE = CommonPaths.QUERYSTRING_SEPARATOR +  "saveEnvConfigFile";
	
	/**
	 * Key to define the path to bind save environment config file method
	 */
	public static final String CHECK_CONFIG_FILE = CommonPaths.QUERYSTRING_SEPARATOR +  "checkConfigFile";
	
	/**
	 * Key to define the path to bind save environment config file method
	 */
	public static final String CHECK_AFFINITY_POLICY = CommonPaths.QUERYSTRING_SEPARATOR +  "checkAffinityPolicy";

	/**
	 * To avoid any instantiation
	 */
	private NodesManagerPaths() {
		
	}

}
