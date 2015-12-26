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
package org.pepstock.jem.gwt.server.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

import org.pepstock.jem.gwt.server.commons.ExceptionsUtility;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.JemException;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

/**
 * Creates a bean with all sockets info to create teh proxy communication with JEM cluster
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class ProxyBeanFactory {
	
	private static int COUNTER = 0;

	/**
	 * To avoid any instantiation
	 */
	private ProxyBeanFactory() {
	}

	/**
	 * Returns a bean with socket info 
	 * @param incoming incoming socket
	 * @return teh bean with sockets info
	 * @throws JemException if any error occurs
	 */
	static ProxyBean createProxyBean(Socket incoming) throws JemException{
		// gets connection to putcoming socket
		Socket outcoming = openConnection();
		// creates the bean with UUID
		ProxyBean bean = new ProxyBean();
		bean.setId(UUID.randomUUID().toString());
		// sets sockets
		bean.setIncomingSocket(incoming);
		bean.setOutcomingSocket(outcoming);
		// sets host address info
		bean.setIncomingHost(incoming.getRemoteSocketAddress());
		bean.setOutcomingHost(outcoming.getRemoteSocketAddress());
		return bean;
	}
	
	/**
	 * Opens the connection to a member of JEM cluster if available
	 * @return opened socket instance
	 * @throws JemException if any error occurs
	 */
	private static Socket openConnection() throws JemException {
		// gets an available member of JEM cluster
		InetSocketAddress sa = getMember();
		try {
			// creates the outcoming socket
			return new Socket(sa.getAddress(), sa.getPort());
		} catch (IOException ioe) {
			throw new JemException(sa.toString(), ioe);
		}
	}
	
	/**
	 * Gets an available member of JEM cluster to setup the connection, round robin approach
	 * @return socket address of the JEM node
	 * @throws JemException if JEM cluster is not available
	 */
	private synchronized static InetSocketAddress getMember() throws JemException{
		// checks if cluster is available
		if (!SharedObjects.getInstance().isDataClusterAvailable()){
			throw ExceptionsUtility.throwGroupNotAvailableException();
		}
		// access to JEM cluster
		// getting the cluster members
		HazelcastInstance instance = SharedObjects.getInstance().getHazelcastClient();
		Cluster cluster = instance.getCluster();
		// scans all members
		Set<Member> members = cluster.getMembers();
		int localCounter = 0;
		for (Member member : members) {
			// using round robin approach
			// uses a counter to go to the next member
			if (localCounter == COUNTER){
				COUNTER++;
				if (COUNTER == members.size()){
					COUNTER = 0;
				}
				// gets the member
				return member.getSocketAddress();
			}
			localCounter++;
		}
		// if here, JEM not available
		throw ExceptionsUtility.throwGroupNotAvailableException();
	}
	
}
