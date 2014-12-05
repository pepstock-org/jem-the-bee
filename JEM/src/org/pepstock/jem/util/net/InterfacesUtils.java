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
package org.pepstock.jem.util.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;

import com.hazelcast.config.Config;
import com.hazelcast.config.Interfaces;

/**
 * Is a class with common utilities for network interface utility
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public final class InterfacesUtils {

	/**
	 * To avoid any instantiation 
	 */
	private InterfacesUtils() {

	}
	
	/**
	 * Returns local network interface touse for multicast and an other services, starting from Hazelcast
	 * configuration where you can put all interfaces and if missing use the local host interface.
	 * @param hazelcastConfig Hazelcast configuration
	 * @return interface object
	 * @throws MessageException if any error occurs
	 */
	public static Interface getInterface(Config hazelcastConfig) throws MessageException{
		Interface networkInterface = new Interface();
		Interfaces interfaces = null;
		// if config is null, it doesn't use Hazelcast, only local host
		if (hazelcastConfig != null){
			interfaces = hazelcastConfig.getNetworkConfig().getInterfaces();	
		}
		Exception exception = null;
		// checks if there is hazelcast configuration for Multicast
		if (interfaces != null && interfaces.isEnabled()) {
			try {
				// uses the hazelcast config
				loadInterfaceFromHazelcastConfiguration(networkInterface, interfaces.getInterfaces());
				return networkInterface;
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC249W, e);
				exception = e;
			}
		} else {
			// if there isn't any configuration for hazelcast
			// scans network interfaces, taking the interface which matches with localhost address
			try {
				Enumeration<NetworkInterface> interfaces3 = NetworkInterface.getNetworkInterfaces();
				while (interfaces3.hasMoreElements()) {
					NetworkInterface ni = interfaces3.nextElement();
					Enumeration<InetAddress> addresses = ni.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress addr = addresses.nextElement();
						String localhost = addr.getHostAddress();
						// checks if is local host address
						if (InetAddress.getLocalHost().getHostAddress().equals(localhost)){
							networkInterface.setAddress(addr);
							networkInterface.setNetworkInterface(ni);
							return networkInterface;
						}
					}
				}
				exception = new MessageException(NodeMessage.JEMC250E);
			} catch (Exception e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
				exception = e;
			}
		}
		throw new MessageException(NodeMessage.JEMC250E, exception);
	}

	/**
	 * Loads interfaces defines on Hazelcast configuration file
	 * @param networkInterface instance to load the network info
	 * @param interfaces list of interfaces set in Hazelcast
	 * @throws MessageException if any error occurs
	 */
	private static void loadInterfaceFromHazelcastConfiguration(Interface networkInterface, Collection<String> interfaces) throws MessageException {
		try {
			for (String toMatch : interfaces) {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets)) {
					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAddresses)) {
						if (doMatch(inetAddress, toMatch)) {
							LogAppl.getInstance().emit(NodeMessage.JEMC248I, inetAddress);
							networkInterface.setAddress(inetAddress);
							networkInterface.setNetworkInterface(netint);
							return;
						}
					}
				}
			}
			throw new MessageException(NodeMessage.JEMC249W);
		} catch (SocketException e) {
			throw new MessageException(NodeMessage.JEMC250E, e);
		}
	}

	/**
	 * 
	 * @param inetAddress
	 * @param toMatch can be either an IP, an IP with wild card "*" on the last
	 *            octet or an IP with a range on last octet (e.g. 196.168.35.10-100)
	 * @return true if the toMatch string matches the hostAddress from the
	 *         InetAddress
	 */
	private static boolean doMatch(InetAddress inetAddress, String toMatchParam) {
		String hostAddres = inetAddress.getHostAddress();
		if (toMatchParam.contains("-")) {
			// if is ipv6 address do not consider
			if (hostAddres.contains(":")) {
				return false;
			}
			String[] octects = toMatchParam.split("\\.");
			String[] octectsIA = hostAddres.split("\\.");
			String first3 = octects[0] + "." + octects[1] + "." + octects[2];
			String firstIA = octectsIA[0] + "." + octectsIA[1] + "." + octectsIA[2];
			if (!first3.equals(firstIA)) {
				return false;
			}
			String lastOctect = octects[3];
			String[] ranges = lastOctect.split("-");
			int min = Integer.parseInt(ranges[0]);
			int max = Integer.parseInt(ranges[1]);
			int inetAddressLastOctect = Integer.parseInt(octectsIA[3]);
			return inetAddressLastOctect >= min && inetAddressLastOctect <= max;
		} else {
			String toMatch = toMatchParam.replace(".", "\\.").replace("*", "\\w*");
			return hostAddres.matches(toMatch);
		}
	}
}
