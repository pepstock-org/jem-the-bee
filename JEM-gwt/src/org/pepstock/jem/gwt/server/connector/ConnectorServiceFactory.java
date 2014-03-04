/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.server.connector;

import org.pepstock.jem.Service;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.connector.multicast.WebMulticastService;
import org.pepstock.jem.gwt.server.connector.tcpip.TcpIpService;

import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.TcpIpConfig;

/**
 * The we application, contains an hazelcast client that has to connect to the
 * hazelcast cluster (the JEM cluster). A client to connect to the cluster,
 * needs at least one active member in the form ipaddress:port. Since the web
 * application contains the hazelcast configuration of the cluster the following
 * can be true:
 * <p>
 * 1- The cluster is running on multicast mode
 * <p>
 * 2- The cluster is running in TCP/IP mode
 * <p>
 * 
 * If the cluster is running in multicast mode, the ConnectorServiceFactory will
 * instantiated a MulticastService that will discover the nodes via multicast
 * and then tell the client how to connect to the cluster.
 * 
 * If the cluster is running in TCP/IP mode, the ConnectorServiceFactory will
 * instantiated a TcpIpService that with the hazelcast configuration present in
 * the web configuration that contains all the information about those nodes
 * will connect to possible active members of the cluster
 * 
 * @author Simone "Busy" Businaro.
 * 
 */
public class ConnectorServiceFactory {
	
	/**
	 * To avoid any instantiation
	 */
    private ConnectorServiceFactory() {
    }

	/**
	 * 
	 * @return the right service, Multicast or TCP-IP, depending on the
	 *         hazelcast configuration
	 */
	public static Service getConnectorServie() {
		Service service = null;
		MulticastConfig multicastConfig = SharedObjects.getInstance().getHazelcastConfig()
				.getNetworkConfig().getJoin().getMulticastConfig();
		TcpIpConfig tcpIpConfig = SharedObjects.getInstance().getHazelcastConfig()
				.getNetworkConfig().getJoin().getTcpIpConfig();
		if (multicastConfig != null && multicastConfig.isEnabled()) {
			// set multicast port plus one to use for JEM multicast service that
			// must be different from hazelcast one
			int multicastPort = multicastConfig.getMulticastPort() + 100;
			multicastConfig.setMulticastPort(multicastPort);
			service = new WebMulticastService();
		} else if (tcpIpConfig != null && tcpIpConfig.isEnabled()) {
			service = new TcpIpService();
		}
		return service;
	}
}
