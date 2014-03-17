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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * Is a class with common utilities for multicast service
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class MulticastUtils {

	/**
	 * 
	 * @param interfaces
	 * @return the correct inetAddress of the node that matches the first
	 *         interface present in the list of interfaces read in the hazelcast
	 *         configuration
	 * @throws Exception
	 */
	public static InetAddress getIntetAddress(Collection<String> interfaces) throws Exception {
		for (String toMatch : interfaces) {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					if (doMatch(inetAddress, toMatch)) {
						LogAppl.getInstance().emit(NodeMessage.JEMC248I, inetAddress);
						return inetAddress;
					}
				}
			}
		}
		throw new Exception(NodeMessage.JEMC249W.toMessage().getFormattedMessage());
	}

	/**
	 * 
	 * @param inetAddress
	 * @param toMatch can be either an IP, an IP with wild card "*" on the last
	 *            octet or an IP with a range on last octet (e.g. 196.168.35.10-100)
	 * @return true if the toMatch string matches the hostAddress from the
	 *         InetAddress
	 * @throws Exception
	 */
	private static boolean doMatch(InetAddress inetAddress, String toMatch) throws Exception {
		String hostAddres = inetAddress.getHostAddress();
		if (toMatch.contains("-")) {
			// if is ipv6 address do not consider
			if (hostAddres.contains(":")) {
				return false;
			}
			String[] octects = toMatch.split("\\.");
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
			return (inetAddressLastOctect >= min && inetAddressLastOctect <= max);
		} else {
			toMatch = toMatch.replace(".", "\\.");
			toMatch = toMatch.replace("*", "\\w*");
			return hostAddres.matches(toMatch);
		}
	}
}
