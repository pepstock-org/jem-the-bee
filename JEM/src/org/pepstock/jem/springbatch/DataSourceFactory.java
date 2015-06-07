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
package org.pepstock.jem.springbatch;

import java.net.URL;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Utility of SpringBatch to set the restartability of jobs.<br>
 * It collects all keys necessary to configure data source of SB JCL.<br>
 * It exposes the methods to create a data source pool.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class DataSourceFactory {
	
	private static final String JDBC_URL_KEY = "jem.jdbc.url";

	private static final String JDBC_DRIVER_KEY = "jem.jdbc.driver";
	
	private static final String JDBC_USER_KEY = "jem.jdbc.user";
	
	private static final String JDBC_PASSWORD_KEY = "jem.jdbc.password";
	
	private static final String JDBC_PROPERTIES_KEY = "jem.jdbc.properties";
	
	private static final String JDBC_TYPE_KEY = "jem.jdbc.type";

	/**
	 * To avoid any instantiation
	 */
	private DataSourceFactory() {
	}
	
	/**
	 * Checks if the properties object contains all necessary keys to setup a data source pool.
	 * 
	 * @param props properties instance defined inside of SB JCL definition.
	 * @return <code>true</code> if properties contains all necessary keys, otherwise <code>false</code>
	 */
	public static boolean isJobRepositoryPersistent(Properties props){
		return props.containsKey(JDBC_DRIVER_KEY) &&
				props.containsKey(JDBC_URL_KEY) &&
				props.containsKey(JDBC_TYPE_KEY) &&
				props.containsKey(JDBC_USER_KEY) &&
				props.containsKey(JDBC_PASSWORD_KEY);
	}
	
	/**
	 * Returns the URL of JDBC driver, defined in SB JCL configuration, using reflection.
	 * 
	 * @param props properties instance defined inside of SB JCL definition.
	 * @return URL of JDBC driver
	 */
	public static URL getDriverJar(Properties props){
		if (!isJobRepositoryPersistent(props)){
			return null;
		}
		String driver = props.getProperty(JDBC_DRIVER_KEY);
		return DataSourceFactory.class.getResource('/'+driver.replace('.', '/')+".class");
	}
	
	/**
	 * Returns the data source type of JDBC definition.<br>
	 * Here are the supported JDBC type (from <code>org.springframework.batch.support.DatabaseType</code>):
	 * <br>
	 * <pre>
	 * db2          DB2("DB2"), DB2ZOS("DB2ZOS")
	 * derby        DERBY("Apache Derby")
	 * h2           H2("H2")
	 * hsqldb       HSQL("HSQL Database Engine")
	 * mysql        MYSQL("MySQL")
	 * oracle10g    ORACLE("Oracle")
	 * postgresql   POSTGRES("PostgreSQL")
	 * sqlserver    SQLSERVER("Microsoft SQL Server")
	 * sybase       SYBASE("Sybase")
	 * sqlf         SQLITE("SQLite")
	 * </pre>
	 * <br>
	 * 
	 * @param props properties instance defined inside of SB JCL definition.
	 * @return the data source type.
	 */
	public static String getDataSourceType(Properties props){
		return props.getProperty(JDBC_TYPE_KEY);
	}

	/**
	 * Creates a data source pool to connect to a data base.
	 * @param props properties instance defined inside of SB JCL definition.
	 * @return a data source to use for restartability
	 */
	public static DataSource createDataSource(Properties props){
		// checks if properties are all set
		if (!isJobRepositoryPersistent(props)){
			return null;
		}
		// gets all properties
		String driver = props.getProperty(JDBC_DRIVER_KEY);
		String url = props.getProperty(JDBC_URL_KEY);
		String user = props.getProperty(JDBC_USER_KEY);
		String password = props.getProperty(JDBC_PASSWORD_KEY);
		
		String jdbcProperties = props.getProperty(JDBC_PROPERTIES_KEY);
		
		// creates the data source
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		if (jdbcProperties != null){
			dataSource.setConnectionProperties(jdbcProperties);
		}
		return dataSource;
	}
}
