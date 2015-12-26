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
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.hazelcast.ConfigFactory;
import org.pepstock.jem.node.hazelcast.ConfigProvider;
import org.pepstock.jem.util.filters.Filter;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueueConfig;
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
public abstract class AbstractMapManager<T> implements MapStore<String, T>, Recoverable, ConfigProvider {

	private DatabaseManager<T> dbManager = null;
	
	private RedoManager<T> redoManager = null;

	/**
	 * Construct the object getting a DBManager
	 * @param queueName HC queue name
	 * @param dbManager dbManager instance
	 * @param recovery if the map must use redo in case of DB failure
	 */
	public AbstractMapManager(DatabaseManager<T> dbManager, boolean recovery) {
		this.dbManager = dbManager;
		if (recovery){
			this.redoManager = new RedoManager<T>(dbManager.getQueueName());
		}
	}
	
	/**
	 * @return the queueName
	 */
	public boolean canBeEvicted() {
		return dbManager.canBeEvicted();
	}
	
	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return dbManager.getQueueName();
	}

	/**
	 * @return the redoManager
	 */
	public RedoManager<T> getRedoManager() {
		return redoManager;
	}
	
	/**
	 * Returns the Hazelcast configuration for this map
	 * @return the Hazelcast configuration for this map
	 */
	public MapConfig getMapConfig(){
		return ConfigFactory.createMapConfig(getQueueName(), this);
	}
	
	/**
	 * Returns alwasy null
	 */
	public QueueConfig getQueueConfig(){
		return null;
	}

	/**
	 * Returns if this map must entry into REDO cycles
	 * @return true if must be in redo cycle, otherwise false
	 */
	public boolean hasToRecover(){
		return redoManager != null;
	}
	
	/**
	 * Queries on DB to get the estimated size of map
	 * @return total amount of bytes 
	 * @throws DatabaseException if any DB error occurs
	 */
	public long getSize() throws DatabaseException{
		return dbManager.getSize();
	}
	
	/**
	 * Loads object instance by object name passed by Hazelcast
	 * 
	 * @see com.hazelcast.core.MapLoader#load(java.lang.Object)
	 * @param key object id of object to load
	 */
	@Override
	public T load(String key) {
		T object = null;
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
		} else{
			try {
				// load object instance from table
				object = dbManager.getItem(key);
			} catch (DatabaseException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			}
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
		Set<String> set = null;
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
		} else {
			try {
				// loadAll keys from table
				set = dbManager.getAllKeys();
				if (set != null){
					LogAppl.getInstance().emit(NodeMessage.JEMC045I, String.valueOf(set.size()), getQueueName());
				}
			} catch (DatabaseException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			}
		}
		return set;
	}

	/**
	 * Loads all objects saved, by a list of keys.
	 * 
	 * @see com.hazelcast.core.MapLoader#loadAll(java.util.Collection)
	 * @param collaction of keys to load
	 * @return maps with all objects
	 */
	@Override
	public Map<String, T> loadAll(Collection<String> keys) {
		Map<String, T> objects = null;
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
		} else {
			try {
				// load object instance from table
				objects = dbManager.getAllItems(keys);
			} catch (DatabaseException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			}
		}
		return objects;
	}

	
	/**
	 * Loads all objects saved, by a list of keys.
	 * 
	 * @see com.hazelcast.core.MapLoader#loadAll(java.util.Collection)
	 * @param filter filter to apply to database
	 * @return collection with all objects
	 * @throws DatabaseException if any errors occurs
	 */
	public Collection<T> loadByFilter(Filter filter) throws DatabaseException {
		Collection<T> objects = null;
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
		} else {
			// load object instance from table
			objects = dbManager.loadByFilter(filter);
		}
		return objects;
	}
	
	/**
	 * Deletes a object instance from queue by object name
	 * 
	 * @see com.hazelcast.core.MapStore#delete(java.lang.Object)
	 */
	@Override
	public void delete(String key) {
		delete(key, false);
	}
	
	/**
	 * used when a synchronous persistence is chosen
	 * 
	 * @see com.hazelcast.core.MapStore#deleteAll(java.util.Collection)
	 */
	@Override
	public void deleteAll(Collection<String> ids) {
		// scans all ids and
		// deletes the entities by ids
		for (String id : ids){
			delete(id);
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
		store(object, false);
	}

	/**
	 * used when a synchronous persistence is chosen
	 * 
	 * @see com.hazelcast.core.MapStore#storeAll(java.util.Map)
	 */
	@Override
	public void storeAll(Map<String, T> objects) {
		for (Entry<String, T> entry : objects.entrySet()){
			store(entry.getKey(), entry.getValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void recover(RedoStatement statement) throws JemException{
		try {
			// if action is store, it calls the store method
			// of map store
			// otherwise it calls the delete one
			if (statement.getAction().equalsIgnoreCase(RedoStatement.STORE)) {
				store((T)statement.getEntity(), true);
			} else if (statement.getAction().equalsIgnoreCase(RedoStatement.DELETE)) {
				delete(statement.getEntityId(), true);
			}
		} catch (Exception e) {
			throw new JemException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.Recoverable#check()
	 */
	@Override
	public void check() throws JemException {
		try {
			dbManager.getSize();
		} catch (DatabaseException e) {
			throw new JemException(e);
		}
	}

	/**
	 * Checks if persistence objects are available and creates that if not
	 * @throws DatabaseException if any DB 
	 */
	public void checkAndCreate() throws DatabaseException {
		dbManager.checkAndCreate();
	}
	
	/**
	 * Deletes the entities by  ID. Accepts also if an exception must be thrown or not.
	 * This is done because the same method is called both from normal persistence and
	 * from recovery manager to apply the redo statements.
	 * @param id id to be deleted
	 * @param exception if <code>true</code>, it will throw an exception if any errors occurs
	 */
	private void delete(String id, boolean exception) {
		try {
			// deletes the entity in table
			dbManager.delete(id);
			// if you don't want the exception
			// means it has been called by recovery manager
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (DatabaseException e) {
			// if exception, it throws the exception
			if (exception) {
				throw new MessageRuntimeException(NodeMessage.JEMC043E, e);
			} else {
				// recovers the delete statement moving on redo ones 
				recoverDeleteStatement(id, e);
			}
		}
	}

	/**
	 * Changes the attributes of node when SQL statement
	 * ended correctly.
	 * <br>
	 * For this reason, it checks here 
	 * if the node was not operational because now it works.
	 * <br>
	 * And then applies all redo statements and changes the node
	 * status.
	 */
	private void checkNodeStatus(){
		// locks the node
		Lock l = Main.getNode().getLock();
		try {
			l.lock();
			// if not operational
			if (!Main.getNode().isOperational()){
				// tries to apply the redo statements however
				RecoveryManager.getInstance().applyRedoStatements();
				// if here, it was able to delete entities
				Main.getNode().setOperational(true);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				NodeInfoUtility.start();
				LogAppl.getInstance().emit(NodeMessage.JEMC172I);
			}
		} catch (Exception e) {
			// here I've got the exception
			LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
		} finally {
			// always unlock
			l.unlock();
		}
	}
	
	/**
	 * It recovers the delete statement saving the statement on the REDO ones.
	 * Also checks the status of the node because if it's up and running, this is
	 * wrong because it wasn't able to access to database.
	 * <br>
	 * For this reason, changes the status of the node.
	 * 
	 * @param id id to delete
	 * @param e exception occurred
	 */
	private void recoverDeleteStatement(String id, Exception e){
		// if not redo, return
		if (!hasToRecover()){
			return;
		}
		// locks node
		Lock l = Main.getNode().getLock();
		try {
			l.lock();
			// gives the delete statement to redo manager
			// to save internally
			redoManager.delete(id);
			if (Main.getNode().isOperational()){
				// if operation, changes the status of the node
				// putting the node in drain
				// because the database is not reachable
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				Main.getNode().setOperational(false);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				NodeInfoUtility.drain();
				LogAppl.getInstance().emit(NodeMessage.JEMC173E);						
			}
		} finally {
			// always unlock
			l.unlock();
		}
	}
	
	/**
	 * Stores the entities. Accepts also if an exception must be thrown or not.
	 * This is done because the same method is called both from normal persistence and
	 * from recovery manager to apply the redo statements.
	 * @param entity entity instance
	 * @param exception if <code>true</code>, it will throw an exception if any errors occurs 
	 */
	private void store(T entity, boolean exception) {
		try {
			// inserts the entity in table
			dbManager.insert(entity);
			// if you don't want the exception
			// means it has been called by recovery manager
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (DatabaseException e1) {
			// ignore
			LogAppl.getInstance().ignore(e1.getMessage(), e1);
			// I have an exception (it happens if the key already exists, so
			// update anyway
			tryToUpdate(entity, exception);
		}
	}
	
	/**
	 * This method is called when an INSERT statement went wrong (because the key is already on database).
	 * <br>
	 * Here it tries to update the object. If it doesn't work, it will recover the statement
	 * moving it on the REDO ones.
	 * 
	 * @param entity instance to be updated
	 * @param exception if the exception must be thrown or not.
	 */
	private void tryToUpdate(T entity,  boolean exception){
		// I have an exception (it happens if the key already exists, so
		// update anyway
		try {
			// updates the in table
			dbManager.update(entity);
			// if exception and you want to redo statement, it throws the exception
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (DatabaseException e3) {
			// if exception, it throws the exception
			if (exception) {
				throw new MessageRuntimeException(NodeMessage.JEMC043E, e3);
			} else {
				// recovers the store statement moving on redo ones
				recoverStoreStatement(entity, e3);
			}
		}
	}
	/**
	 * It recovers the store statement saving the statement on the REDO ones.
	 * Also checks the status of the node because if it's up and running, this is
	 * wrong because it wasn't able to access to database.
	 * <br>
	 * For this reason, changes the status of the node.
	 * 
	 * @param entity instance to store
	 * @param e exception occurred
	 */
	private void recoverStoreStatement(T entity, Exception e){
		// if not redo, return
		if (!hasToRecover()){
			return;
		}
		// gets node lock
		Lock l = Main.getNode().getLock();
		try {
			l.lock();
			// gives the store statement to redo manager
			// to save internally
			redoManager.store(entity);
			if (Main.getNode().isOperational()){
				// if operation, changes the status of the node
				// putting the node in drain
				// because the database is not reachable
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
				Main.getNode().setOperational(false);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				NodeInfoUtility.drain();
				LogAppl.getInstance().emit(NodeMessage.JEMC173E);	
			}
		} finally {
			// always unlock
			l.unlock();
		}		
	}
}