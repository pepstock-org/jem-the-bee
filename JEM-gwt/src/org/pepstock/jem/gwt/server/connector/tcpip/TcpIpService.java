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
package org.pepstock.jem.gwt.server.connector.tcpip;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.pepstock.jem.Service;
import org.pepstock.jem.ServiceStatus;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.LogAppl;

/**
 * TCP-IP service used when the hazelcast configuration is set with tcp-ip
 * enabled. This class will use the information of the nodes present in the
 * hazelcast configuration to try to connect to these nodes in tcp ip mode.
 * <p>
 * This class will enabled the web client of the following capabilities:
 * <p>
 * 1) if the cluster is down and web application start, the web part will keep
 * trying to connect to the nodes every 30 seconds. So when at least a node
 * present in the configuration will start up, in at most 30 seconds the web
 * application will connect to it.
 * <p>
 * 2) If the web application is running and the cluster shut down, the client
 * will keep trying to connect to the nodes every 30 seconds. So when at least a
 * node present in the configuration will start up, in at most 30 seconds the
 * web application will connect to it.
 * <p>
 * 3) The web client will remain connected to the cluster until all the nodes of
 * the cluster shut down.
 * 
 * @author Simone"Busy" Businaro
 * 
 */
public class TcpIpService extends Service {

	private static String NAME = "TCP-IP Service";

	private ExecutorService executorService = null;

	/**
	 * 
	 */
	public TcpIpService() {
		this.executorService = Executors.newSingleThreadExecutor();
	}

	/**
	 * Start the hazelcast client in TOCP/IP mode
	 * 
	 * @param config
	 */
	public void start() {
		setStatus(ServiceStatus.STARTING);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
		TcpIpConnector tcpIpConnector = new TcpIpConnector();
		executorService.execute(tcpIpConnector);
		setStatus(ServiceStatus.STARTED);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
	}

	/**
	 * Stop gracefully the TCP/IP service
	 * 
	 * @param config
	 */
	public void shutdown() {
		setStatus(ServiceStatus.SHUTTING_DOWN);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
		executorService.shutdownNow();
		try {
			if (!executorService.awaitTermination(TcpIpConnector.RETRY_PERIOD,
					TimeUnit.SECONDS)) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG061W);
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG061W, e);
		}
		setStatus(ServiceStatus.SHUT_DONW);
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG060I, NAME,
				getStatus());
	}

}
