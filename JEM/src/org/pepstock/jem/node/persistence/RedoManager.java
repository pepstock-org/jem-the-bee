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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;

import com.hazelcast.core.IMap;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class RedoManager {

	private String queueName = null;

	/**
	 * @param queueName
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
	 * 
	 * @param queueName
	 * @param job
	 */
	public void store(Job job) {
		storeRedoStatement(job.getId(), job, RedoStatement.STORE);
	}

	/**
	 * 
	 * @param queueName
	 * @param jobId
	 */
	public void delete(String jobId) {
		storeRedoStatement(jobId, null, RedoStatement.DELETE);
	}

	/**
	 * Stores the redo statements in HC map
	 * @param jobId job id used only if is a DELETE
	 * @param job job instance used only if STORE
	 * @param what type of opertaion, or STORE or DELETE
	 */
	private void storeRedoStatement(String jobId, Job job, String what){
		IMap<Long, RedoStatement> redoMap = Main.getHazelcast().getMap(Queues.REDO_STATEMENT_MAP);
		Lock lock = Main.getHazelcast().getLock(Queues.REDO_STATEMENT_MAP_LOCK);
		boolean isLock = false;
		try {
			isLock = lock.tryLock(10, TimeUnit.SECONDS);
			if (isLock) {
				Long id = Long.valueOf(redoMap.size() + 1);
				RedoStatement statement = new RedoStatement();
				statement.setId(id);
				statement.setQueueName(queueName);
				if (RedoStatement.DELETE.equalsIgnoreCase(what)){
					statement.setJobId(jobId);	
				} else if (RedoStatement.STORE.equalsIgnoreCase(what)){
					statement.setJob(job);
				} else {
					throw new MessageException(NodeMessage.JEMC180E);
				}
				statement.setAction(what);
				redoMap.put(id, statement);
				LogAppl.getInstance().emit(NodeMessage.JEMC179I, statement.toString());
			} else {
				throw new MessageException(NodeMessage.JEMC119E, Queues.REDO_STATEMENT_MAP);
			}
		} catch (MessageException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC180E, e);
		} catch (Exception e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC119E, e);
		} finally {
			if(isLock){
				lock.unlock();
			}
		}
	}
}