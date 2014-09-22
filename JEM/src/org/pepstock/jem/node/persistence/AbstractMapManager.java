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

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

import com.hazelcast.core.MapStore;

/**
 * Persistent manager for HC map.<br>
 * It uses DBManager instance to perform all sqls.<br>
 * It throws MapStoreException if the database manager has errors but Hazelcast
 * is not able to catch them, so it logs all errors.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T> Object to map this store
 * 
 */
public abstract class AbstractMapManager<T> implements MapStore<String, T> {
	
	private String queueName = null;

	private AbstractDBManager<String, T> dbManager = null;
	
	private SQLContainer sql = null;

	/**
	 * Construct the object getting a DBManager
	 * @param queueName HC queue name
	 * @param dbManager dbManager instance
	 */
	public AbstractMapManager(String queueName, AbstractDBManager<String, T> dbManager) {
		this.queueName = queueName;
		this.dbManager = dbManager;
		sql = dbManager.getSqlContainer();
	}
	
	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Loads object instance by object name passed by Hazelcast
	 * 
	 * @see com.hazelcast.core.MapLoader#load(java.lang.Object)
	 * @param key object id of object to load
	 */
	@Override
	public T load(String key) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		T object = null;
		try {
			// load object instance from table
			object = dbManager.getItem(sql.getGetStatement(), key);
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return object;
	}

	/**
	 * Loads all keys (object names) at the starting of Hazelcast
	 * 
	 * @see com.hazelcast.core.MapLoader#loadAllKeys()
	 */
	@Override
	public Set<String> loadAllKeys() {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		Set<String> set = null;
		try {
			// loadAll keys from table
			set = dbManager.getAllKeys(sql.getGetAllKeysStatement());
			LogAppl.getInstance().emit(NodeMessage.JEMC045I, String.valueOf(set.size()), getQueueName());
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return set;
	}

	/**
	 * Deletes a object instance from queue by object name
	 * 
	 * @see com.hazelcast.core.MapStore#delete(java.lang.Object)
	 */
	@Override
	public void delete(String key) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		try {
			// deletes the object in table
			dbManager.delete(sql.getDeleteStatement(), key);
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
	}

	/**
	 * Stores a object instance in map. If already exists, it updates it.
	 * 
	 * @see com.hazelcast.core.MapStore#store(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void store(String key, T object) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		try {
			// inserts the object in table
			dbManager.insert(sql.getInsertStatement(), key, object);
		} catch (SQLException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);

			// I have an exception (it happens if the key already exists, so
			// update anyway
			try {
				// updates the object in table
				dbManager.update(sql.getUpdateStatement(), key, object);
			} catch (SQLException e1) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e1);
				throw new MapStoreException(NodeMessage.JEMC043E, e1);
			}
		}
	}

	/**
	 * used when a synchronous persistence is chosen
	 * 
	 * @see com.hazelcast.core.MapStore#storeAll(java.util.Map)
	 */
	@Override
	public void storeAll(Map<String, T> objects) {
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		for (Entry<String, T> entry : objects.entrySet()){
			try {
				// inserts the object in table
				dbManager.insert(sql.getInsertStatement(), entry.getKey(), entry.getValue());
			} catch (SQLException e) {
				// ignore
				LogAppl.getInstance().ignore(e.getMessage(), e);

				// I have an exception (it happens if the key already exists, so
				// update anyway
				try {
					// updates the object in table
					dbManager.update(sql.getUpdateStatement(), entry.getKey(), entry.getValue());
				} catch (SQLException e1) {
					LogAppl.getInstance().emit(NodeMessage.JEMC043E, e1);
					throw new MapStoreException(NodeMessage.JEMC043E, e1);
				}
			}
		}
	}

	/**
	 * used when a synchronous persistence is chosen
	 * 
	 * @see com.hazelcast.core.MapStore#deleteAll(java.util.Collection)
	 */
	@Override
	public void deleteAll(Collection<String> ids) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
		for (String id : ids){
			try {
				// deletes the object in table
				dbManager.delete(sql.getDeleteStatement(), id);
			} catch (SQLException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				throw new MapStoreException(NodeMessage.JEMC043E, e);
			}
		}
	}

	/**
	 * Loads all objects saved, by a list of keys.
	 * 
	 * @see com.hazelcast.core.MapLoader#loadAll(java.util.Collection)
	 * @param collaction of keys to load
	 * @return maps with all roles
	 */
	@Override
	public Map<String, T> loadAll(Collection<String> keys) {
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
			throw new MapStoreException(NodeMessage.JEMC044E);
		}
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
		String sqlString = MessageFormat.format(sql.getGetAllStatement(), sb.toString());
		
		Map<String, T> objects = null;
		try {
			// load object instance from table
			objects = dbManager.getAllItems(sqlString);
			LogAppl.getInstance().emit(NodeMessage.JEMC055I, String.valueOf(objects.size()), getQueueName());
		} catch (SQLException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			throw new MapStoreException(NodeMessage.JEMC043E, e);
		}
		return objects;
	}
}