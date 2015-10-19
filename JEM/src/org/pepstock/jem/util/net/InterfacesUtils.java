/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
import java.net.UnknownHostException;
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
 * @version 2.1
 * 
 */
public final class InterfacesUtils {
	
	private static final int ELEMENT_1 = 0;
	
	private static final int ELEMENT_2 = 1;
	
	private static final int ELEMENT_3 = 2;
	
	private static final int ELEMENT_4 = 3;

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
		// checks if there is hazelcast configuration for Multicast
		if (interfaces != null && interfaces.isEnabled()) {
			try {
				// uses the hazelcast config
				loadInterfaceFromHazelcastConfiguration(networkInterface, interfaces.getInterfaces());
				return networkInterface;
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC249W, e);
				throw new MessageException(NodeMessage.JEMC249W, e);
			}
		} else {
			// if there isn't any configuration for hazelcast
			// scans network interfaces, taking the interface which matches with localhost address
			try {
				// scans all NICs
				Enumeration<NetworkInterface> interfaces3 = NetworkInterface.getNetworkInterfaces();
				while (interfaces3.hasMoreElements()) {
					NetworkInterface ni = interfaces3.nextElement();
					// gets all ip addresses
					Enumeration<InetAddress> addresses = ni.getInetAddresses();
					// scans the ip addresses
					while (addresses.hasMoreElements()) {
						InetAddress addr = addresses.nextElement();
						// gets local host
						String localhost = addr.getHostAddress();
						// checks if is local host address
						if (InetAddress.getLocalHost().getHostAddress().equals(localhost)){
							// if yes, sets the address and NIC to use 
							networkInterface.setAddress(addr);
							networkInterface.setNetworkInterface(ni);
							return networkInterface;
						}
					}
				}
				throw new MessageException(NodeMessage.JEMC250E);
			} catch (SocketException e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
				throw new MessageException(NodeMessage.JEMC250E, e);
			} catch (UnknownHostException e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
				throw new MessageException(NodeMessage.JEMC250E, e);
			}
		}
	}

	/**
	 * Loads interfaces defines on Hazelcast configuration file
	 * @param networkInterface instance to load the network info
	 * @param interfaces list of interfaces set in Hazelcast
	 * @throws MessageException if any error occurs
	 */
	private static void loadInterfaceFromHazelcastConfiguration(Interface networkInterface, Collection<String> interfaces) throws MessageException {
		try {
			// scans all interfaces of hazelcast
			for (String toMatch : interfaces) {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				// scans all NICs
				for (NetworkInterface netint : Collections.list(nets)) {
					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
					// scans all addresses
					for (InetAddress inetAddress : Collections.list(inetAddresses)) {
						// checks if matches with the HC ones
						if (doMatch(inetAddress, toMatch)) {
							LogAppl.getInstance().emit(NodeMessage.JEMC248I, inetAddress);
							// sets the address and NIC
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
	 * Checks a string macthes with a IP address.
	 * <br>
	 * String is the NIC interface defined inside of Hazecast configuration.
	 * <br>
	 * INetaddress is one of addresses of the machine.
	 * 
	 * @param inetAddress ont of addresses of the machine
	 * @param toMatch can be either an IP, an IP with wild card "*" on the last
	 *            octet or an IP with a range on last octet (e.g. 196.168.35.10-100)
	 * @return true if the toMatch string matches the hostAddress from the
	 *         InetAddress
	 */
	private static boolean doMatch(InetAddress inetAddress, String toMatchParam) {
		// gets host ip address
		String hostAddres = inetAddress.getHostAddress();
		// if HC string contains "-", 
		// means contains more than 1 ip address (at last OCTECT)
		if (toMatchParam.contains("-")) {
			// if is ipv6 address do not consider
			if (hostAddres.contains(":")) {
				return false;
			}
			// splits the ip HC string address 
			String[] octects = toMatchParam.split("\\.");
			// splits the local IP address
			String[] octectsIA = hostAddres.split("\\.");
			// composes again strings which represent
			// the two ip addresses
			String first3 = octects[ELEMENT_1] + "." + octects[ELEMENT_2] + "." + octects[ELEMENT_3];
			String firstIA = octectsIA[ELEMENT_1] + "." + octectsIA[ELEMENT_2] + "." + octectsIA[ELEMENT_3];
			// compares the ip addresses
			if (!first3.equals(firstIA)) {
				return false;
			}
			// gets last octect
			String lastOctect = octects[ELEMENT_4];
			// gets the range of IP address
			String[] ranges = lastOctect.split("-");
			// min and max of ip address
			int min = Integer.parseInt(ranges[ELEMENT_1]);
			int max = Integer.parseInt(ranges[ELEMENT_2]);
			// parses the last octect as integer
			int inetAddressLastOctect = Integer.parseInt(octectsIA[ELEMENT_4]);
			// checks if the last octect is inside
			// of the range of HC config
			return inetAddressLastOctect >= min && inetAddressLastOctect <= max;
		} else {
			// checks the 2 ip addresses by regex
			String toMatch = toMatchParam.replace(".", "\\.").replace("*", "\\w*");
			return hostAddres.matches(toMatch);
		}
	}
}