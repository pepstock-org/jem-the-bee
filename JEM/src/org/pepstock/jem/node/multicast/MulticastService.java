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
package org.pepstock.jem.node.multicast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.pepstock.jem.Service;
import org.pepstock.jem.ServiceStatus;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.multicast.messages.NodeResponse;

import com.hazelcast.config.MulticastConfig;

/**
 * Is the multicast service that is activated if the hazelcast configuration has
 * multicast enabled.
 * 
 * @see NodeMulticastListener
 * @see NodeMulticastSender
 * @see NodeResponse
 * @see MulticastConfig
 * 
 * 
 *      Here is an example of a complete communication between node and web
 *      client: When the node start, register a listener to the same multicast
 *      group of hazelcast but with a different port (hazelcast port +100).
 *      After the listener has been register the node send the following message
 *      containing all the nodes of the cluster:
 * 
 *      {@code
 *  <cluster>
 *  	<group>GroupName</group>
 *  	<nodesMembers>
 * 	 		<node>197.12.1.1:58824</node>
 * 			<node>197.13.1.1:58824</node>
 * 			<node>197.14.1.1:58824</node>
 * 			...
 *  	</nodesMembers>
 * 	</cluster>
 * }
 * 
 *      each time the listener will receive the following request:
 *      {@code
 *  <request>
 *  	<group>GroupName</group>
 * 	</request>
 * }
 * 
 *      it will answer with the list of nodes in the cluster: {@code
 *  <cluster>
 *  	<group>GroupName</group>
 *  	<nodesMembers>
 * 	 		<node>197.12.1.1:58824</node>
 * 			<node>197.13.1.1:58824</node>
 * 			<node>197.14.1.1:58824</node>
 * 			...
 *  	</nodesMembers>
 * 	</cluster>
 * }
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class MulticastService extends Service {

	private ExecutorService executorService = null;

	private MulticastConfig config = null;

	/**
	 * 
	 * @param config multicast config
	 */
	public MulticastService(MulticastConfig config) {
		this.executorService = Executors.newSingleThreadExecutor();
		this.config = config;
	}

	/**
	 * Start the multicast service. The multicast service is used by the web
	 * client to get informations about the nodes of the cluster so to be able
	 * to connect to them in case the cluster is started with multicast enabled
	 * 
	 * @see NodeMulticastListener
	 * @see NodeMulticastSender
	 * @see NodeResponse
	 * @see MulticastConfig
	 * 
	 */
	public void start() {
		setStatus(ServiceStatus.STARTING);
		LogAppl.getInstance().emit(NodeMessage.JEMC227I, getStatus());
		NodeMulticastListener multicastListeners = new NodeMulticastListener();
		executorService.execute(multicastListeners);
		// Wait until multicast listener is ready
		while (!multicastListeners.isReady()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		MulticastSender.sendNodesInfo();
		setStatus(ServiceStatus.STARTED);
		LogAppl.getInstance().emit(NodeMessage.JEMC227I, getStatus());
	}

	/**
	 * Stop gracefully the multicast service
	 * 
	 * @param config
	 */
	public void shutdown() {
		setStatus(ServiceStatus.SHUTTING_DOWN);
		LogAppl.getInstance().emit(NodeMessage.JEMC227I, getStatus());
		executorService.shutdownNow();
		MulticastSender.sendShutDownMessage();
		try {
			if (!executorService.awaitTermination(100, TimeUnit.SECONDS)) {
				LogAppl.getInstance().emit(NodeMessage.JEMG228W);
			}
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMG228W, e);
		}
		setStatus(ServiceStatus.SHUT_DONW);
		LogAppl.getInstance().emit(NodeMessage.JEMC227I, getStatus());
	}

	/**
	 * @return the config
	 */
	public MulticastConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(MulticastConfig config) {
		this.config = config;
	}

}
