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
package org.pepstock.jem.node.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.pepstock.jem.log.LogAppl;

/**
 * Is managing the database pool of connection to persist jobs and other entities (placed on Hazelcast maps or queue)
 * on database.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DBPoolManager {
	
	private static final DBPoolManager POOL_MANAGER = new DBPoolManager();

	private String driver = null;

	private String url = null;
	
	private String user = null;
	
	private String password = null;
	
	private Properties properties = null;
	
	private String keepAliveConnectionSQL = null;
	
	private BasicDataSource pool = null;
	
	
	/**
	 * Empty constructor
	 */
	private DBPoolManager(){
	}

	/**
	 * 
	 * @return dbpool instance
	 */
	public static DBPoolManager getInstance(){
		return POOL_MANAGER;
	}
	
	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the userid
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUser(String userid) {
		this.user = userid;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return the testConnectionSQL
	 */
	public String getKeepAliveConnectionSQL() {
		return keepAliveConnectionSQL;
	}

	/**
	 * @param testConnectionSQL the testConnectionSQL to set
	 */
	public void setKeepAliveConnectionSQL(String testConnectionSQL) {
		this.keepAliveConnectionSQL = testConnectionSQL;
	}

	/**
	 * Initializes the database pool 
	 *  
	 * @throws SQLException if any errors occurs
	 */
	public void init() throws SQLException {
		if (pool == null){
			// creates the database pool
			pool = new BasicDataSource();
			// sets all information to setup
			// the JDBC connection
			pool.setDriverClassName(driver);
			pool.setUrl(url);
			pool.setUsername(user);
			pool.setPassword(password);

			// if the connection properties have been configured
			// all properties are passed to database pool
			if (properties != null && !properties.isEmpty()){
				for (Object key : properties.keySet()){
					String value = properties.getProperty(key.toString());
					pool.addConnectionProperty(key.toString(), value);
				}
			}
			// Due to Issue #262 set AutoCommit to FALSE
			// Remember to commit all SQL statements
			pool.setDefaultAutoCommit(false);
			// sets the pool
			pool.setInitialSize(5);
			pool.setMaxActive(10);
			pool.setMaxIdle(5);
			// if configured, uses the keep alive query 
			if (keepAliveConnectionSQL != null){
				pool.setValidationQuery(keepAliveConnectionSQL);
			}
		}
	}

	/**
	 * Creates objects by DDL statement. At the moment only tables
	 * 
	 * @param create DDL statement to create objects
	 * @throws SQLException if occurs
	 */
	public synchronized void create(String create) throws SQLException {
		// open connection
		Connection connection = getConnection();
		Statement stmt =null;
		try {
			// if not, exception
			// perform statement
			stmt = connection.createStatement();
			stmt.executeUpdate(create);
			connection.commit();
		} finally {
			// closes always the statement
			try {
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes always the connection
			// putting again on pool
			if (connection != null){
				connection.close();
			}
		}
		return;
	}

	/**
	 * Open H2 connection, setting auto-commit to true
	 * 
	 * @return SQL connection
	 * @throws SQLException if occurs
	 */
	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}
	
	/**
	 * Returns true if the pool has been initialized
	 * @return true if the pool has been initialized
	 */
	public boolean isInitialized(){
		return pool != null;
	}
	
	/**
	 * Close DBPOOL connection
	 * 
	 * @throws SQLException if occurs
	 */
	public void close() throws SQLException {
		// if not null, close
		pool.close();
	}
}