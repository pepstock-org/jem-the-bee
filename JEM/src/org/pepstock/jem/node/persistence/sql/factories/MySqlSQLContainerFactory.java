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
package org.pepstock.jem.node.persistence.sql.factories;

import org.pepstock.jem.node.persistence.sql.SQLContainer;

/**
 * Contains all MySql for Hazelcast persistence
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class MySqlSQLContainerFactory extends DefaultSQLContainerFactory {
	/**
	 * is the type of the database in this case mysql
	 */
	public static final String DATABASE_TYPE = "mysql";

	/**
	 * the sql for the creation of the CHECKING_QUEUE
	 */
	public static final String CREATE_CHECKING_QUEUE = "create table CHECKING_QUEUE (JOB_ID char(39) primary key,	PRE_JOB LONGTEXT not null)";

	/**
	 * the sql for the creation of the INPUT_QUEUE
	 */
	public static final String CREATE_INPUT_QUEUE = "create table INPUT_QUEUE (JOB_ID char(39) primary key,	JOB LONGTEXT not null)";

	/**
	 * the sql for the creation of the RUNNING_QUEUE
	 */
	public static final String CREATE_RUNNING_QUEUE = "create table RUNNING_QUEUE (JOB_ID char(39) primary key,	JOB LONGTEXT not null)";

	/**
	 * the sql for the creation of the OUTPUT_QUEUE
	 */
	public static final String CREATE_OUTPUT_QUEUE = "create table OUTPUT_QUEUE (JOB_ID char(39) primary key, JOB LONGTEXT not null,"
			+ "JOB_NAME			    varchar(128) not null,"
			+ "JOB_USERID		    varchar(64) not null,"
			+ "JOB_ROUTED			bit(1) not null,"
			+ "JOB_SUBMITTED_TIME	bigint not null,"
			+ "JOB_RUNNING_TIME	    bigint,"
			+ "JOB_ENDED_TIME		bigint not null,"
			+ "JOB_RETURN_CODE		smallint not null,"
			+ "JOB_MEMBER			varchar(24),"
			+ "JOB_STEP			    varchar(128),"
			+ "JOB_JCL_TYPE		    varchar(16),"
			+ "JOB_JCL_ENVIRONMENT	varchar(64) not null,"
			+ "JOB_JCL_DOMAIN		varchar(64) not null,"
			+ "JOB_JCL_AFFINITY	    varchar(128) not null,"
			+ "JOB_JCL_PRIORITY	    smallint not null,"
			+ "JOB_JCL_MEMORY		smallint not null"
			+ ")";

	/**
	 * the sql for the creation of the ROUTING_QUEUE
	 */
	public static final String CREATE_ROUTING_QUEUE = "create table ROUTING_QUEUE (JOB_ID char(39) primary key,	JOB LONGTEXT not null)";

	/**
	 * the sql for the creation of the COMMON_RESOURCES_MAP
	 */
	public static final String CREATE_COMMON_RESOURCES_MAP = "create table COMMON_RESOURCES_MAP (RESOURCE_NAME char(32) primary key, RESOURCE LONGTEXT not null)";

	/**
	 * the sql for the creation of the NODES_MAP
	 */
	public static final String CREATE_NODES_MAP = "create table NODES_MAP (NODE_KEY char(40) primary key, NODE LONGTEXT not null)";

	/**
	 * the sql for the creation of the ROLES_MAP
	 */
	public static final String CREATE_ROLES_MAP = "create table ROLES_MAP (ROLE_NAME char(32) primary key, ROLE LONGTEXT not null)";

	/**
	 * Create table for ROUTING_CONFIGs table
	 */
	public static final String CREATE_ROUTING_CONFIG_MAP = "create table ROUTING_CONFIG_MAP (ROUTING_CONFIG_NAME char(32) primary key, ROUTING_CONFIG LONGTEXT not null)";

	/**
	 * Create table for USER_PREFERENCESs table
	 */
	private static final String CREATE_USER_PREFERENCES_MAP = "create table USER_PREFERENCES_MAP (USER_ID char(32) primary key, USER_PREFERENCES LONGTEXT not null)";


	/**
	 * 
	 */
	public MySqlSQLContainerFactory() {
		
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