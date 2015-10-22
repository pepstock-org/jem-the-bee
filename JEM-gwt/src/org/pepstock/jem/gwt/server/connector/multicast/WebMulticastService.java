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
package org.pepstock.jem.gwt.server.connector.multicast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.pepstock.jem.Service;
import org.pepstock.jem.ServiceStatus;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.TimeUtils;

/**
 * Multicast service used when the hazelcast configuration is set with multicast
 * enabled. This class will discover throw multicast protocoll all the nodes of
 * the JEM cluster and uses this information to start hazelcst client.
 * <p>
 * This class will enabled the web client of the following capabilities:
 * <p>
 * 1) if the cluster is down and web application start, the web part will stay
 * in listening in the multicast port and as soon as a node of a cluster will
 * start up the web clietn will connect to him.
 * <p>
 * 2) If the web application is running and the cluster shut down, the client
 * will remain in listening in the multicast port and as soon a node will start
 * up the web application will again connet to it
 * <p>
 * 3) The web client will remain connected to the cluster until all the nodes of
 * the cluster shut down.
 * 
 * @author Simone"Busy" Businaro
 * 
 */
public class WebMulticastService extends Service {

	/**
	 * The service name
	 */
	public static final String NAME = "Multicast";

	private ExecutorService executorService = null;

	private WebMulticastSender sender;

	/**
	 * The Multicast service
	 */
	public WebMulticastService() {
		this.executorService = Executors.newSingleThreadExecutor();
		this.sender = new WebMulticastSender();
	}

	/**
	 * Start the Multicast Service
	 */
	public void start() {
		setStatus(ServiceStatus.STARTING);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
		WebMulticastListener clientMulticastListeners = new WebMulticastListener();
		executorService.execute(clientMulticastListeners);
		while (!clientMulticastListeners.isReady()) {
			try {
				Thread.sleep(TimeUtils.SECOND);
			} catch (InterruptedException e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG066I, Thread.currentThread().getName());
			}
		}
		sender.sendRequestMessage();
		setStatus(ServiceStatus.STARTED);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
	}

	/**
	 * Stop gracefully the Multicast service
	 * 
	 * @param config
	 */
	public void shutdown() {
		setStatus(ServiceStatus.SHUTTING_DOWN);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
		executorService.shutdownNow();
		sender.sendShutDownMessage();
		try {
			if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG061W, NAME);
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG061W, NAME);
		}
		setStatus(ServiceStatus.SHUT_DONW);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
	}

}
