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
package org.pepstock.jem.node;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.persistence.DBPoolManager;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.core.ILock;

/**
 * Is the shutdown hook, called when SIGNAl is sent to JVM to close the JVM.<br>
 * Cleans up all structures like Hazelcast, database connections and job in
 * execution.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class ShutDownHandler extends Thread {
	/**
	 * Creates the handler with a list of threads to interrupt
	 * 
	 * @param jclCheckManager
	 * @param submitter
	 * 
	 * @param threads threads to interrupt
	 * 
	 */
	public ShutDownHandler() {
		super(ShutDownHandler.class.getName());
	}

	/**
	 * Main method, called by JVM to clean up.<br>
	 * In sequence :<br>
	 * <br>
	 * <ul>
	 * <ol>
	 * sets a static reference <code>Main.IS_SHUTTINGDOWN</code> to
	 * <code>true</code>
	 * </ol>
	 * <ol>
	 * drains the initiator
	 * </ol>
	 * <ol>
	 * waits for initiator in DRAINED (if there is a job in execution, it waits)
	 * </ol>
	 * <ol>
	 * interrupts all threads passed by arguments in the constructor
	 * </ol>
	 * <ol>
	 * shuts down Hazelcast, removing itself from Hazelcast group
	 * </ol>
	 * <ol>
	 * Closes all databases connection (for Job, Roles, CommonResources)
	 * </ol>
	 * </ul>
	 */

	public void run() {
		ILock shutdownSynch = null;

		if (Main.getNode() != null) {
			// writes to log that is shutting down
			LogAppl.getInstance().emit(NodeMessage.JEMC071I, Main.getNode().getLabel());

			if (Main.getHazelcast() != null) {
				shutdownSynch = Main.getHazelcast().getLock(Queues.SHUTDOWN_LOCK);
				shutdownSynch.lock();
			}
			// sets the static reference to true
			Main.IS_SHUTTING_DOWN.set(true);

			// drains initiator
			NodeInfoUtility.drain();
			// waits for a second before to check the status
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
			// waits for the status is drained (means the end of eventually
			// execution job)
			while (!Main.getNode().getStatus().equals(Status.DRAINED)) {
				if (!Main.CURRENT_TASKS.isEmpty()) {
					for (CancelableTask task : Main.CURRENT_TASKS.values()) {
						LogAppl.getInstance().emit(NodeMessage.JEMC072I, task.getJobTask().getJob().toString());
					}
				}
				try {
					Thread.sleep(10 * TimeUtils.SECOND);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}

		// interrupts all threads
		if (Main.JCL_CHECKER != null) {
			try {
				Main.JCL_CHECKER.shutdown();
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC162E, e, StringUtils.substringAfterLast(JclCheckingQueueManager.class.getName(), "."));
			}
		}

		// interrupts all threads
		if (Main.INPUT_QUEUE_MANAGER != null) {
			try {
				Main.INPUT_QUEUE_MANAGER.shutdown();
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC162E, e, StringUtils.substringAfterLast(InputQueueManager.class.getName(), "."));
			}
		}

		// stops the statistics timer
		if (Main.getStatisticsManager() != null) {
			Main.getStatisticsManager().stop();
		}

		// interrupt Multicast Service
		if (Main.getMulticastService() != null) {
			Main.getMulticastService().shutdown();
		}

		if (Main.getHazelcast() != null) {
			// shuts down Hazelcast
			Main.getHazelcast().getLifecycleService().shutdown();
		}

		if (DBPoolManager.getInstance().isInitialized()){
			// closes DB connection
			try {
				DBPoolManager.getInstance().close();
				LogAppl.getInstance().emit(NodeMessage.JEMC069I, StringUtils.substringAfterLast(DBPoolManager.class.getName(), "."));
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC070E, e, StringUtils.substringAfterLast(DBPoolManager.class.getName(), "."));
			}
		}
	}
}