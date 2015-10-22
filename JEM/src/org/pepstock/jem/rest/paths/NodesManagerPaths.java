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
	 * Path parameter name to define the node key to search a node
	 */
	public static final String NODEKEY = "nodekey";
	
	/**
	 * Path parameter REST format to define the node key to search a node
	 */
	public static final String NODEKEY_PATH_PARAM = "{"+NODEKEY+"}";
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.PATH_SEPARATOR +  "nodes";

	/**
	 * Key to define the path to bind get a node
	 */
	public static final String GET = CommonPaths.PATH_SEPARATOR +  "get" + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;

	/**
	 * Key to define the path to bind get nodes list method
	 */
	public static final String LIST = CommonPaths.PATH_SEPARATOR +  "list";

	/**
	 * Key to define the path to bind get swarm nodes list method
	 */
	public static final String SWARM_LIST = CommonPaths.PATH_SEPARATOR +  "swarmList";
	
	/**
	 * Key to define the path to bind get nodes list method by filter
	 */
	public static final String LIST_BY_FILTER = CommonPaths.PATH_SEPARATOR +  "listByFilter";
	
	/**
	 * Key to define the path to bind update node method
	 */
	public static final String UPDATE = CommonPaths.PATH_SEPARATOR +  "update" + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;

	/**
	 * Key to define the path to bind start command node method
	 */
	public static final String START = CommonPaths.PATH_SEPARATOR +  "start"  + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;

	/**
	 * Key to define the path to bind drain command node method
	 */
	public static final String DRAIN = CommonPaths.PATH_SEPARATOR +  "drain"  + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;

	/**
	 * Key to define the path to bind top command node method
	 */
	public static final String TOP = CommonPaths.PATH_SEPARATOR +  "top"  + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;
	
	/**
	 * Key to define the path to bind log command node method
	 */
	public static final String LOG = CommonPaths.PATH_SEPARATOR +  "log"  + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;
	
	/**
	 * Key to define the path to bind display cluster command node method
	 */
	public static final String DISPLAY_CLUSTER = CommonPaths.PATH_SEPARATOR +  "displayCluster" + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;
	
	/**
	 * Key to define the path to bind get affinity policy file method
	 */
	public static final String GET_AFFINITY_POLICY = CommonPaths.PATH_SEPARATOR +  "getAffinityPolicy" + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;

	/**
	 * Key to define the path to bind check affinity policy file method
	 */
	public static final String CHECK_AFFINITY_POLICY = CommonPaths.PATH_SEPARATOR +  "checkAffinityPolicy" + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;

	/**
	 * Key to define the path to bind put affinity policy file method
	 */
	public static final String PUT_AFFINITY_POLICY = CommonPaths.PATH_SEPARATOR +  "putAffinityPolicy" + CommonPaths.PATH_SEPARATOR + NODEKEY_PATH_PARAM;

	/**
	 * To avoid any instantiation
	 */
	private NodesManagerPaths() {
		
	}

}
