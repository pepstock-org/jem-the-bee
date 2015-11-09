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
package org.pepstock.jem.util;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class DefaultClientLifeCycle implements LifecycleListener {

	/* (non-Javadoc)
	 * @see com.hazelcast.core.LifecycleListener#stateChanged(com.hazelcast.core.LifecycleEvent)
	 */
	@Override
	public void stateChanged(LifecycleEvent event) {
		LogAppl.getInstance().emit(NodeMessage.JEMC296I, event.getState().toString());
		if (event.getState().equals(LifecycleState.STARTING)) {
			starting();
		} else if (event.getState().equals(LifecycleState.STARTED)) {
			started();
		} else if (event.getState().equals(LifecycleState.RESTARTING)) {
			restarting();
		} else if (event.getState().equals(LifecycleState.RESTARTED)) {
			restarted();
		} else if (event.getState().equals(LifecycleState.PAUSING)) {
			pausing();
		} else if (event.getState().equals(LifecycleState.PAUSED)) {
			paused();
		} else if (event.getState().equals(LifecycleState.RESUMING)) {
			resuming();
		} else if (event.getState().equals(LifecycleState.RESUMED)) {
			resumed();
		} else if (event.getState().equals(LifecycleState.CLIENT_CONNECTION_OPENING)) {
			clientConnectionOpening();
		} else if (event.getState().equals(LifecycleState.CLIENT_CONNECTION_OPENED)) {
			clientConnectionOpened();
		} else if (event.getState().equals(LifecycleState.CLIENT_CONNECTION_LOST)) {
			clientConnectionLost();
		} else if (event.getState().equals(LifecycleState.SHUTTING_DOWN)) {
			shuttingDown();
		} else if (event.getState().equals(LifecycleState.SHUTDOWN)) {
			shuttedDown();
		}
	}
	
	/**
	 * Called when client is starting
	 */
	public void starting(){
		// do nothing
	}

	/**
	 * Called when client is started
	 */
	public void started(){
		// do nothing
	}

	/**
	 * Called when client is restarting
	 */
	public void restarting(){
		// do nothing
	}
	
	/**
	 * Called when client is restarted
	 */
	public void restarted(){
		// do nothing
	}
	
	/**
	 * Called when client is pausing
	 */
	public void pausing(){
		// do nothing
	}
	
	/**
	 * Called when client is paused
	 */
	public void paused(){
		// do nothing
	}
	
	/**
	 * Called when client is resuming
	 */
	public void resuming(){
		// do nothing
	}

	/**
	 * Called when client is resumed
	 */
	public void resumed(){
		// do nothing
	}

	/**
	 * Called when client is shutting down
	 */
	public void shuttingDown(){
		// do nothing
	}

	/**
	 * Called when client is shutted down
	 */
	public void shuttedDown(){
		// do nothing
	}

	/**
	 * Called when client is opening connection
	 */
	public void clientConnectionOpening(){
		// do nothing
	}

	/**
	 * Called when client is opened connection
	 */
	public void clientConnectionOpened(){
		// do nothing
	}

	/**
	 * Called when client lost connection
	 */
	public void clientConnectionLost(){
		// do nothing
	}
	
}
