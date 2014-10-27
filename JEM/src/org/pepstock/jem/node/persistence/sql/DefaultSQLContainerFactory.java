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
package org.pepstock.jem.node.persistence.sql;

import org.pepstock.jem.node.persistence.SQLContainer;
import org.pepstock.jem.node.persistence.SQLContainerFactory;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DefaultSQLContainerFactory implements SQLContainerFactory {
	
	private static final String TEST_CONNECTION_SQL_QUERY = "SELECT 1";

	/*------------------------------------
	| C H E C K I N G                          |
	------------------------------------*/
	
	private static final String TABLE_NAME_CHECKING_QUEUE = "CHECKING_QUEUE";

	/**
	 * Create table for checking table
	 */
	private static final String CREATE_CHECKING_QUEUE = "create table CHECKING_QUEUE (PRE_JOB_ID BIGINT primary key not null, PRE_JOB CLOB (1000000) not null)";
	/**
	 * Insert statement to put a pre job in CHECKING queue
	 */
	private static final String INSERT_CHECKING_QUEUE = "insert into CHECKING_QUEUE values(?, ?)";

	/**
	 * Delete statement to remove a pre job from CHECKING queue
	 */
	private static final String DELETE_CHECKING_QUEUE = "delete from CHECKING_QUEUE where PRE_JOB_ID = ?";

	/**
	 * Update statement to change a pre job in CHECKING queue
	 */
	private static final String UPDATE_CHECKING_QUEUE = "update CHECKING_QUEUE set PRE_JOB = ? where PRE_JOB_ID = ?";

	/**
	 * Select statement to get a pre job from CHECKING queue
	 */
	private static final String GET_PRE_JOB_CHECKING_QUEUE = "select PRE_JOB from CHECKING_QUEUE where PRE_JOB_ID = ?";

	/**
	 * Select statement to get all pre jobs (using a list of IDS) from CHECKING
	 * queue. is not a PrepareStatement but a message format to feed with a list
	 * of jobs id.
	 */
	private static final String GET_ALL_PRE_JOBS_CHECKING_QUEUE = "select PRE_JOB_ID PRE_JOB from CHECKING_QUEUE where PRE_JOB_ID IN ( {0} )";

	/**
	 * Select statement to get all job ids (the keys of table) from CHECKING
	 * queue
	 */
	private static final String GET_PRE_JOB_IDS_CHECKING_QUEUE = "select PRE_JOB_ID from CHECKING_QUEUE";

	/**
	 * Get size of pre jobs inside the CHECKING_QUEUE
	 */
	private static final String GET_PRE_JOBS_CHECKING_QUEUE_SIZES = "select SUM(LENGTH(PRE_JOB)) from CHECKING_QUEUE";


	/*------------------------------------
	| I N P U T                          |
	------------------------------------*/

	private static final String TABLE_NAME_INPUT_QUEUE = "INPUT_QUEUE";
	
	/**
	 * Create table for input table
	 */
	private static final String CREATE_INPUT_QUEUE = "create table INPUT_QUEUE (JOB_ID char(39) primary key not null, JOB CLOB(1000000) not null)";
	
	/**
	 * Insert statement to put a job in INPUT queue
	 */
	private static final String INSERT_INPUT_QUEUE = "insert into INPUT_QUEUE values(?, ?)";

	/**
	 * Delete statement to remove a job from INPUT queue
	 */
	private static final String DELETE_INPUT_QUEUE = "delete from INPUT_QUEUE where JOB_ID = ?";

	/**
	 * Update statement to change a job in INPUT queue
	 */
	private static final String UPDATE_INPUT_QUEUE = "update INPUT_QUEUE set JOB = ? where JOB_ID = ?";

	/**
	 * Select statement to get a job from INPUT queue
	 */
	private static final String GET_JOB_INPUT_QUEUE = "select JOB from INPUT_QUEUE where JOB_ID = ?";

	/**
	 * Select statement to get all jobs (using a list of IDS) from INPUT queue.
	 * is not a PrepareStatement but a message format to feed with a list of
	 * jobs id.
	 */
	private static final String GET_ALL_JOBS_INPUT_QUEUE = "select JOB from INPUT_QUEUE where JOB_ID IN ( {0} )";

	/**
	 * Select statement to get all job ids (the keys of table) from INPUT queue
	 */
	private static final String GET_JOB_IDS_INPUT_QUEUE = "select JOB_ID from INPUT_QUEUE";

	/**
	 * Get size of jobs inside the INPUT_QUEUE
	 */
	private static final String GET_JOBS_INPUT_QUEUE_SIZE = "select SUM(LENGTH(JOB)) from INPUT_QUEUE";
	
	/*------------------------------------
	| R U N N I N G                      |
	------------------------------------*/

	private static final String TABLE_NAME_RUNNING_QUEUE = "RUNNING_QUEUE";
	
	/**
	 * Create table for input table
	 */
	private static final String CREATE_RUNNING_QUEUE = "create table RUNNING_QUEUE (JOB_ID char(39) primary key not null, JOB CLOB(1000000) not null)";
	
	/**
	 * Insert statement to put a job in INPUT queue
	 */
	private static final String INSERT_RUNNING_QUEUE = "insert into RUNNING_QUEUE values(?, ?)";

	/**
	 * Delete statement to remove a job from INPUT queue
	 */
	private static final String DELETE_RUNNING_QUEUE = "delete from RUNNING_QUEUE where JOB_ID = ?";

	/**
	 * Update statement to change a job in INPUT queue
	 */
	private static final String UPDATE_RUNNING_QUEUE = "update RUNNING_QUEUE set JOB = ? where JOB_ID = ?";

	/**
	 * Select statement to get a job from INPUT queue
	 */
	private static final String GET_JOB_RUNNING_QUEUE = "select JOB from RUNNING_QUEUE where JOB_ID = ?";

	/**
	 * Select statement to get all jobs (using a list of IDS) from INPUT queue.
	 * is not a PrepareStatement but a message format to feed with a list of
	 * jobs id.
	 */
	private static final String GET_ALL_JOBS_RUNNING_QUEUE = "select JOB from RUNNING_QUEUE where JOB_ID IN ( {0} )";

	/**
	 * Select statement to get all job ids (the keys of table) from INPUT queue
	 */
	private static final String GET_JOB_IDS_RUNNING_QUEUE = "select JOB_ID from RUNNING_QUEUE";

	/**
	 * Get size of jobs inside the RUNNING_QUEUE
	 */
	private static final String GET_JOBS_RUNNING_QUEUE_SIZE = "select SUM(LENGTH(JOB)) from RUNNING_QUEUE";

	/*------------------------------------
	| O U T P U T                        |
	------------------------------------*/

	private static final String TABLE_NAME_OUTPUT_QUEUE = "OUTPUT_QUEUE";
	
	/**
	 * Create table for output table
	 */
	private static final String CREATE_OUTPUT_QUEUE = "create table OUTPUT_QUEUE (JOB_ID char(39) primary key not null, JOB CLOB (1000000) not null)";
	
	/**
	 * Insert statement to put a job in OUTPUT queue
	 */
	private static final String INSERT_OUTPUT_QUEUE = "insert into OUTPUT_QUEUE values(?, ?)";

	/**
	 * Delete statement to remove a job from OUTPUT queue
	 */
	private static final String DELETE_OUTPUT_QUEUE = "delete from OUTPUT_QUEUE where JOB_ID = ?";

	/**
	 * Update statement to change a job in OUTPUT queue
	 */
	private static final String UPDATE_OUTPUT_QUEUE = "update OUTPUT_QUEUE set JOB = ? where JOB_ID = ?";

	/**
	 * Select statement to get a job from OUTPUT queue
	 */
	private static final String GET_JOB_OUTPUT_QUEUE = "select JOB from OUTPUT_QUEUE where JOB_ID = ?";

	/**
	 * Select statement to get all jobs (using a list of IDS) from OUTPUT queue.
	 * is not a PrepareStatement but a message format to feed with a list of
	 * jobs id.
	 */
	private static final String GET_ALL_JOBS_OUTPUT_QUEUE = "select JOB from OUTPUT_QUEUE where JOB_ID IN ( {0} )";

	/**
	 * Select statement to get all job ids (the keys of table) from OUTPUT queue
	 */
	private static final String GET_JOB_IDS_OUTPUT_QUEUE = "select JOB_ID from OUTPUT_QUEUE";

	/**
	 * Get size of jobs inside the OUTPUT_QUEUE
	 */
	private static final String GET_JOBS_OUTPUT_QUEUE_SIZE = "select SUM(LENGTH(JOB)) from OUTPUT_QUEUE";

	/*------------------------------------
	| R O U T I N G                      |
	------------------------------------*/

	private static final String TABLE_NAME_ROUTING_QUEUE = "ROUTING_QUEUE";
	
	/**
	 * Create table for routing table
	 */
	private static final String CREATE_ROUTING_QUEUE = "create table ROUTING_QUEUE (JOB_ID char(39) primary key not null,	JOB CLOB (1000000) not null)";

	/**
	 * Insert statement to put a job in ROUTING queue
	 */
	private static final String INSERT_ROUTING_QUEUE = "insert into ROUTING_QUEUE values(?, ?)";

	/**
	 * Delete statement to remove a job from ROUTING queue
	 */
	private static final String DELETE_ROUTING_QUEUE = "delete from ROUTING_QUEUE where JOB_ID = ?";

	/**
	 * Update statement to change a job in ROUTING queue
	 */
	private static final String UPDATE_ROUTING_QUEUE = "update ROUTING_QUEUE set JOB = ? where JOB_ID = ?";

	/**
	 * Select statement to get a job from ROUTING queue
	 */
	private static final String GET_JOB_ROUTING_QUEUE = "select JOB from ROUTING_QUEUE where JOB_ID = ?";

	/**
	 * Select statement to get all jobs (using a list of IDS) from ROUTING_QUEUE
	 * queue. is not a PrepareStatement but a message format to feed with a list
	 * of jobs id.
	 */
	private static final String GET_ALL_JOBS_ROUTING_QUEUE = "select JOB from ROUTING_QUEUE where JOB_ID IN ( {0} )";

	/**
	 * Select statement to get all job ids (the keys of table) from ROUTING
	 * queue
	 */
	private static final String GET_JOB_IDS_ROUTING_QUEUE = "select JOB_ID from ROUTING_QUEUE";

	/**
	 * Get size of jobs inside the ROUTING_QUEUE
	 */
	private static final String GET_JOBS_ROUTING_QUEUE_SIZE = "select SUM(LENGTH(JOB)) from ROUTING_QUEUE";

	/*------------------------------------
	| Common R E S O U R C E S           |
	------------------------------------*/

	private static final String TABLE_NAME_COMMON_RESOURCES_MAP = "COMMON_RESOURCES_MAP";
	
	/**
	 * Create table for common resources table
	 */
	private static final String CREATE_COMMON_RESOURCES_MAP = "create table COMMON_RESOURCES_MAP (RESOURCE_NAME char(32) primary key not null, RESOURCE CLOB (500000) not null)";

	/**
	 * Insert statement to put a resource in COMMON RESOURCES map
	 */
	private static final String INSERT_COMMON_RESOURCES_MAP = "insert into COMMON_RESOURCES_MAP values(?, ?)";

	/**
	 * Delete statement to remove a resource from COMMON RESOURCES map
	 */
	private static final String DELETE_COMMON_RESOURCES_MAP = "delete from COMMON_RESOURCES_MAP where RESOURCE_NAME = ?";

	/**
	 * Update statement to change a resource in COMMON RESOURCES map
	 */
	private static final String UPDATE_COMMON_RESOURCES_MAP = "update COMMON_RESOURCES_MAP set RESOURCE = ? where RESOURCE_NAME = ?";

	/**
	 * Select statement to get a resource from COMMON RESOURCES map
	 */
	private static final String GET_COMMON_RESOURCE_MAP = "select RESOURCE from COMMON_RESOURCES_MAP where RESOURCE_NAME = ?";

	/**
	 * Select statement to get all resources (using a list of names) from COMMON
	 * RESOURCES_map map. is not a PrepareStatement but a message format to feed
	 * with a list of resources names.
	 */
	private static final String GET_ALL_COMMON_RESOURCES_MAP = "select RESOURCE from COMMON_RESOURCES_MAP where RESOURCE_NAME IN ( {0} )";

	/**
	 * Select statement to get all resource names (the keys of table) from
	 * COMMON RESOURCES map
	 */
	private static final String GET_COMMON_RESOURCE_NAMES_MAP = "select RESOURCE_NAME from COMMON_RESOURCES_MAP";

	/**
	 * Get size of resources inside the COMMON_RESOURCES_MAP
	 */
	private static final String GET_RESOURCES_SIZE = "select SUM(LENGTH(RESOURCE)) from COMMON_RESOURCES_MAP";

	/*------------------------------------
	| R O L E S                          |
	------------------------------------*/
	
	private static final String TABLE_NAME_ROLES_MAP = "ROLES_MAP";
	/**
	 * Create table for ROLEs table
	 */
	private static final String CREATE_ROLES_MAP = "create table ROLES_MAP (ROLE_NAME char(32) primary key not null, ROLE CLOB (500000) not null)";
	
	/**
	 * Insert statement to put a ROLE in ROLES map
	 */
	private static final String INSERT_ROLES_MAP = "insert into ROLES_MAP values(?, ?)";

	/**
	 * Delete statement to remove a ROLE from ROLES map
	 */
	private static final String DELETE_ROLES_MAP = "delete from ROLES_MAP where ROLE_NAME = ?";

	/**
	 * Update statement to change a ROLE in ROLES map
	 */
	private static final String UPDATE_ROLES_MAP = "update ROLES_MAP set ROLE = ? where ROLE_NAME = ?";

	/**
	 * Select statement to get a ROLE from ROLES map
	 */
	private static final String GET_ROLE_MAP = "select ROLE from ROLES_MAP where ROLE_NAME = ?";

	/**
	 * Select statement to get all ROLEs (using a list of names) from ROLES_map
	 * map. is not a PrepareStatement but a message format to feed with a list
	 * of ROLEs names.
	 */
	private static final String GET_ALL_ROLES_MAP = "select ROLE from ROLES_MAP where ROLE_NAME IN ( {0} )";

	/**
	 * Select statement to get all ROLE names (the keys of table) from ROLES map
	 */
	private static final String GET_ROLE_NAMES_MAP = "select ROLE_NAME from ROLES_MAP";

	/**
	 * Get size of roles inside the ROLES_MAP
	 */
	private static final String GET_ROLES_SIZE = "select SUM(LENGTH(ROLE)) from ROLES_MAP";

	/*------------------------------------
	| N O D E S                          |
	------------------------------------*/
	
	private static final String TABLE_NAME_NODES_MAP = "NODES_MAP";
	/**
	 * Create table for NODEs table
	 */
	private static final String CREATE_NODES_MAP = "create table NODES_MAP (NODE_KEY char(40) primary key not null, NODE CLOB (500000) not null)";
	
	/**
	 * Insert statement to put a NODE in NODES map
	 */
	private static final String INSERT_NODES_MAP = "insert into NODES_MAP values(?, ?)";

	/**
	 * Delete statement to remove a NODE from NODES map
	 */
	private static final String DELETE_NODES_MAP = "delete from NODES_MAP where NODE_KEY = ?";

	/**
	 * Update statement to change a NODE in NODES map
	 */
	private static final String UPDATE_NODES_MAP = "update NODES_MAP set NODE = ? where NODE_KEY = ?";

	/**
	 * Select statement to get a NODE from NODES map
	 */
	private static final String GET_NODE_MAP = "select NODE from NODES_MAP where NODE_KEY = ?";

	/**
	 * Select statement to get all NODEs (using a list of names) from NODES_map
	 * map. is not a PrepareStatement but a message format to feed with a list
	 * of NODEs names.
	 */
	private static final String GET_ALL_NODES_MAP = "select NODE from NODES_MAP where NODE_KEY IN ( {0} )";

	/**
	 * Select statement to get all NODE names (the keys of table) from NODES map
	 */
	private static final String GET_NODE_KEYS_MAP = "select NODE_KEY from NODES_MAP";

	/**
	 * Get size of roles inside the NODES_MAP
	 */
	private static final String GET_NODES_SIZE = "select SUM(LENGTH(NODE)) from NODES_MAP";

	/*------------------------------------
	| R O U T I N G   C O N F            |
	------------------------------------*/
	
	private static final String TABLE_NAME_ROUTING_CONFIG_MAP = "ROUTING_CONFIG_MAP";
	/**
	 * Create table for ROUTING_CONFIGs table
	 */
	private static final String CREATE_ROUTING_CONFIG_MAP = "create table ROUTING_CONFIG_MAP (ROUTING_CONFIG_NAME char(32) primary key not null, ROUTING_CONFIG CLOB (500000) not null)";
	
	/**
	 * Insert statement to put a ROUTING_CONFIG in ROUTING_CONFIGS map
	 */
	private static final String INSERT_ROUTING_CONFIG_MAP = "insert into ROUTING_CONFIG_MAP values(?, ?)";

	/**
	 * Delete statement to remove a ROUTING_CONFIG from ROUTING_CONFIGS map
	 */
	private static final String DELETE_ROUTING_CONFIG_MAP = "delete from ROUTING_CONFIG_MAP where ROUTING_CONFIG_NAME = ?";

	/**
	 * Update statement to change a ROUTING_CONFIG in ROUTING_CONFIGS map
	 */
	private static final String UPDATE_ROUTING_CONFIG_MAP = "update ROUTING_CONFIG_MAP set ROUTING_CONFIG = ? where ROUTING_CONFIG_NAME = ?";

	/**
	 * Select statement to get a ROUTING_CONFIG from ROUTING_CONFIGS map
	 */
	private static final String GET_ROUTING_CONFIG_MAP = "select ROUTING_CONFIG from ROUTING_CONFIG_MAP where ROUTING_CONFIG_NAME = ?";

	/**
	 * Select statement to get all ROUTING_CONFIGs (using a list of names) from ROUTING_CONFIGS_map
	 * map. is not a PrepareStatement but a message format to feed with a list
	 * of ROUTING_CONFIGs names.
	 */
	private static final String GET_ALL_ROUTING_CONFIG_MAP = "select ROUTING_CONFIG from ROUTING_CONFIG_MAP where ROUTING_CONFIG_NAME IN ( {0} )";

	/**
	 * Select statement to get all ROUTING_CONFIG names (the keys of table) from ROUTING_CONFIGS map
	 */
	private static final String GET_ROUTING_CONFIG_NAMES_MAP = "select ROUTING_CONFIG_NAME from ROUTING_CONFIG_MAP";

	/**
	 * Get size of roles inside the ROUTING_CONFIG_MAP
	 */
	private static final String GET_ROUTING_CONFIGS_SIZE = "select SUM(LENGTH(ROUTING_CONFIG)) from ROUTING_CONFIG_MAP";

	
	/*------------------------------------
	| U S E R    P R E F E R E N C E S   |
	------------------------------------*/
	
	private static final String TABLE_NAME_USER_PREFERENCES_MAP = "USER_PREFERENCES_MAP";
	/**
	 * Create table for USER_PREFERENCESs table
	 */
	private static final String CREATE_USER_PREFERENCES_MAP = "create table USER_PREFERENCES_MAP (USER_ID char(32) primary key not null, USER_PREFERENCES CLOB (500000) not null)";
	
	/**
	 * Insert statement to put a USER_PREFERENCES in USER_PREFERENCESS map
	 */
	private static final String INSERT_USER_PREFERENCES_MAP = "insert into USER_PREFERENCES_MAP values(?, ?)";

	/**
	 * Delete statement to remove a USER_PREFERENCES from USER_PREFERENCESS map
	 */
	private static final String DELETE_USER_PREFERENCES_MAP = "delete from USER_PREFERENCES_MAP where USER_ID = ?";

	/**
	 * Update statement to change a USER_PREFERENCES in USER_PREFERENCESS map
	 */
	private static final String UPDATE_USER_PREFERENCES_MAP = "update USER_PREFERENCES_MAP set USER_PREFERENCES = ? where USER_ID = ?";

	/**
	 * Select statement to get a USER_PREFERENCES from USER_PREFERENCESS map
	 */
	private static final String GET_USER_PREFERENCES_MAP = "select USER_PREFERENCES from USER_PREFERENCES_MAP where USER_ID = ?";

	/**
	 * Select statement to get all USER_PREFERENCESs (using a list of names) from USER_PREFERENCESS_map
	 * map. is not a PrepareStatement but a message format to feed with a list
	 * of USER_PREFERENCESs names.
	 */
	private static final String GET_ALL_USER_PREFERENCES_MAP = "select USER_PREFERENCES, USER_ID from USER_PREFERENCES_MAP where USER_ID IN ( {0} )";

	/**
	 * Select statement to get all USER_PREFERENCES names (the keys of table) from USER_PREFERENCESS map
	 */
	private static final String GET_USER_IDS_MAP = "select USER_ID from USER_PREFERENCES_MAP";

	/**
	 * Get size of roles inside the USER_PREFERENCES_MAP
	 */
	private static final String GET_USER_PREFERENCESS_SIZE = "select SUM(LENGTH(USER_PREFERENCES)) from USER_PREFERENCES_MAP";

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForInputQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForInputQueue() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_INPUT_QUEUE);
		container.setCreateTableStatement(CREATE_INPUT_QUEUE);
		container.setCheckQueueSizeStatement(GET_JOBS_INPUT_QUEUE_SIZE);
		container.setDeleteStatement(DELETE_INPUT_QUEUE);
		container.setGetAllStatement(GET_ALL_JOBS_INPUT_QUEUE);
		container.setGetAllKeysStatement(GET_JOB_IDS_INPUT_QUEUE);
		container.setGetStatement(GET_JOB_INPUT_QUEUE);
		container.setInsertStatement(INSERT_INPUT_QUEUE);
		container.setUpdateStatement(UPDATE_INPUT_QUEUE);
		return container;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForInputQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForRunningQueue() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_RUNNING_QUEUE);
		container.setCreateTableStatement(CREATE_RUNNING_QUEUE);
		container.setCheckQueueSizeStatement(GET_JOBS_RUNNING_QUEUE_SIZE);
		container.setDeleteStatement(DELETE_RUNNING_QUEUE);
		container.setGetAllStatement(GET_ALL_JOBS_RUNNING_QUEUE);
		container.setGetAllKeysStatement(GET_JOB_IDS_RUNNING_QUEUE);
		container.setGetStatement(GET_JOB_RUNNING_QUEUE);
		container.setInsertStatement(INSERT_RUNNING_QUEUE);
		container.setUpdateStatement(UPDATE_RUNNING_QUEUE);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForOutputQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForOutputQueue() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_OUTPUT_QUEUE);
		container.setCreateTableStatement(CREATE_OUTPUT_QUEUE);
		container.setCheckQueueSizeStatement(GET_JOBS_OUTPUT_QUEUE_SIZE);
		container.setDeleteStatement(DELETE_OUTPUT_QUEUE);
		container.setGetAllStatement(GET_ALL_JOBS_OUTPUT_QUEUE);
		container.setGetAllKeysStatement(GET_JOB_IDS_OUTPUT_QUEUE);
		container.setGetStatement(GET_JOB_OUTPUT_QUEUE);
		container.setInsertStatement(INSERT_OUTPUT_QUEUE);
		container.setUpdateStatement(UPDATE_OUTPUT_QUEUE);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForRoutingQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForRoutingQueue() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_ROUTING_QUEUE);
		container.setCreateTableStatement(CREATE_ROUTING_QUEUE);
		container.setCheckQueueSizeStatement(GET_JOBS_ROUTING_QUEUE_SIZE);
		container.setDeleteStatement(DELETE_ROUTING_QUEUE);
		container.setGetAllStatement(GET_ALL_JOBS_ROUTING_QUEUE);
		container.setGetAllKeysStatement(GET_JOB_IDS_ROUTING_QUEUE);
		container.setGetStatement(GET_JOB_ROUTING_QUEUE);
		container.setInsertStatement(INSERT_ROUTING_QUEUE);
		container.setUpdateStatement(UPDATE_ROUTING_QUEUE);
		return container;
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForNodesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForNodesMap() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_NODES_MAP);
		container.setCreateTableStatement(CREATE_NODES_MAP);
		container.setCheckQueueSizeStatement(GET_NODES_SIZE);
		container.setDeleteStatement(DELETE_NODES_MAP);
		container.setGetAllStatement(GET_ALL_NODES_MAP);
		container.setGetAllKeysStatement(GET_NODE_KEYS_MAP);
		container.setGetStatement(GET_NODE_MAP);
		container.setInsertStatement(INSERT_NODES_MAP);
		container.setUpdateStatement(UPDATE_NODES_MAP);
		return container;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForRolesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForRolesMap() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_ROLES_MAP);
		container.setCreateTableStatement(CREATE_ROLES_MAP);
		container.setCheckQueueSizeStatement(GET_ROLES_SIZE);
		container.setDeleteStatement(DELETE_ROLES_MAP);
		container.setGetAllStatement(GET_ALL_ROLES_MAP);
		container.setGetAllKeysStatement(GET_ROLE_NAMES_MAP);
		container.setGetStatement(GET_ROLE_MAP);
		container.setInsertStatement(INSERT_ROLES_MAP);
		container.setUpdateStatement(UPDATE_ROLES_MAP);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForCommonResourcesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForCommonResourcesMap() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_COMMON_RESOURCES_MAP);
		container.setCreateTableStatement(CREATE_COMMON_RESOURCES_MAP);
		container.setCheckQueueSizeStatement(GET_RESOURCES_SIZE);
		container.setDeleteStatement(DELETE_COMMON_RESOURCES_MAP);
		container.setGetAllStatement(GET_ALL_COMMON_RESOURCES_MAP);
		container.setGetAllKeysStatement(GET_COMMON_RESOURCE_NAMES_MAP);
		container.setGetStatement(GET_COMMON_RESOURCE_MAP);
		container.setInsertStatement(INSERT_COMMON_RESOURCES_MAP);
		container.setUpdateStatement(UPDATE_COMMON_RESOURCES_MAP);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForCheckingQueue()
	 */
	@Override
	public SQLContainer getSQLContainerForCheckingQueue() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_CHECKING_QUEUE);
		container.setCreateTableStatement(CREATE_CHECKING_QUEUE);
		container.setCheckQueueSizeStatement(GET_PRE_JOBS_CHECKING_QUEUE_SIZES);
		container.setDeleteStatement(DELETE_CHECKING_QUEUE);
		container.setGetAllStatement(GET_ALL_PRE_JOBS_CHECKING_QUEUE);
		container.setGetAllKeysStatement(GET_PRE_JOB_IDS_CHECKING_QUEUE);
		container.setGetStatement(GET_PRE_JOB_CHECKING_QUEUE);
		container.setInsertStatement(INSERT_CHECKING_QUEUE);
		container.setUpdateStatement(UPDATE_CHECKING_QUEUE);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getTestConnectionSQL()
	 */
	@Override
	public String getKeepAliveConnectionSQL() {
		return TEST_CONNECTION_SQL_QUERY;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForRoutingConfigMap()
	 */
	@Override
	public SQLContainer getSQLContainerForRoutingConfigMap() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_ROUTING_CONFIG_MAP);
		container.setCreateTableStatement(CREATE_ROUTING_CONFIG_MAP);
		container.setCheckQueueSizeStatement(GET_ROUTING_CONFIGS_SIZE);
		container.setDeleteStatement(DELETE_ROUTING_CONFIG_MAP);
		container.setGetAllStatement(GET_ALL_ROUTING_CONFIG_MAP);
		container.setGetAllKeysStatement(GET_ROUTING_CONFIG_NAMES_MAP);
		container.setGetStatement(GET_ROUTING_CONFIG_MAP);
		container.setInsertStatement(INSERT_ROUTING_CONFIG_MAP);
		container.setUpdateStatement(UPDATE_ROUTING_CONFIG_MAP);
		return container;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getSQLContainerForUserPreferencesMap()
	 */
	@Override
	public SQLContainer getSQLContainerForUserPreferencesMap() {
		SQLContainer container = new SQLContainer();
		container.setTableName(TABLE_NAME_USER_PREFERENCES_MAP);
		container.setCreateTableStatement(CREATE_USER_PREFERENCES_MAP);
		container.setCheckQueueSizeStatement(GET_USER_PREFERENCESS_SIZE);
		container.setDeleteStatement(DELETE_USER_PREFERENCES_MAP);
		container.setGetAllStatement(GET_ALL_USER_PREFERENCES_MAP);
		container.setGetAllKeysStatement(GET_USER_IDS_MAP);
		container.setGetStatement(GET_USER_PREFERENCES_MAP);
		container.setInsertStatement(INSERT_USER_PREFERENCES_MAP);
		container.setUpdateStatement(UPDATE_USER_PREFERENCES_MAP);
		return container;
	}

}