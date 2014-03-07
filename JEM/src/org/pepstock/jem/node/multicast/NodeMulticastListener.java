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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.multicast.messages.ClientRequest;
import org.pepstock.jem.node.multicast.messages.MulticastMessage;
import org.pepstock.jem.node.multicast.messages.MulticastMessageFactory;
import org.pepstock.jem.node.multicast.messages.NodeResponse;
import org.pepstock.jem.node.multicast.messages.ShutDown;
import org.pepstock.jem.util.CharSet;

/**
 * Is a thread responsible for listening to the multicast client request and
 * response with the list of nodes presents in the cluster so that the web
 * hazelcast client can start. This service will be invoke by the web client
 * only if in the hazelcast configuration multicast is enabled.
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class NodeMulticastListener implements Runnable {

	private boolean isReady = false;

	/**
	 * listen to the multicast address and port of the jem multicast service and
	 * if it recieve a valid request from web client it returns the list of
	 * members present in the cluster in the form Ipaddress:port
	 */
	@Override
	public void run() {
		MulticastSocket socket = null;
		DatagramPacket inPacket = null;
		try {
			// Prepare to join multicast group
			socket = new MulticastSocket(Main.getMulticastService().getConfig().getMulticastPort());
			socket.setTimeToLive(Main.getMulticastService().getConfig().getMulticastTimeToLive());
			InetAddress address = InetAddress.getByName(Main.getMulticastService().getConfig().getMulticastGroup());
			socket.joinGroup(address);
			isReady = true;
			while (!Thread.currentThread().isInterrupted()) {
				byte[] inBuf = new byte[512];
				inPacket = new DatagramPacket(inBuf, inBuf.length);
				// wait until a message is received
				socket.receive(inPacket);
				String inMsg = new String(inBuf, 0, inPacket.getLength(), CharSet.DEFAULT);
				MulticastMessage message = MulticastMessageFactory.getMessage(inMsg);
				// if is a message from client to get notify about the cluster
				// members
				if (message instanceof ClientRequest) {
					ClientRequest multicastRequest = (ClientRequest) message;
					if (multicastRequest.getGroup().equals(Main.EXECUTION_ENVIRONMENT.getEnvironment())) {
						LogAppl.getInstance().emit(NodeMessage.JEMC225I, inPacket.getAddress());
						MulticastSender.sendNodesInfo();
					}
				} else if (message instanceof NodeResponse) {
					// if is a node message do nothing I'm not
					// interesting
				} else if (message instanceof ShutDown) {
					// if is shudown message do nothing when it will come back
					// to the while loop it will exit if the executor
					// shutdownNow() has been invoked
				} else {
					// log a warn saying that is not a JEM multicast message
					LogAppl.getInstance().emit(NodeMessage.JEMC226W, inPacket.getAddress(), Main.getMulticastService().getConfig().getMulticastGroup(), Main.getMulticastService().getConfig().getMulticastPort(), inMsg);
				}
			}
			socket.close();
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(NodeMessage.JEMC224E, ioe);
		}
	}

	/**
	 * @return the isReady
	 */
	public boolean isReady() {
		return isReady;
	}

}