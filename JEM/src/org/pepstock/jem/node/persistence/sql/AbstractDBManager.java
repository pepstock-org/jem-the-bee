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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.persistence.AbstractDatabaseManager;
import org.pepstock.jem.node.persistence.DatabaseException;
import org.pepstock.jem.node.persistence.DatabaseManager;
import org.pepstock.jem.util.filters.Filter;

import com.thoughtworks.xstream.XStream;

/**
 * Manages all SQL statements towards the database to persist the Hazelcast items.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 * @param <T> object stored in Hazelcast map 
 * 
 */
public abstract class AbstractDBManager<T> extends AbstractDatabaseManager<T> implements DatabaseManager<T>{
	
	private static final int FIRST_FIELD = 1;
	
	private static final int SECOND_FIELD = 2;

	private XStream xStream = null;
	
	private String queueName = null;
	
	private SQLContainer sqlContainer = null;
	
	private EvictionHandler<T> envictionHandler = null;

	/**
	 * Standard constructor which creates a Xstream instance
	 * @param queueName hazelcast queuename
	 * @param sqlContainer SQL container
	 */
	AbstractDBManager(String queueName, SQLContainer sqlContainer) {
		this(queueName, sqlContainer, false);
	}
	
	/**
	 * Standard constructor which creates a Xstream instance
	 * @param queueName hazelcast queuename
	 * @param sqlContainer SQL container
	 * @param canBeEvicted if the map can be evited in HC
	 */
	AbstractDBManager(String queueName, SQLContainer sqlContainer, boolean canBeEvicted) {
		this(queueName, sqlContainer, new XStream(), canBeEvicted);
	}

	/**
	 * Creates the object using the XStream instance 
	 * @param queueName hazelcast queuename
	 * @param sqlContainer SQL container
	 * @param xs XStream instance
	 * @param canBeEvicted if the map can be evited in HC
	 */
	AbstractDBManager(String queueName, SQLContainer sqlContainer, XStream xs, boolean canBeEvicted) {
		super(canBeEvicted);
		this.queueName = queueName;
		this.xStream = xs;
		this.sqlContainer = sqlContainer;
		
	}
	
	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @return the sqlContainer
	 */
	public SQLContainer getSqlContainer() {
		return sqlContainer;
	}
	
	/**
	 * @return the envictionHandler
	 */
	public EvictionHandler<T> getEnvictionHandler() {
		return envictionHandler;
	}

	/**
	 * @param envictionHandler the envictionHandler to set
	 */
	public void setEnvictionHandler(EvictionHandler<T> envictionHandler) {
		this.envictionHandler = envictionHandler;
	}

	/**
	 * Returns for a key fields for object
	 * @param item instance used to get key
	 * @return key to use on database table
	 */
	public abstract String getKey(T item);

	/**
	 * Deletes an instance from queue by the key of table
	 * 
	 * @param key instance key
	 * @throws DatabaseException if occurs
	 */
	public void delete(String key) throws DatabaseException {
		// open connection
		// getting a connection from pool
		Connection connection = null;
		PreparedStatement updateStmt = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			updateStmt = connection.prepareStatement(sqlContainer.getDeleteStatement());
			// set resource name in prepared statement
			// checks if is a string or long (long used only for queue of HC)
			updateStmt.setString(FIRST_FIELD, key);
			// executes the statement
			updateStmt.executeUpdate();
			// commit
			connection.commit();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally{
			// closes statement
			try {
				if (updateStmt != null){
					updateStmt.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Inserts a new item in the database using the SQL statement passed as argument
	 * 
	 * @param insert SQL statement to be executed
	 * @param item item to be serialize on the database
	 * @throws DatabaseException if any error occurs
	 */
	public void insert(T item) throws DatabaseException {
		// open connection
		// getting a connection from pool
		Connection connection = null;
		PreparedStatement updateStmt = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// serialize the resource in XML, use a reader because necessary in
			// clob
			StringReader reader = new StringReader(xStream.toXML(item));
			updateStmt = connection.prepareStatement(sqlContainer.getInsertStatement());
			// set resource name to key
			// gets the key if null 
			String myKey = getKey(item);
			updateStmt.setString(FIRST_FIELD, myKey);
			// set XML to clob
			updateStmt.setCharacterStream(SECOND_FIELD, reader);
			if (canBeEvicted() && getEnvictionHandler() != null){
				getEnvictionHandler().fillSQLStatement(updateStmt, item);
			}
			// executes SQL
			updateStmt.executeUpdate();
			// commit
			connection.commit();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally{
			// closes statement
			try {
				if (updateStmt != null){
					updateStmt.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Updates the resource instance by resource name, serializing resource in
	 * XML
	 * 
	 * @param update SQL statement
	 * @param item resource instance to serialize
	 * @throws DatabaseException if occurs
	 */
	public void update(T item) throws DatabaseException {
		int updatedRows = 0;
		// open connection
		// getting a connection from pool
		Connection connection = null;
		PreparedStatement updateStmt = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// serialize the resource in XML
			StringReader reader = new StringReader(xStream.toXML(item));

			updateStmt = connection.prepareStatement(sqlContainer.getUpdateStatement());
			// set XML to clob
			updateStmt.setCharacterStream(FIRST_FIELD, reader);
			// set resource name to key
			// gets the key if null 
			String myKey = getKey(item);
			updateStmt.setString(SECOND_FIELD, myKey);
			// updates 
			updateStmt.executeUpdate();
			// gets updates rows
			updatedRows = updateStmt.getUpdateCount();
			// commit
			connection.commit();
			// if rows updated are 0, EXCEPTION!!
			if (updatedRows <= 0){
				throw new SQLException("Not update! Not found "+myKey);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally{
			// closes statement
			try {
				if (updateStmt != null){
					updateStmt.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Returns all resource names (keys) in a Set object (asked by Hazelcast
	 * framework).
	 * 
	 * @param query SQL statement
	 * @return set with all keys in the table
	 * @throws SQLException if occurs
	 */
	public Set<String> getAllKeys() throws DatabaseException {
		// open connection
		// getting a connection from pool
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// creates statement and
			// executes it
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlContainer.getGetAllKeysStatement());

			// creates the set
			Set<String> allIds = new HashSet<String>();
			while (rs.next()) {
				Object o = rs.getObject(FIRST_FIELD);
				allIds.add((String) o);
				// loads all keys in a set
			}
			return allIds;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally{
			// closes statement and result set
			try {
				if (stmt != null){
					stmt.close();
				}			
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Returns all resources in a HasMap object (asked by Hazelcast framework).
	 * 
	 * @return set with all keys in the table
	 * @throws SQLException if occurs
	 */
	public Map<String, T> getAllItems() throws DatabaseException {
		return getAllItems(null);
	}
	
	/**
	 * Returns all resources in a HasMap object (asked by Hazelcast framework).
	 * 
	 * @param keys all keys must be searched inside the DB
	 * @return set with all keys in the table
	 * @throws SQLException if occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, T> getAllItems(Collection<String> keys) throws DatabaseException {
		// open connection
		// getting a connection from pool
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// creates statement and
			// executes it
			stmt = connection.createStatement();
			rs = stmt.executeQuery(createSQLforAllItems(keys));

			// creates the set
			Map<String, T> allItems = new HashMap<String, T>();
			while (rs.next()) {
				// get CLOB field which contains resource XML serialization
				T item = (T) xStream.fromXML(rs.getCharacterStream(FIRST_FIELD));
				String myKey = getKey(item);
				allItems.put(myKey, item);
			}
			return allItems;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally{
			// closes statement and result set
			try {
				if (stmt != null){
					stmt.close();
				}			
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Creates a SQL query using all keys requsted by HC.
	 * @param keys list of keys to extract
	 * @return SQL statement
	 */
	private String createSQLforAllItems(Collection<String> keys){
		if (keys != null && !keys.isEmpty()){
			// use collections of keys in string format, to create SQL
			// for IN statement, put ' and , on right position
			StringBuilder sb = new StringBuilder();
			Iterator<String> iter = keys.iterator();
			for (;;){
				String key = iter.next();
				sb.append("'").append(key).append("'");
				if (!iter.hasNext()){
					break;
				}
				sb.append(", ");
			}
			// formats SQL to get all roles by keys 
			return MessageFormat.format(sqlContainer.getGetAllStatement(), sb.toString());
		} else {
			return sqlContainer.getGetAllStatement();
		}
	}

	/**
	 * Returns a item instance store on table, by key.
	 * 
	 * @param query SQL statement
	 * @param key the key value
	 * @return instance
	 * @throws SQLException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public T getItem(String key) throws DatabaseException {
		// open connection
		// getting a connection from pool
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// creates statement
			stmt = connection.prepareStatement(sqlContainer.getGetStatement());
			// sets resource names where condition
			// checks if is a string or long (long used only for queue of HC)
			stmt.setString(FIRST_FIELD, key);
			// executes query
			rs = stmt.executeQuery();
			T item = null;

			// checks if I have the result. ONLY 1 row if expected. If more, is
			// an error because the resource name
			// is a primary key of table
			if (rs.next()) {
				// get CLOB field which contains RESOURCE XML serialization
				// deserializes RESOURCE instance
				item = (T) xStream.fromXML(rs.getCharacterStream(FIRST_FIELD));
			}
			return item;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally{
			// closes statement and result set
			try {
				if (stmt != null){
					stmt.close();
				}			
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Returns the size of Hazelcast map to check if any out of memory could risk
	 *  
	 * @return the size of resources in byte present in the map
	 * @throws SQLException if an sql exception occurs
	 */
	public long getSize() throws DatabaseException {
		// open connection
		// getting a connection from pool
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// creates statement and
			// executes it
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlContainer.getCheckQueueSizeStatement());
			rs.next();
			// returns the the first value of result sets
			// always the sum of byte of items
			return rs.getLong(FIRST_FIELD);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally{
			// closes statement and result set
			try {
				if (stmt != null){
					stmt.close();
				}			
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			// closes connection
			if (connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.AbstractDataBaseManager#checkAndCreate()
	 */
	@Override
	public void checkAndCreate() throws DatabaseException {
		try {
			// checks input
			checkAndCreateTable();
			checkAndCreateIndexes();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	/**
	 * Called to hand over the control to DB manager to create additional SQL structure (like indexes).
	 * @throws SQLException if any error occurs 
	 */
	private void checkAndCreateIndexes() {
		// gets SQL container
		SQLContainer container = getSqlContainer();
		if (container.getIndexes().isEmpty()){
			return;
		}
		// gets the DB connection from pool
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// gets metadata
			DatabaseMetaData md = connection.getMetaData();
			// gets a result set which searches for the table name
			rs = md.getIndexInfo(null, null, container.getTableName(), false, true);
			System.err.println(rs);
			// if result set is empty, it creates the table
			while(!rs.next()) {
				String indexName = rs.getString("INDEX_NAME");
				System.err.println(indexName);
				if (container.getIndexes().containsKey(indexName)){
					container.getIndexes().remove(indexName);
				}
			}
			if (!container.getIndexes().isEmpty()){
				for (String stmt : container.getIndexes().values()){
					// creates table and return a empty set because if empty of
					// course
					DBPoolManager.getInstance().create(stmt);
				}
			}
		} catch (SQLException e) {
			// FIXME
//			e.printStackTrace();
		} finally {
			// if result set is not null
			// it closes the result set
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// ignoring any exception
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
			// if connection not null
			// it closes putting again on pool
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Checks if the necessary tables exists on database. If not, it creates them.
	 * @throws SQLException if any error occurs checking the existence of tables
	 */
	private void checkAndCreateTable() throws SQLException {
		// gets the DB connection from pool
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DBPoolManager.getInstance().getConnection();
			// gets metadata
			DatabaseMetaData md = connection.getMetaData();
			// gets SQL container
			SQLContainer container = getSqlContainer();
			// gets a result set which searches for the table anme
			rs = md.getTables(null, null, container.getTableName(), new String[] { "TABLE", "ALIAS" });
			// if result set is empty, it creates the table
			if (!rs.next()) {
				// creates table and return a empty set because if empty of
				// course
				DBPoolManager.getInstance().create(container.getCreateTableStatement());
			}
		} finally {
			// if result set is not null
			// it closes the result set
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// ignoring any exception
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
			// if connection not null
			// it closes putting again on pool
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * This method must be overrided from DB managers which needds to access to database
	 * to get object. Mandatory to use when a HC map has been configured with eviction.
	 * @param filter filter to apply
	 * @return alwasy null
	 */
	String getStatementForFilter(Filter filter){
		LogAppl.getInstance().debug(filter.toString());
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DatabaseManager#loadAll(org.pepstock.jem.util.filters.Filter)
	 */
	@Override
	public final Collection<T> loadByFilter(Filter filter) throws DatabaseException {
		Collection<T> allItems = new ArrayList<T>();
		// gets SQL statements
		String stmtString = getStatementForFilter(filter);
		if (stmtString != null){
			// open connection
			// getting a connection from pool
			Connection connection = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				connection = DBPoolManager.getInstance().getConnection();
				// creates statement and
				// executes it
				stmt = connection.createStatement();
				rs = stmt.executeQuery(stmtString);

				// creates the set
				while (rs.next()) {
					// get CLOB field which contains resource XML serialization
					@SuppressWarnings("unchecked")
					T item = (T) xStream.fromXML(rs.getCharacterStream(FIRST_FIELD));
					allItems.add(item);
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally{
				// closes statement and result set
				try {
					if (stmt != null){
						stmt.close();
					}			
					if (rs != null){
						rs.close();
					}
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
				// closes connection
				if (connection != null){
					try {
						connection.close();
					} catch (SQLException e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}
		}
		return allItems;
	}
	
	
}