/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class RecoveryManager {

	private static final RecoveryManager INSTANCE = new RecoveryManager();

	/**
	 * @param queueName
	 * 
	 */
	private RecoveryManager() {
		String className = FilenameUtils.getExtension(this.getClass().getName());
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
	 * @return true if apply statements
	 * @throws MessageException
	 * 
	 */
	public boolean applyRedoStatements() throws MessageException {
		IMap<Long, RedoStatement> redoMap = Main.getHazelcast().getMap(Queues.REDO_STATEMENT_MAP);
		Lock lock = Main.getHazelcast().getLock(Queues.REDO_STATEMENT_MAP_LOCK);
		boolean isLock = false;
		try {
			isLock = lock.tryLock(10, TimeUnit.SECONDS);
			if (isLock) {
				if (redoMap.isEmpty()){
					return false;
				}

				List<RedoStatement> values = new ArrayList<RedoStatement>(redoMap.values());
				Collections.sort(values, new Comparator<RedoStatement>() {
					@Override
					public int compare(RedoStatement rd0, RedoStatement rd1) {
						return rd0.getId().compareTo(rd1.getId());
					}
				});

				for (RedoStatement statement : values) {
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

					if (statement.getAction().equalsIgnoreCase(RedoStatement.STORE)) {
						mapStore.store(statement.getJob().getId(), statement.getJob(), true);
					} else if (statement.getAction().equalsIgnoreCase(RedoStatement.DELETE)) {
						mapStore.delete(statement.getJobId(), true);
					}
					redoMap.remove(statement.getId());
					LogAppl.getInstance().emit(NodeMessage.JEMC176I, statement.toString());
				}
				LogAppl.getInstance().emit(NodeMessage.JEMC177I, String.valueOf(values.size()));
				return true;
			} else {
				throw new MessageException(NodeMessage.JEMC119E, Queues.REDO_STATEMENT_MAP);
			}
		} catch (Exception e) {
			throw new MessageException(NodeMessage.JEMC119E, e, Queues.REDO_STATEMENT_MAP);
		} finally {
			if (isLock){
				lock.unlock();
			}
		}
	}

	class PersistenceHealthCheck extends TimerTask {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			if (Main.IS_SHUTTING_DOWN.get()){
				return;
			}
			Lock l = Main.getNode().getLock();
			try {
				l.lock();
				if (Main.getNode().isOperational()) {
					IMap<Long, RedoStatement> redoMap = Main.getHazelcast().getMap(Queues.REDO_STATEMENT_MAP);
					if (!redoMap.isEmpty()) {
						applyRedoStatements();
					}
					return;
				}

				if (applyRedoStatements()) {
					Main.getNode().setOperational(true);
					NodeInfoUtility.storeNodeInfo(Main.getNode());
					NodeInfoUtility.start();
					LogAppl.getInstance().emit(NodeMessage.JEMC172I);
				} else {
					InputDBManager.getInstance().getSize();
					RunningDBManager.getInstance().getSize();
					OutputDBManager.getInstance().getSize();
					RoutingDBManager.getInstance().getSize();

					Main.getNode().setOperational(true);
					NodeInfoUtility.storeNodeInfo(Main.getNode());
					NodeInfoUtility.start();
					LogAppl.getInstance().emit(NodeMessage.JEMC172I);
				}

			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC178E, e);
			} finally {
				l.unlock();
			}
		}

	}

}