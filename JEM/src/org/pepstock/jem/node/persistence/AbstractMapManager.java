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

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.persistence.database.AbstractDBManager;

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
public abstract class AbstractMapManager<T> implements MapStore<String, T>, Recoverable {
	
	private String queueName = null;

	private AbstractDBManager<T> dbManager = null;
	
	private SQLContainer sql = null;
	
	private RedoManager<T> redoManager = null;

	/**
	 * Construct the object getting a DBManager
	 * @param queueName HC queue name
	 * @param dbManager dbManager instance
	 * @param recovery if the map must use redo in case of DB failure
	 */
	public AbstractMapManager(String queueName, AbstractDBManager<T> dbManager, boolean recovery) {
		this.queueName = queueName;
		this.dbManager = dbManager;
		sql = dbManager.getSqlContainer();
		if (recovery){
			this.redoManager = new RedoManager<T>(queueName);
		}
	}
	
	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @return the redoManager
	 */
	public RedoManager<T> getRedoManager() {
		return redoManager;
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
				object = dbManager.getItem(sql.getGetStatement(), key);
			} catch (SQLException e) {
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
				set = dbManager.getAllKeys(sql.getGetAllKeysStatement());
				LogAppl.getInstance().emit(NodeMessage.JEMC045I, String.valueOf(set.size()), getQueueName());
			} catch (SQLException e) {
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
	 * @return maps with all roles
	 */
	@Override
	public Map<String, T> loadAll(Collection<String> keys) {
		Map<String, T> objects = null;
		// check if I have the database manager, otherwise log error and
		// exception
		if (dbManager == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
		} else {
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
			try {
				// load object instance from table
				objects = dbManager.getAllItems(sqlString);
				LogAppl.getInstance().emit(NodeMessage.JEMC055I, String.valueOf(objects.size()), getQueueName());
			} catch (SQLException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
			}
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
//		// check if I have the database manager, otherwise log error and
//		// exception
//		if (dbManager == null) {
//			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
//		} else {
//			try {
//				// deletes the object in table
//				dbManager.delete(sql.getDeleteStatement(), key);
//			} catch (SQLException e) {
//				LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
//			}
//		}
	}
	
	/**
	 * used when a synchronous persistence is chosen
	 * 
	 * @see com.hazelcast.core.MapStore#deleteAll(java.util.Collection)
	 */
	@Override
	public void deleteAll(Collection<String> ids) {
		// scans all job ids and
		// deletes the jobs
		for (String id : ids){
			delete(id);
		}
//		// check if I have the database manager, otherwise log error and
//		// exception
//		if (dbManager == null) {
//			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
//		} else {
//			for (String id : ids){
//				try {
//					// deletes the object in table
//					dbManager.delete(sql.getDeleteStatement(), id);
//				} catch (SQLException e) {
//					LogAppl.getInstance().emit(NodeMessage.JEMC043E, e);
//				}
//			}
//		}
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
//		// check if I have the database manager, otherwise log error and
//		// exception
//		if (dbManager == null) {
//			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
//		} else {
//			try {
//				// inserts the object in table
//				dbManager.insert(sql.getInsertStatement(), key, object);
//			} catch (SQLException e) {
//				// ignore
//				LogAppl.getInstance().ignore(e.getMessage(), e);
//
//				// I have an exception (it happens if the key already exists, so
//				// update anyway
//				try {
//					// updates the object in table
//					dbManager.update(sql.getUpdateStatement(), key, object);
//				} catch (SQLException e1) {
//					LogAppl.getInstance().emit(NodeMessage.JEMC043E, e1);
//				}
//			}
//		}
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


//		if (dbManager == null) {
//			LogAppl.getInstance().emit(NodeMessage.JEMC044E);
//		} else {
//			for (Entry<String, T> entry : objects.entrySet()){
//				try {
//					// inserts the object in table
//					dbManager.insert(sql.getInsertStatement(), entry.getKey(), entry.getValue());
//				} catch (SQLException e) {
//					// ignore
//					LogAppl.getInstance().ignore(e.getMessage(), e);
//
//					// I have an exception (it happens if the key already exists, so
//					// update anyway
//					try {
//						// updates the object in table
//						dbManager.update(sql.getUpdateStatement(), entry.getKey(), entry.getValue());
//					} catch (SQLException e1) {
//						LogAppl.getInstance().emit(NodeMessage.JEMC043E, e1);
//					}
//				}
//			}
//		}
//	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void recover(RedoStatement statement) throws Exception{
		// if action is store, it calls the store method
		// of map store
		// otherwise it calls the delete one
		if (statement.getAction().equalsIgnoreCase(RedoStatement.STORE)) {
			store((T)statement.getEntity(), true);
		} else if (statement.getAction().equalsIgnoreCase(RedoStatement.DELETE)) {
			delete(statement.getEntityId(), true);
		}
	}

	/**
	 * Deletes the jobs by jobs ID. Accepts also if an exception must be thrown or not.
	 * This is done because the same method is called both from normal persistence and
	 * from recovery manager to apply the redo statements.
	 * @param id job id to be deleted
	 * @param exception if <code>true</code>, it will throw an exception if any errors occurs
	 */
	private void delete(String id, boolean exception) {
		try {
			// deletes the job in table
			dbManager.delete(sql.getDeleteStatement(), id);
			// if you don't want the exception
			// means it has been called by recovery manager
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (SQLException e) {
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
				// if here, it was able to delete jobs
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
	 * @param id job id to delete
	 * @param e exception occurred
	 */
	private void recoverDeleteStatement(String id, Exception e){
		// if not redo, return
		if (redoManager == null){
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
	 * Stores the job by jobs ID. Accepts also if an exception must be thrown or not.
	 * This is done because the same method is called both from normal persistence and
	 * from recovery manager to apply the redo statements.
	 * @param jobid job id of job instance
	 * @param entity job instance
	 * @param exception if <code>true</code>, it will throw an exception if any errors occurs 
	 */
	private void store(T entity, boolean exception) {
		try {
			// inserts the job in table
			dbManager.insert(sql.getInsertStatement(), entity);
			// if you don't want the exception
			// means it has been called by recovery manager
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (SQLException e1) {
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
	 * Here it tries to update the job object. If it doesn't work, it will recover the statement
	 * moving it on the REDO ones.
	 * 
	 * @param entity job instance to be updated
	 * @param exception if the exception must be thrown or not.
	 */
	private void tryToUpdate(T entity,  boolean exception){
		// I have an exception (it happens if the key already exists, so
		// update anyway
		try {
			// updates the job in table
			dbManager.update(sql.getUpdateStatement(), entity);
			// if exception and you want to redo statement, it throws the exception
			if (!exception){
				// checks and resets of attributes of node
				checkNodeStatus();
			}
		} catch (SQLException e3) {
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
	 * @param entity job instance to store
	 * @param e exception occurred
	 */
	private void recoverStoreStatement(T entity, Exception e){
		// if not redo, return
		if (redoManager == null){
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