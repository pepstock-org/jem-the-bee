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
package org.pepstock.jem.node.persistence;

/**
 * Contains all DDL and SQL statements constants for Hazelcast persistence.<br>
 * The implementation follows the idea to have a table for each queue, even if
 * the structure and oprations are the same.<br>
 * Having one table for all could be a bottleneck.<br>
 * 
 * Tables INPUT_QUEUE, OUTPUT_QUEUE and ROUTING_QUEUE have the same structure:<br>
 * <br>
 * <code>JOB_ID char(38) primary key</code>: used to store the job id.
 * Currently, with generatorID of Hazelcast and started time, the id is 38 chars
 * long.<br>
 * <code>JOB clob(1000000)</code>: used to store the job, serializing the Job
 * object in XML format. XML must be less than 1M.<br>
 * 
 * @see org.pepstock.jem.Job
 * 
 *      Table CHECKING_QUEUE has the following structure:<br>
 * <br>
 *      <code>PRE_JOB_ID bigint primary key</code>: used to store the pre job
 *      id.<br>
 *      <code>PRE_JOB clob(1000000)</code>: used to store the pre job,
 *      serializing the PreJob object in XML format. XML must be less than 1M.<br>
 * @see org.pepstock.jem.PreJob
 * 
 *      Table CREATE_COMMON_RESOURCES_MAP has the following structure:<br>
 * <br>
 *      <code>RESOURCE_NAME char(32) primary key</code>: used to store the name
 *      of the resource.<br>
 *      <code>RESOURCE clob(1000000)</code>: used to store the resource,
 *      serializing Resource object in XML format. XML must be less than 1M.<br>
 * @see org.pepstock.jem.node.resources.Resource
 * 
 *      Table ROLES_MAP has the following structure:<br>
 * <br>
 *      <code>ROLE_NAME char(32) primary key</code>: used to store the name of
 *      the role.<br>
 *      <code>ROLE clob(1000000)</code>: used to store the role, serializing
 *      Role object in XML format. XML must be less than 1M.<br>
 * @see org.pepstock.jem.node.security.Role
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class  SQLContainer {
	
	private String tableName = null;
	
	private String createTableStatement = null;
	
	private String insertStatement = null;
			
	private String deleteStatement = null;

	private String updateStatement = null;
	
	private String getStatement = null;
	
	private String getAllStatement = null;
	
	private String getAllKeysStatement = null;

	private String checkQueueSizeStatement = null;

	/**
	 * 
	 */
	public SQLContainer() {
		super();
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the createTableStatement
	 */
	public String getCreateTableStatement() {
		return createTableStatement;
	}

	/**
	 * @param createTableStatement the createTableStatement to set
	 */
	public void setCreateTableStatement(String createTableStatement) {
		this.createTableStatement = createTableStatement;
	}

	/**
	 * @return the insertStatement
	 */
	public String getInsertStatement() {
		return insertStatement;
	}

	/**
	 * @param insertStatement the insertStatement to set
	 */
	public void setInsertStatement(String insertStatement) {
		this.insertStatement = insertStatement;
	}

	/**
	 * @return the deleteTableStatement
	 */
	public String getDeleteStatement() {
		return deleteStatement;
	}

	/**
	 * @param deleteStatement the deleteTableStatement to set
	 */
	public void setDeleteStatement(String deleteStatement) {
		this.deleteStatement = deleteStatement;
	}

	/**
	 * @return the updateStatement
	 */
	public String getUpdateStatement() {
		return updateStatement;
	}

	/**
	 * @param updateStatement the updateStatement to set
	 */
	public void setUpdateStatement(String updateStatement) {
		this.updateStatement = updateStatement;
	}

	/**
	 * @return the getStatement
	 */
	public String getGetStatement() {
		return getStatement;
	}

	/**
	 * @param getStatement the getStatement to set
	 */
	public void setGetStatement(String getStatement) {
		this.getStatement = getStatement;
	}

	/**
	 * @return the getAllStatement
	 */
	public String getGetAllStatement() {
		return getAllStatement;
	}

	/**
	 * @param getAllStatement the getAllStatement to set
	 */
	public void setGetAllStatement(String getAllStatement) {
		this.getAllStatement = getAllStatement;
	}

	/**
	 * @return the getAllIDsStatement
	 */
	public String getGetAllKeysStatement() {
		return getAllKeysStatement;
	}

	/**
	 * @param getAllKeysStatement the getAllIDsStatement to set
	 */
	public void setGetAllKeysStatement(String getAllKeysStatement) {
		this.getAllKeysStatement = getAllKeysStatement;
	}

	/**
	 * @return the checkQueueSizeStatement
	 */
	public String getCheckQueueSizeStatement() {
		return checkQueueSizeStatement;
	}

	/**
	 * @param checkQueueSizeStatement the checkQueueSizeStatement to set
	 */
	public void setCheckQueueSizeStatement(String checkQueueSizeStatement) {
		this.checkQueueSizeStatement = checkQueueSizeStatement;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SQLContainer [tableName=" + tableName + ", createTableStatement=" + createTableStatement + ", insertStatement=" + insertStatement + ", deleteTableStatement=" + deleteStatement 
				+ ", updateStatement=" + updateStatement + ", getStatement=" + getStatement + ", getAllStatement=" + getAllStatement + ", getAllIDsStatement=" + getAllKeysStatement + ", checkQueueSizeStatement="
				+ checkQueueSizeStatement + "]";
	}

}