package org.pepstock.jem.gwt.server.listeners;

import org.pepstock.jem.ServiceStatus;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.security.JemCacheManager;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;

/**
 * Is the client life cycle listener for the TCP/IP service. It handled the
 * business logic for each state changes
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class TcpIpLifeCycle implements LifecycleListener {

	private boolean isClientConnected = false;

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
	 * Handle the client shutting down
	 */
	private void handleShuttingDown() {
		SharedObjects.getInstance().setDataClusterAvailable(false);
		JemCacheManager.updateJemCache();
	}

	/**
	 * Handle the cliet shut down
	 */
	private void handleShutDown() {
		// if the client was connected than restart the TcpIpService only if the
		// status of the service is not SHUTTING_DOWN or SHUT_DOWN
		if (isClientConnected && !SharedObjects.getInstance().getConnectorService().getStatus().equals(ServiceStatus.SHUTTING_DOWN)
				&& !SharedObjects.getInstance().getConnectorService().getStatus().equals(ServiceStatus.SHUT_DONW)) {
			SharedObjects.getInstance().getConnectorService().start();
		}
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

	/**
	 * This method is used to understand if the client fulfilled the connection
	 * to the cluster or not. We cannot use the handleStarted method to
	 * understad that because a client will passed through that state even if it
	 * actually do not connect to the cluster
	 * 
	 * @return the isClientConnected
	 */
	public boolean isClientConnected() {
		return isClientConnected;
	}

	/**
	 * This method is used by the ConnectorService to mark the fact that the
	 * hazlecast client was able to connect to the cluster. We cannot use the
	 * handleStarted method to understad that because a client will passed
	 * through that state even if it actually do not connect to the cluster
	 * 
	 * @param isClientConnected
	 *            the isClientConnected to set
	 */
	public void setClientConnected(boolean isClientConnected) {
		this.isClientConnected = isClientConnected;
	}

}
