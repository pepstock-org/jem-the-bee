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
package org.pepstock.jem.node.events;

import java.util.EventListener;
import java.util.concurrent.LinkedBlockingQueue;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.hazelcast.Queues;

/**
 * Contains all listener of job lifecycle. <br>
 * It fires event when a job changes its status, in asynch way.
 * 
 * @see JobLifecycleListener
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 * 
 */
public class JobLifecycleListenersSystem extends ListenersSystem {

	private LinkedBlockingQueue<JobLifecycleEvent> queue = null;

	/**
	 * Empty constructor
	 */
	public JobLifecycleListenersSystem() {
		queue = new LinkedBlockingQueue<JobLifecycleEvent>();
		Notifier notifier = new Notifier();
		notifier.start();
	}

	/**
	 * Returns if has listeners
	 * 
	 * @return <code>true</code> if has listeners, otherwise <code>false</code>
	 */
	public boolean hasListeners() {
		return hasListener(JobLifecycleListener.class);
	}

	/**
	 * Adds a new event about job lifecycle in a queue, so it can manage and
	 * fire event in async way.
	 * 
	 * @param event
	 */
	public synchronized void addJobLifecycleEvent(JobLifecycleEvent event) {
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC042E, e);
		}
	}

	/**
	 * This class reads from queue all events to notify all listeners.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.3
	 * 
	 */
	class Notifier extends Thread {

		/**
		 * Reads from queue new events and calls all fire methods.
		 */
		public void run() {
			while (true) {
				try {
					// gets event from queue
					JobLifecycleEvent event = queue.take();
					// checks which map has created the event
					if (event.getStatus().equalsIgnoreCase(Queues.INPUT_QUEUE)) {
						// fires the right method
						fireQueued(event.getJob());
					} else if (event.getStatus().equalsIgnoreCase(Queues.RUNNING_QUEUE)) {
						// fires the right method
						fireRunning(event.getJob());
					} else if (event.getStatus().equalsIgnoreCase(Queues.OUTPUT_QUEUE)) {
						// fires the right method
						fireEnded(event.getJob());
					}
				} catch (Exception e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC041E, e);
				}
			}
		}

		/**
		 * Fires event when a job is put on input queue
		 * 
		 * @param job job instance in input queue
		 */
		public void fireQueued(Job job) {
			// scans all listeners and
			// fires the event
			EventListener[] listener = getAllListeners(JobLifecycleListener.class);
			for (int i = 0; i < listener.length; i++) {
				((JobLifecycleListener) listener[i]).queued(job);
			}
		}

		/**
		 * Fires event when a job is set running
		 * 
		 * @param job job instance
		 */
		public void fireRunning(Job job) {
			// scans all listeners and
			// fires the event
			EventListener[] listener = getAllListeners(JobLifecycleListener.class);
			for (int i = 0; i < listener.length; i++) {
				((JobLifecycleListener) listener[i]).running(job);
			}
		}

		/**
		 * Fires event when a job is ended
		 * 
		 * @param job job instance
		 */
		public void fireEnded(Job job) {
			// scans all listeners and
			// fires the event
			EventListener[] listener = getAllListeners(JobLifecycleListener.class);
			for (int i = 0; i < listener.length; i++) {
				((JobLifecycleListener) listener[i]).ended(job);
			}
		}
	}
}