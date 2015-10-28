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
package org.pepstock.jem.node.persistence.sql;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public interface SQLContainerFactory {
	
	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForCheckingQueue();
	
	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForInputQueue();

	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForRunningQueue();


	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForOutputQueue();
	
	/**
	 * @return SQLContainer
	 */ 
	SQLContainer getSQLContainerForRoutingQueue();
	
	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForRolesMap();
	
	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForCommonResourcesMap();
	
	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForRoutingConfigMap();
	
	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForUserPreferencesMap();

	/**
	 * @return SQLContainer
	 */
	SQLContainer getSQLContainerForNodesMap();

	/**
	 * @return the query to execute to maintain alive the connection with DB
	 */
	String getKeepAliveConnectionSQL();

}