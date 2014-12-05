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
package org.pepstock.jem.gwt.server.connector.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.connector.WebInterceptor;
import org.pepstock.jem.gwt.server.listeners.MulticastLifeCycle;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.multicast.messages.ClientRequest;
import org.pepstock.jem.node.multicast.messages.MulticastMessage;
import org.pepstock.jem.node.multicast.messages.MulticastMessageFactory;
import org.pepstock.jem.node.multicast.messages.NodeResponse;
import org.pepstock.jem.node.multicast.messages.ShutDown;
import org.pepstock.jem.util.CharSet;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * ClientMulticastListener listen to the same multicast address of hazelcast on
 * hazelcast port plus one. This listener will received by the Coordinator of
 * the cluster all the address and port of the running nodes so that the web
 * hazelcast client will be able to join the cluster.
 * 
 * @author Simone "Busy" Businaro
 */
public class WebMulticastListener implements Runnable {

	private boolean ready = false;

	/**
	 * 
	 * @return true if the Client is in listening to the multicast address port
	 */
	public boolean isReady() {
		return this.ready;
	}

	@Override
	public void run() {
		MulticastSocket socket = null;
		DatagramPacket inPacket = null;
		try {
			Config config = SharedObjects.getInstance().getHazelcastConfig();
			boolean isSocketInterceptor = config.getNetworkConfig().getSocketInterceptorConfig().isEnabled();
			MulticastConfig multicastConfig = config.getNetworkConfig().getJoin().getMulticastConfig();
			// Prepare to join multicast group
			String multicastGroup = multicastConfig.getMulticastGroup();
			int multicastPort = multicastConfig.getMulticastPort();
			socket = new MulticastSocket(multicastPort);
			
			socket.setNetworkInterface(SharedObjects.getInstance().getNetworkInterface().getNetworkInterface());

			socket.setTimeToLive(multicastConfig.getMulticastTimeToLive());
			InetAddress groupAddress = InetAddress.getByName(multicastGroup);
			socket.joinGroup(groupAddress);
			ready = true;

			// so we can shatted down
			while (!Thread.currentThread().isInterrupted()) {
				byte[] inBuf = new byte[20000];
				inPacket = new DatagramPacket(inBuf, inBuf.length);
				socket.receive(inPacket);
				String inMsg = new String(inBuf, 0, inPacket.getLength(), CharSet.DEFAULT);

				MulticastMessage multicastMesssage = MulticastMessageFactory.getMessage(inMsg);
				// if is a client message do nothing I'm not
				// interesting
				if (multicastMesssage instanceof ClientRequest) {
					// ignore
				} else if (multicastMesssage instanceof NodeResponse) {
					NodeResponse message = (NodeResponse) multicastMesssage;
					// check if group is correct
					if (message.getGroup() != null && message.getGroup().equals(config.getGroupConfig().getName()) &&
							(SharedObjects.getInstance().getHazelcastClient() == null || !SharedObjects.getInstance().getHazelcastClient().getLifecycleService().isRunning())) {
						// start hazelcast client only if it is not runnig.
						LogAppl.getInstance().emit(UserInterfaceMessage.JEMG058I, inPacket.getAddress(), inMsg);
						// create client config
						ClientConfig clientConfig = new ClientConfig();
						clientConfig.setGroupConfig(config.getGroupConfig());
						// check socket interceptor
						if (isSocketInterceptor) {
							clientConfig.setSocketInterceptor(new WebInterceptor(config.getNetworkConfig().getSocketInterceptorConfig().getProperties()));
						}
						clientConfig.setAddresses(message.getNodesMembers());
						MulticastLifeCycle lifeCycle = new MulticastLifeCycle();

						clientConfig.getListeners().add(lifeCycle);
						HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
						lifeCycle.atInstantiation(instance);
					}
				} else if (multicastMesssage instanceof ShutDown) {
					// ignore
				} else {
					// log a warn saying that is not a JEM multicast message
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG057W, inPacket.getAddress(), multicastGroup, multicastPort, inMsg);
				}
			}
			socket.leaveGroup(groupAddress);
			socket.close();
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG056E, ioe);
		}
	}
}