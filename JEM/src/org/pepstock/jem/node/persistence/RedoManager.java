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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.hazelcast.Locks;
import org.pepstock.jem.node.hazelcast.Queues;

/**
 * This is the manager which is in charge to save on Hazelcast all redo statements, during the connection broken with the database
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * @param <T> Type of object to save for REDO
 * 
 */
public class RedoManager<T> {

	private String queueName = null;

	/**
	 * Creates the object saving the HC queue name of redo statements
	 * @param queueName Hazelcast queue name
	 * 
	 */
	public RedoManager(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Saves the statement on internal Hazelcast map
	 * @param entity entity instance to be stored
	 */
	public void store(T entity) {
		storeRedoStatement(null, entity, RedoStatement.STORE);
	}

	/**
	 * Saves a delete redo statement on internal Hazelcast map
	 * @param id id to be removed
	 */
	public void delete(String id) {
		storeRedoStatement(id, null, RedoStatement.DELETE);
	}

	/**
	 * Stores the redo statements in HC map
	 * @param id id used only if is a DELETE
	 * @param entity instance used only if STORE
	 * @param what type of operation, or STORE or DELETE
	 */
	private void storeRedoStatement(String id, T entity, String what){
		// gets hazelcast map
		Map<Long, RedoStatement> redoMap = Main.getHazelcast().getReplicatedMap(Queues.REDO_STATEMENT_MAP);
		// gets a lock to avoid
		// that other nodes access to the same resource
		Lock lock = Main.getHazelcast().getLock(Locks.REDO_STATEMENT_MAP);
		boolean isLock = false;
		try {
			// gets a lock
			isLock = lock.tryLock(Locks.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock) {
				// creates a redo statement using a counter as ID
				Long redoid = Long.valueOf(redoMap.size() + 1);
				RedoStatement statement = new RedoStatement();
				// sets redo information to re-apply the statement when 
				// the database will be up and running
				statement.setId(redoid);
				statement.setQueueName(queueName);
				// for delete saved the ID
				// otherwise the entity itself
				if (RedoStatement.DELETE.equalsIgnoreCase(what)){
					statement.setEntityId(id);	
				} else if (RedoStatement.STORE.equalsIgnoreCase(what)){
					statement.setEntity(entity);
					statement.setEntityToString(entity.toString());
				} else {
					throw new MessageException(NodeMessage.JEMC180E);
				}
				// sets action
				statement.setAction(what);
				// puts in the map
				redoMap.put(redoid, statement);
				LogAppl.getInstance().emit(NodeMessage.JEMC179I, statement.toString());
			} else {
				throw new MessageException(NodeMessage.JEMC119E, Queues.REDO_STATEMENT_MAP);
			}
		} catch (MessageException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC180E, e);
		} catch (Exception e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC119E, e);
		} finally {
			// always unlock 
			if(isLock){
				lock.unlock();
			}
		}
	}
}