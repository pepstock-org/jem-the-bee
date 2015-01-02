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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.core.IMap;

/**
 * This manager will apply all redo statements when the database is back up & running.
 * It pools the database connection every 15 seconds to understand if the database is up or down.  
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class RecoveryManager {

	private static final RecoveryManager INSTANCE = new RecoveryManager();

	/**
	 * Creates the manager scheduling the timer to check
	 * if the database is up & running
	 */
	private RecoveryManager() {
		// gets the name of the class as name of the timer
		String className = FilenameUtils.getExtension(this.getClass().getName());
		// schedules the time
		Timer timer = new Timer(className, false);
		timer.schedule(new PersistenceHealthCheck(), 1, 15 * TimeUtils.SECOND);
	}

	/**
	 * @return instance
	 */
	public static RecoveryManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Applies all redo statements from Hazelcast map 
	 * @return true if apply statements
	 * @throws MessageException if any errors occurs during the apply of redo statements
	 * 
	 */
	public boolean applyRedoStatements() throws MessageException {
		// gets the HC map
		IMap<Long, RedoStatement> redoMap = Main.getHazelcast().getMap(Queues.REDO_STATEMENT_MAP);
		// locks inetrnally of JEM cluster
		Lock lock = Main.getHazelcast().getLock(Queues.REDO_STATEMENT_MAP_LOCK);
		boolean isLock = false;
		try {
			// gets a lock
			isLock = lock.tryLock(10, TimeUnit.SECONDS);
			if (isLock) {
				// if map of redo statements is empty
				// means nothing to apply
				if (redoMap.isEmpty()){
					return false;
				}
				// reads all redo statements
				List<RedoStatement> values = new ArrayList<RedoStatement>(redoMap.values());
				// sorts by ID
				// in this ways it can apply all redo statements on the right order
				// how they have been created
				Collections.sort(values, new Comparator<RedoStatement>() {
					@Override
					public int compare(RedoStatement rd0, RedoStatement rd1) {
						return rd0.getId().compareTo(rd1.getId());
					}
				});
				// scans all redo statements
				for (RedoStatement statement : values) {
					// extracts the map store of HC
					// using the queue information of redo statement
					JobMapManager mapStore = null;
					if (statement.getQueueName().equalsIgnoreCase(Queues.INPUT_QUEUE)) {
						mapStore = InputMapManager.getInstance();
					} else if (statement.getQueueName().equalsIgnoreCase(Queues.RUNNING_QUEUE)) {
						mapStore = RunningMapManager.getInstance();
					} else if (statement.getQueueName().equalsIgnoreCase(Queues.OUTPUT_QUEUE)) {
						mapStore = OutputMapManager.getInstance();
					} else if (statement.getQueueName().equalsIgnoreCase(Queues.ROUTING_QUEUE)) {
						mapStore = RoutingMapManager.getInstance();
					}
					// if action is store, it calls the store method
					// of map store
					// otherwise it calls the delete one
					if (statement.getAction().equalsIgnoreCase(RedoStatement.STORE)) {
						mapStore.store(statement.getJob().getId(), statement.getJob(), true);
					} else if (statement.getAction().equalsIgnoreCase(RedoStatement.DELETE)) {
						mapStore.delete(statement.getJobId(), true);
					}
					// remove the redo statement from HC map
					redoMap.remove(statement.getId());
					// shows the redo has been ended correctly
					LogAppl.getInstance().emit(NodeMessage.JEMC176I, statement.toString());
				}
				LogAppl.getInstance().emit(NodeMessage.JEMC177I, String.valueOf(values.size()));
				// all redo has been done correctly
				return true;
			} else {
				throw new MessageException(NodeMessage.JEMC119E, Queues.REDO_STATEMENT_MAP);
			}
		} catch (Exception e) {
			throw new MessageException(NodeMessage.JEMC119E, e, Queues.REDO_STATEMENT_MAP);
		} finally {
			// always unlock the lock on REDO
			if (isLock){
				lock.unlock();
			}
		}
	}

	/**
	 * Timer which checks if the database is up and running.
	 * If up but it was down before, it will apply
	 * all the redo statements
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.0
	 */
	class PersistenceHealthCheck extends TimerTask {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// if node is shutting down, do nothing
			if (Main.IS_SHUTTING_DOWN.get()){
				return;
			}
			// gets locks of NODE
			Lock l = Main.getNode().getLock();
			try {
				l.lock();
				// if node is working well
				if (Main.getNode().isOperational()) {
					// checks if there are some redo statements
					// if yes, it will apply all redo statements
					IMap<Long, RedoStatement> redoMap = Main.getHazelcast().getMap(Queues.REDO_STATEMENT_MAP);
					if (!redoMap.isEmpty()) {
						applyRedoStatements();
					}
					// and exit
					return;
				}
				// if node is not working
				// try to apply the redo statements
				if (applyRedoStatements()) {
					// if here, it was able to store the redo statements
					// and then the node is operational
					Main.getNode().setOperational(true);
					// saves the node info
					NodeInfoUtility.storeNodeInfo(Main.getNode());
					// starts the node
					NodeInfoUtility.start();
					LogAppl.getInstance().emit(NodeMessage.JEMC172I);
				} else {
					// if not able to apply the redo statements
					// gets all size of HC maps for jobs
					// checking if the database is working well
					// checks all tables because we could have a issue
					// on a specific table
					InputDBManager.getInstance().getSize();
					RunningDBManager.getInstance().getSize();
					OutputDBManager.getInstance().getSize();
					RoutingDBManager.getInstance().getSize();
					
					// if here, all queries went well
					// then the node is operational
					Main.getNode().setOperational(true);
					NodeInfoUtility.storeNodeInfo(Main.getNode());
					NodeInfoUtility.start();
					LogAppl.getInstance().emit(NodeMessage.JEMC172I);
				}
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC178E, e);
			} finally {
				// always unlock
				l.unlock();
			}
		}
	}
}