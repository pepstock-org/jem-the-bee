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
package org.pepstock.jem.gwt.server.connector.tcpip;

import java.util.List;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.connector.WebInterceptor;
import org.pepstock.jem.gwt.server.listeners.TcpIpLifeCycle;
import org.pepstock.jem.log.LogAppl;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * Is the runnable that try to connect to the JEM cluster in TCP/IP mode
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class TcpIpConnector implements Runnable {

	/**
	 * Retry interval
	 */
	public static final int RETRY_PERIOD = 15;

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Config config = SharedObjects.getInstance().getHazelcastConfig();
				boolean isSocketInterceptor = config.getNetworkConfig().getSocketInterceptorConfig().isEnabled();
				ClientConfig clientConfig = new ClientConfig();
				clientConfig.setGroupConfig(config.getGroupConfig());
				ClientNetworkConfig networkConfig = new ClientNetworkConfig();
				clientConfig.setNetworkConfig(networkConfig);
				if (isSocketInterceptor) {
					SocketInterceptorConfig socketInterceptorConfig=new SocketInterceptorConfig();
					socketInterceptorConfig.setClassName(WebInterceptor.class.getName());
					socketInterceptorConfig.setEnabled(true);
					socketInterceptorConfig.setProperties(config.getNetworkConfig().getSocketInterceptorConfig().getProperties());
					networkConfig.setSocketInterceptorConfig(socketInterceptorConfig);
				}
				List<String> members = config.getNetworkConfig().getJoin().getTcpIpConfig().getMembers();
				int port = config.getNetworkConfig().getPort();
				for (String member : members) {
					for (int i = 0; i < 5; i++) {
						int currPort = port + i;
						String currMember = member + ":" + currPort;
						networkConfig.addAddress(currMember);
					}
				}
				TcpIpLifeCycle lifeCycle = new TcpIpLifeCycle();
				ListenerConfig listenerConfig = new ListenerConfig();
				listenerConfig.setImplementation(lifeCycle);
				clientConfig.addListenerConfig(listenerConfig);
				HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
				lifeCycle.atInstantiation(instance);
				lifeCycle.setClientConnected(true);
				break;
			} catch (Exception e) {
				LogAppl.getInstance().debug(e.getMessage(), e);
				try {
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG062W, RETRY_PERIOD);
					Thread.sleep(RETRY_PERIOD * 1000);
				} catch (InterruptedException e1) {
					LogAppl.getInstance().ignore(e1.getMessage(), e1);
				}
			}
		}
	}
}
