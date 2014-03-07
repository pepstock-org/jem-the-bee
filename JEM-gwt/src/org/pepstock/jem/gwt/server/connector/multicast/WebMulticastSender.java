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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.multicast.MulticastService;
import org.pepstock.jem.node.multicast.messages.ClientRequest;
import org.pepstock.jem.node.multicast.messages.ShutDown;
import org.pepstock.jem.util.CharSet;

import com.hazelcast.config.Config;

/**
 * Class responsible for sending multicast messages used by the
 * {@link MulticastService}
 * 
 * @author Simone "Busy" Businaro
 */
public class WebMulticastSender {

	/**
	 * Send multicast request to discover the JEM cluster members
	 */
	public void sendRequestMessage() {
		MulticastSocket socket = null;
		byte[] outBuf;
		try {
			Config config = SharedObjects.getInstance().getHazelcastConfig();
			socket = new MulticastSocket();
			socket.setTimeToLive(config.getNetworkConfig().getJoin()
					.getMulticastConfig().getMulticastTimeToLive());
			InetAddress address = InetAddress.getByName(config
					.getNetworkConfig().getJoin().getMulticastConfig()
					.getMulticastGroup());
			ClientRequest clientRequest = new ClientRequest();
			clientRequest.setGroup(config.getGroupConfig().getName());
			String message = ClientRequest.marshall(clientRequest);
			outBuf = message.getBytes(CharSet.DEFAULT);
			DatagramPacket outPacket = new DatagramPacket(outBuf,
					outBuf.length, address, config.getNetworkConfig().getJoin()
							.getMulticastConfig().getMulticastPort());
			socket.send(outPacket);
			socket.close();
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG056E, ioe);
		}
	}

	/**
	 * Send shut down message that is used during the gracefully shut down
	 * procedure {@link MulticastService#sendShutDownMessage()}
	 */
	public void sendShutDownMessage() {
		DatagramSocket socket = null;
		byte[] outBuf;
		try {
			ShutDown shutDown = new ShutDown();
			String shutDownMessage = ShutDown.marshall(shutDown);
			Config config = SharedObjects.getInstance().getHazelcastConfig();
			socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(config
					.getNetworkConfig().getJoin().getMulticastConfig()
					.getMulticastGroup());
			outBuf = shutDownMessage.getBytes(CharSet.DEFAULT);
			DatagramPacket outPacket = new DatagramPacket(outBuf,
					outBuf.length, address, config.getNetworkConfig().getJoin()
							.getMulticastConfig().getMulticastPort());
			socket.send(outPacket);
			socket.close();
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG056E, ioe);
		}
	}

}