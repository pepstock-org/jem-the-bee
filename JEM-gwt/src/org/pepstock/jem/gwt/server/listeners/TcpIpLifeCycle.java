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

import org.pepstock.jem.ServiceStatus;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.security.JemCacheManager;

/**
 * Is the client life cycle listener for the TCP/IP service. It handled the
 * business logic for each state changes
 * 
 * @author Simone "Busy" Businaro
 * @version 1.3
 */
public class TcpIpLifeCycle extends LifeCycle {

	private boolean isClientConnected = false;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.listeners.LifeCycle#handleShuttingDown()
	 */
    @Override
    public void shuttingDown() {
		SharedObjects.getInstance().setDataClusterAvailable(false);
		JemCacheManager.updateJemCache();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.listeners.LifeCycle#handleShutDown()
	 */
    @Override
    public void shuttedDown() {
		// if the client was connected than restart the TcpIpService only if the
		// status of the service is not SHUTTING_DOWN or SHUT_DOWN
		if (isClientConnected && !SharedObjects.getInstance().getConnectorService().getStatus().equals(ServiceStatus.SHUTTING_DOWN)
				&& !SharedObjects.getInstance().getConnectorService().getStatus().equals(ServiceStatus.SHUT_DONW)) {
			SharedObjects.getInstance().getConnectorService().start();
		}
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
