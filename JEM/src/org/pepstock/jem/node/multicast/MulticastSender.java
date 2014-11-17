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

import com.hazelcast.config.Interfaces;
import com.hazelcast.core.Member;

/**
 * Class used to send multicast message from the node
 * 
 * @author Simone "Busy" Businaro
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
	 * @param config the multicast config
	 */
	public static void sendNodesInfo(){
		if (Main.IS_COORDINATOR.get()) {
			MulticastSocket socket = null;
			byte[] outBuf;
			NodeResponse nodeMessage = new NodeResponse();
			Set<Member> members = Main.getHazelcast().getCluster().getMembers();
			nodeMessage.setGroup(Main.EXECUTION_ENVIRONMENT.getEnvironment());
			for (Member member : members) {
				String ip = member.getInetSocketAddress().getAddress().getHostAddress();
				int port = member.getInetSocketAddress().getPort();
				nodeMessage.addMember(ip + ":" + port);
			}
			String response = NodeResponse.marshall(nodeMessage);
			try {
				socket = new MulticastSocket();
				Interfaces interfaces = Main.getHazelcast().getConfig().getNetworkConfig().getInterfaces();
				if (interfaces != null && interfaces.isEnabled()) {
					try {
						socket.setInterface(MulticastUtils.getInetAddress(interfaces.getInterfaces()));
					} catch (Exception e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC249W, e);
					}
				}
				socket.setTimeToLive(Main.getMulticastService().getConfig().getMulticastTimeToLive());
				InetAddress address = InetAddress.getByName(Main.getMulticastService().getConfig().getMulticastGroup());
				outBuf = response.getBytes(CharSet.DEFAULT);
				DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length, address, Main.getMulticastService().getConfig().getMulticastPort());
				// FIXME
				System.err.println(socket.getInetAddress()+" "+address+" "+socket.getInterface()+" "+response);
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
			ShutDown shutDown=new ShutDown();
			String shutDownMessage=ShutDown.marshall(shutDown);
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(Main.getMulticastService().getConfig().getMulticastGroup());
			outBuf = shutDownMessage.getBytes(CharSet.DEFAULT);
			DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length, address, Main.getMulticastService().getConfig().getMulticastPort());
			socket.send(outPacket);
			socket.close();
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(NodeMessage.JEMC224E, ioe);
		}
		
	}

}
