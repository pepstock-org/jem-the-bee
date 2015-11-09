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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.pepstock.jem.util.filters.Filter;

/**
 * Manages all SQL statements towards the database to persist the Hazelcast items.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * @param <String> Key of table
 * @param <T> object stored in Hazelcast map 
 * 
 */
public interface DatabaseManager<T>{
	
	/**
	 * Returns the Hazelcast queue name
	 * @return  the Hazelcast queue name
	 */
	String getQueueName();
	
	/**
	 * Returns for a key fields for object
	 * @param item instance used to get key
	 * @return key to use on database table
	 */
	abstract String getKey(T item);

	/**
	 * Deletes an instance from queue by the key of table
	 * 
	 * @param key instance key
	 * @throws DatabaseException if occurs
	 */
	void delete(String key) throws DatabaseException;
	/**
	 * Inserts a new item in the database 
	 * 
	 * @param item item to be serialize on the database
	 * @throws DatabaseException if any error occurs
	 */
	void insert(T item) throws DatabaseException ;
	
	/**
	 * Updates the resource instance by resource name
	 * 
	 * @param item resource instance to serialize
	 * @throws DatabaseException if occurs
	 */
	void update(T item) throws DatabaseException;
	
	/**
	 * Returns all resource names (keys) in a Set object (asked by Hazelcast
	 * framework).
	 * 
	 * @return set with all keys in the table
	 * @throws DatabaseException if occurs
	 */
	Set<String> getAllKeys() throws DatabaseException;
	/**
	 * Returns all resources in a HasMap object (asked by Hazelcast framework).
	 * 
	 * @return set with all keys in the table
	 * @throws DatabaseException if occurs
	 */
	Map<String, T> getAllItems() throws DatabaseException;
	/**
	 * Returns all resources in a HasMap object (asked by Hazelcast framework).
	 * 
	 * @param keys all keys must be searched inside the DB
	 * @return set with all keys in the table
	 * @throws DatabaseException if occurs
	 */
	Map<String, T> getAllItems(Collection<String> keys) throws DatabaseException;

	/**
	 * Returns a item instance store on table, by key.
	 * 
	 * @param key the key value
	 * @return instance
	 * @throws DatabaseException if any exception occurs
	 */
	T getItem(String key) throws DatabaseException;
	/**
	 * Returns the size of Hazelcast map to check if any out of memory could risk
	 *  
	 * @return the size of resources in byte present in the map
	 * @throws DatabaseException if an exception occurs
	 */
	long getSize() throws DatabaseException;
	
	/**
	 * Checks if table exists and creates it if necessary
	 * @throws DatabaseException if any error occurs
	 */
	void checkAndCreate() throws DatabaseException;
	
	/**
	 * Returns true if the HC map can be evicted otherwise false.
	 * @return true if the HC map can be evicted otherwise false
	 */
	boolean canBeEvicted();
	
	/**
	 * Load data from database when eviction is activated
	 * @param filter filter to apply to database
	 * @return collection of objects
	 * @throws DatabaseException if any errors occurs
	 */
	Collection<T> loadByFilter(Filter filter) throws DatabaseException;
}