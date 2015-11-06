/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
package org.pepstock.jem.gwt.server.listeners;

import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.security.JemCacheManager;

import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;

/**
 * Is the client life cycle listener for the multicast service. Here the
 * business logic for each state changes
 * 
 * @author Simone "Busy" Businaro
 * @version 1.3
 * 
 */
public class MulticastLifeCycle extends LifeCycle {

	/**
	 * Be aware of the fact that at least in hazelcast 3.1.3 a client will pass
	 * through the states starting and started even if it will never be able to
	 * actually connect to a cluster. In deep, a client that will fail to
	 * connect to a cluster will pass through the following states:
	 * <p>
	 * {@link LifecycleState#STARTING}
	 * <p>
	 * {@link LifecycleState#STARTED}
	 * <p>
	 * {@link LifecycleState#SHUTTING_DOWN}
	 * <p>
	 * {@link LifecycleState#SHUTDOWN}
	 * 
	 * at state change we do the necessary business logic
	 */
	@Override
	public void stateChanged(LifecycleEvent event) {
		if (event.getState().equals(LifecycleState.STARTING)) {
			handleStarting();
		} else if (event.getState().equals(LifecycleState.STARTED)) {
			handleStarted();
		} else if (event.getState().equals(LifecycleState.SHUTTING_DOWN)) {
			handleShuttingDown();
		} else if (event.getState().equals(LifecycleState.SHUTDOWN)) {
			handleShutDown();
		}
	}

	/**
	 * Handle the client started event
	 */
	private void handleStarted() {
		// do nothing
	}

	/**
	 * Handle the client starting event
	 */
	private void handleStarting() {
		// do nothing
	}

	/**
	 * Be careful on setting business logic here because probably for a
	 * hazelcast bug can cause the client to keep trying to log to the cluster
	 * again and again
	 */
	private void handleShuttingDown() {
		// do nothing
	}

	/**
	 * Handle the cliet shut down
	 */
	private void handleShutDown() {
		SharedObjects.getInstance().setDataClusterAvailable(false);
		JemCacheManager.updateJemCache();
	}

}
