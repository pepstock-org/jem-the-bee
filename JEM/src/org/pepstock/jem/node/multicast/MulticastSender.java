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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Set;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.multicast.messages.NodeResponse;
import org.pepstock.jem.node.multicast.messages.ShutDown;
import org.pepstock.jem.util.CharSet;

import com.hazelcast.core.Member;

/**
 * Class used to send multicast message from the node
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 *
 */
public class MulticastSender {
	
	/**
	 * To avoid any instantiation
	 */
	private MulticastSender() {
	}

	/**
	 * send to the web client an xml containing the list of all nodes belonging 
	 * to the JEM cluster<p>
	 * example:
	 * {@code
 	 *  <response>
 	 * 		<node>197.12.1.1:58824</node>
 	 * 		<node>197.13.1.1:58824</node>
 	 * 		<node>197.14.1.1:58824</node>
 	 * 			...
 	 * 	</response>
 	 * }
	 */
	public static void sendNodesInfo(){
		// only the coordinator sends the nodes info
		if (Main.IS_COORDINATOR.get()) {
			MulticastSocket socket = null;
			byte[] outBuf;
			// prepares the node message to send
			NodeResponse nodeMessage = new NodeResponse();
			// gets all HC members
			Set<Member> members = Main.getHazelcast().getCluster().getMembers();
			// sets the execution environment
			nodeMessage.setGroup(Main.EXECUTION_ENVIRONMENT.getEnvironment());
			// scans all members
			for (Member member : members) {
				// gets ip and prot 
				String ip = member.getInetSocketAddress().getAddress().getHostAddress();
				int port = member.getInetSocketAddress().getPort();
				// adds HC node, format ip:port
				nodeMessage.addMember(ip + ":" + port);
			}
			// serialize in string (XML)
			String response = NodeResponse.marshall(nodeMessage);
			try {
				// creates a mutlicast socket
				socket = new MulticastSocket();
				// sets interface
				socket.setNetworkInterface(Main.getNetworkInterface().getNetworkInterface());
				// and time to live of message
				socket.setTimeToLive(Main.getMulticastService().getConfig().getMulticastTimeToLive());
				// gets multicast group
				InetAddress address = InetAddress.getByName(Main.getMulticastService().getConfig().getMulticastGroup());
				// transforms in bytes
				outBuf = response.getBytes(CharSet.DEFAULT);
				// sends the multicast packet 
				DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length, address, Main.getMulticastService().getConfig().getMulticastPort());
				socket.send(outPacket);
				socket.close();
			} catch (IOException ioe) {
				LogAppl.getInstance().emit(NodeMessage.JEMC224E, ioe);
			}
		}
	}

	/**
	 * Send a shutDown message that is needed after invoke shutDown on MulticastService
	 */
	public static void sendShutDownMessage(){
		byte[] outBuf;
		try {
			// creates a shutdown object to notify
			ShutDown shutDown=new ShutDown();
			// serialize in string
			String shutDownMessage=ShutDown.marshall(shutDown);
			// creates a datagram socket
			DatagramSocket socket = new DatagramSocket();
			// get MULTICAST group
			InetAddress address = InetAddress.getByName(Main.getMulticastService().getConfig().getMulticastGroup());
			// gets bytes
			outBuf = shutDownMessage.getBytes(CharSet.DEFAULT);
			// sends the packt to all.
			DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length, address, Main.getMulticastService().getConfig().getMulticastPort());
			socket.send(outPacket);
			socket.close();
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(NodeMessage.JEMC224E, ioe);
		}
	}
}
