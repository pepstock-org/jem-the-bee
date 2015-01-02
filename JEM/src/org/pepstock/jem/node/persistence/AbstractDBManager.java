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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pepstock.jem.log.LogAppl;

import com.thoughtworks.xstream.XStream;

/**
 * Manages all SQL statements towards the database to persist the Hazelcast items.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * @param <K> Key of table
 * @param <T> object stored in Hazelcast map 
 * 
 */
public abstract class AbstractDBManager<K, T>{

	private XStream xStream = null;
	
	private SQLContainer sqlContainer = null;

	/**
	 * Standard constructor which creates a Xstream instance
	 */
	AbstractDBManager() {
		this(new XStream());
	}

	/**
	 * Creates the object using the XStream instance 
	 * @param xs XStream instance
	 */
	AbstractDBManager(XStream xs) {
		xStream = xs;
	}

	/**
	 * @return the sqlContainer
	 */
	public SQLContainer getSqlContainer() {
		return sqlContainer;
	}

	/**
	 * @param sqlContainer the sqlContainer to set
	 */
	public void setSqlContainer(SQLContainer sqlContainer) {
		this.sqlContainer = sqlContainer;
	}
	
	/**
	 * Returns for a key fields for object
	 * @param item instance used to get key
	 * @return key to use on database table
	 */
	public abstract K getKey(T item);

	/**
	 * Deletes an instance from queue by the key of table
	 * 
	 * @param delete SQL statement to delete
	 * @param key instance key
	 * @throws SQLException if occurs
	 */
	public void delete(String delete, K key) throws SQLException {
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		PreparedStatement updateStmt = null;
		try {
			updateStmt = connection.prepareStatement(delete);
			// set resource name in prepared statement
			// checks if is a string or long (long used only for queue of HC)
			if (key instanceof String){
				updateStmt.setString(1, (String)key);
			} else {
				updateStmt.setLong(1, (Long)key);
			}
			// executes the statement
			updateStmt.executeUpdate();
			// commit
			connection.commit();
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
				connection.close();
			}
		}
	}

	/**
	 * Inserts a new item in the database using the SQL statement passed as argument
	 * 
	 * @param insert SQL statement to be executed
	 * @param item item to be serialize on the database
	 * @throws SQLException if any error occurs
	 */
	public void insert(String insert, T item) throws SQLException {
		insert(insert, null, item);
	}
	
	/**
	 * Inserts a item in table, serializing resource in XML
	 * 
	 * @param insert SQL statement to be executed
	 * @param key key to be used to add item or null
	 * @param item instance to add
	 * @throws SQLException if occurs
	 */
	public void insert(String insert, K key, T item) throws SQLException {
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		PreparedStatement updateStmt = null;
		try {
			// serialize the resource in XML, use a reader because necessary in
			// clob
			StringReader reader = new StringReader(xStream.toXML(item));
			updateStmt = connection.prepareStatement(insert);
			// set resource name to key
			// gets the key if null 
			K myKey = (key == null) ? getKey(item) : key;
			// checks if is a string or long (long used only for queue of HC)
			if (myKey instanceof String){
				updateStmt.setString(1, (String)myKey);
			} else {
				updateStmt.setLong(1, (Long)myKey);
			}
			// set XML to clob
			updateStmt.setCharacterStream(2, reader);
			// executes SQL
			updateStmt.executeUpdate();
			// commit
			connection.commit();
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
				connection.close();
			}
		}
	}

	/**
	 * Updates the resource instance by resource name, serializing resource in
	 * XML
	 * 
	 * @param update SQL statement
	 * @param item resource instance to serialize
	 * @throws SQLException if occurs
	 */
	public void update(String update, T item) throws SQLException {
		update(update, null, item);
	}
	/**
	 * Updates the resource instance by resource name, serializing resource in
	 * XML
	 * 
	 * @param update SQL statement
	 * @param key key to be used to update item or null
	 * @param item resource instance to serialize
	 * @throws SQLException if occurs
	 */
	public void update(String update, K key, T item) throws SQLException {
		int updatedRows = 0;
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		PreparedStatement updateStmt = null;
		try {
			// serialize the resource in XML
			StringReader reader = new StringReader(xStream.toXML(item));

			updateStmt = connection.prepareStatement(update);
			// set XML to clob
			updateStmt.setCharacterStream(1, reader);
			// set resource name to key
			// gets the key if null 
			K myKey = (key == null) ? getKey(item) : key;
			// checks if is a string or long (long used only for queue of HC)
			if (myKey instanceof String){
				updateStmt.setString(2, (String)myKey);
			} else {
				updateStmt.setLong(2, (Long)myKey);
			}
			// updates 
			updateStmt.executeUpdate();
			// gets updates rows
			updatedRows = updateStmt.getUpdateCount();
			// commit
			connection.commit();
			// if rows updated are 0, EXCEPTION!!
			if (updatedRows <= 0){
				throw new SQLException("Not update! Not found "+key);
			}
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
				connection.close();
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
	@SuppressWarnings("unchecked")
	public Set<K> getAllKeys(String query) throws SQLException {
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// creates statement and
			// executes it
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			// creates the set
			Set<K> allIds = new HashSet<K>();
			while (rs.next()) {
				Object o = rs.getObject(1);
				allIds.add((K) o);
				// loads all keys in a set
			}
			return allIds;
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
				connection.close();
			}
		}
	}

	/**
	 * Returns all resources in a HasMap object (asked by Hazelcast framework).
	 * 
	 * @param query SQL statement (well formatted previously by caller)
	 * @return set with all keys in the table
	 * @throws SQLException if occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<K, T> getAllItems(String query) throws SQLException {
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// creates statement and
			// executes it
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			// creates the set
			Map<K, T> allItems = new HashMap<K, T>();
			while (rs.next()) {
				// get CLOB field which contains resource XML serialization
				T item = (T) xStream.fromXML(rs.getCharacterStream(1));
				
				// uses 1 column has object. The key is the second one, if exists
				if (rs.getMetaData().getColumnCount() > 1){
					Object o = rs.getObject(2);
					allItems.put((K) o, item);
				} else {
					allItems.put(getKey(item), item);
				}
			}
			return allItems;
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
				connection.close();
			}
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
	public T getItem(String query, K key) throws SQLException {
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// creates statement
			stmt = connection.prepareStatement(query);
			// sets resource names where condition
			// checks if is a string or long (long used only for queue of HC)
			if (key instanceof String){
				stmt.setString(1, (String)key);
			} else {
				stmt.setLong(1, (Long)key);
			}
			// executes query
			rs = stmt.executeQuery();
			T item = null;

			// checks if I have the result. ONLY 1 row if expected. If more, is
			// an error because the resource name
			// is a primary key of table
			if (rs.next()) {
				// get CLOB field which contains RESOURCE XML serialization
				// deserializes RESOURCE instance
				item = (T) xStream.fromXML(rs.getCharacterStream(1));
			}
			return item;
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
				connection.close();
			}
		}
	}

	/**
	 * Returns the size of Hazelcast map to check if any out of memory could risk
	 *  
	 * @return the size of resources in byte present in the map
	 * @throws SQLException if an sql exception occurs
	 */
	public long getSize() throws SQLException {
		// open connection
		// getting a connection from pool
		Connection connection = DBPoolManager.getInstance().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// creates statement and
			// executes it
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlContainer.getCheckQueueSizeStatement());
			rs.next();
			// returns the the first value of result sets
			// always the sum of byte of items
			return rs.getLong(1);
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
				connection.close();
			}
		}
	}
}