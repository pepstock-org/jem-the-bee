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
 * FIXME
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class ProxyBeanFactory {
	
	private static int COUNTER = 0;

	/**
	 * 
	 */
	private ProxyBeanFactory() {
	}

	static ProxyBean createProxyBean(Socket incoming) throws JemException{
		Socket outcoming = openConnection();
		ProxyBean bean = new ProxyBean();
		bean.setId(UUID.randomUUID().toString());
		bean.setIncomingSocket(incoming);
		bean.setOutcomingSocket(outcoming);
		bean.setIncomingHost(incoming.getRemoteSocketAddress());
		bean.setOutcomingHost(outcoming.getRemoteSocketAddress());
		return bean;
	}
	
	private static Socket openConnection() throws JemException {
		InetSocketAddress sa = getMember();
		try {
			return new Socket(sa.getAddress(), sa.getPort());
		} catch (IOException ioe) {
			throw new JemException(sa.toString(), ioe);
		}
	}
	
	private synchronized static InetSocketAddress getMember() throws JemException{
		if (!SharedObjects.getInstance().isDataClusterAvailable()){
			throw ExceptionsUtility.throwGroupNotAvailableException();
		}
		HazelcastInstance instance = SharedObjects.getInstance().getHazelcastClient();
		Cluster cluster = instance.getCluster();
		Set<Member> members = cluster.getMembers();
		int localCounter = 0;
		for (Member member : members) {
			if (localCounter == COUNTER){
				COUNTER++;
				if (COUNTER == members.size()){
					COUNTER = 0;
				}
				return member.getInetSocketAddress();
			}
			localCounter++;
		}
		throw ExceptionsUtility.throwGroupNotAvailableException();
	}
	
}
