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

import org.pepstock.jem.node.persistence.SQLContainer;

/**
 * Contains all Oracle SQL for Hazelcast persistence
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class OracleSQLContainerFactory extends DefaultSQLContainerFactory {
	/**
	 * is the type of the database in this case mysql
	 */
	public static final String DATABASE_TYPE = "oracle";
	
	private static final String TEST_CONNECTION_SQL_QUERY = "SELECT 1 FROM DUAL";
	
	/**
	 * Create table for checking table
	 */
	private static final String CREATE_CHECKING_QUEUE = "create table CHECKING_QUEUE (JOB_ID char(39) primary key, PRE_JOB CLOB not null)";

	/*------------------------------------
	| I N P U T                          |
	------------------------------------*/

	/**
	 * Create table for input table
	 */
	private static final String CREATE_INPUT_QUEUE = "create table INPUT_QUEUE (JOB_ID char(39) primary key, JOB CLOB not null)";
	
	
	/*------------------------------------
	| R U N N I N G                      |
	------------------------------------*/

	/**
	 * Create table for input table
	 */
	private static final String CREATE_RUNNING_QUEUE = "create table RUNNING_QUEUE (JOB_ID char(39) primary key, JOB CLOB not null)";
	
	/*------------------------------------
	| O U T P U T                        |
	------------------------------------*/

	/**
	 * Create table for output table
	 */
	private static final String CREATE_OUTPUT_QUEUE = "create table OUTPUT_QUEUE (JOB_ID char(39) primary key, JOB CLOB not null)";
	

	/*------------------------------------
	| R O U T I N G                      |
	------------------------------------*/
	
	/**
	 * Create table for routing table
	 */
	private static final String CREATE_ROUTING_QUEUE = "create table ROUTING_QUEUE (JOB_ID char(39) primary key, JOB CLOB not null)";

	/*------------------------------------
	| Common R E S O U R C E S           |
	------------------------------------*/
	
	/**
	 * Create table for common resources table
	 */
	private static final String CREATE_COMMON_RESOURCES_MAP = "create table COMMON_RESOURCES_MAP (RESOURCE_NAME varchar2(32) primary key, RESOURCE_OBJ CLOB not null)";
	
	/**
	 * Select statement to get a resource from COMMON RESOURCES map
	 */
	private static final String GET_COMMON_RESOURCE_MAP = "select RESOURCE_OBJ from COMMON_RESOURCES_MAP where RESOURCE_NAME = ?";

	/**
	 * Select statement to get all resources (using a list of names) from COMMON
	 * RESOURCES_map map. is not a PrepareStatement but a message format to feed
	 * with a list of resources names.
	 */
	private static final String GET_ALL_COMMON_RESOURCES_MAP = "select RESOURCE_OBJ from COMMON_RESOURCES_MAP where RESOURCE_NAME IN ( {0} )";


	/**
	 * Get size of resources inside the COMMON_RESOURCES_MAP
	 */
	private static final String GET_RESOURCES_SIZE = "select SUM(LENGTH(RESOURCE_OBJ)) from COMMON_RESOURCES_MAP";

	/*------------------------------------
	| R O L E S                          |
	------------------------------------*/
	/**
	 * Create table for ROLEs table
	 */
	private static final String CREATE_ROLES_MAP = "create table ROLES_MAP (ROLE_NAME varchar2(32) primary key, ROLE CLOB not null)";
	/*------------------------------------
	| N O D E S                          |
	------------------------------------*/
	
	/**
	 * Create table for NODEs table
	 */
	private static final String CREATE_NODES_MAP = "create table NODES_MAP (NODE_KEY varchar2(40) primary key, NODE CLOB not null)";
	/*------------------------------------
	| R O U T I N G   C O N F            |
	------------------------------------*/
	/**
	 * Create table for ROUTING_CONFIGs table
	 */
	private static final String CREATE_ROUTING_CONFIG_MAP = "create table ROUTING_CONFIG_MAP (ROUTING_CONFIG_NAME varchar2(32) primary key, ROUTING_CONFIG CLOB not null)";
	/*------------------------------------
	| U S E R    P R E F E R E N C E S   |
	------------------------------------*/
	/**
	 * Create table for USER_PREFERENCESs table
	 */
	private static final String CREATE_USER_PREFERENCES_MAP = "create table USER_PREFERENCES_MAP (USER_ID varchar2(32) primary key, USER_PREFERENCES CLOB not null)";

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getTestConnectionSQL()
	 */
	@Override
	public String getKeepAliveConnectionSQL() {
		return TEST_CONNECTION_SQL_QUERY;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForInputQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForInputQueue() {
		SQLContainer container = super.getSQLContainerForInputQueue();
		container.setCreateTableStatement(CREATE_INPUT_QUEUE);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForRunningQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForRunningQueue() {
		SQLContainer container = super.getSQLContainerForRunningQueue();
		container.setCreateTableStatement(CREATE_RUNNING_QUEUE);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForOutputQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForOutputQueue() {
		SQLContainer container = super.getSQLContainerForOutputQueue();
		container.setCreateTableStatement(CREATE_OUTPUT_QUEUE);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForRoutingQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForRoutingQueue() {
		SQLContainer container = super.getSQLContainerForRoutingQueue();
		container.setCreateTableStatement(CREATE_ROUTING_QUEUE);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForRolesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForNodesMap() {
		SQLContainer container = super.getSQLContainerForNodesMap();
		container.setCreateTableStatement(CREATE_NODES_MAP);
		return container;		
	}

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForRolesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForRolesMap() {
		SQLContainer container = super.getSQLContainerForRolesMap();
		container.setCreateTableStatement(CREATE_ROLES_MAP);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForCommonResourcesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForCommonResourcesMap() {
		SQLContainer container = super.getSQLContainerForCommonResourcesMap();
		container.setCreateTableStatement(CREATE_COMMON_RESOURCES_MAP);
		container.setCheckQueueSizeStatement(GET_RESOURCES_SIZE);
		container.setGetAllStatement(GET_ALL_COMMON_RESOURCES_MAP);
		container.setGetStatement(GET_COMMON_RESOURCE_MAP);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForCheckingQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForCheckingQueue() {
		SQLContainer container = super.getSQLContainerForCheckingQueue();
		container.setCreateTableStatement(CREATE_CHECKING_QUEUE);
		return container;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.StandardDatabaseEngine#getSQLContainerForRolesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForRoutingConfigMap() {
		SQLContainer container = super.getSQLContainerForRoutingConfigMap();
		container.setCreateTableStatement(CREATE_ROUTING_CONFIG_MAP);
		return container;
	}
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForUserPreferencesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForUserPreferencesMap() {
		SQLContainer container = super.getSQLContainerForUserPreferencesMap();
		container.setCreateTableStatement(CREATE_USER_PREFERENCES_MAP);
		return container;
	}

}
