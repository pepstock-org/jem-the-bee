package org.pepstock.jem.gwt.server.listeners;

import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.security.JemCacheManager;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;

/**
 * Is the client life cycle listener for the multicast service. Here the
 * business logic for each state changes
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class MulticastLifeCycle implements LifecycleListener {

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

	/**
	 * This method was added to this class to centralized all operation relative
	 * to the client start up. Some operation need the hazelcast instance that
	 * cannot be available in the listener so we use this method
	 * 
	 * @param instance
	 */
	public void atInstantiation(HazelcastInstance instance) {
		SharedObjects.getInstance().setDataClusterAvailable(true);
		SharedObjects.getInstance().setHazelcastClient(instance);
		JemCacheManager.updateJemCache();
	}
}
