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
package org.pepstock.jem.util.net;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Bean with the network interface and inet address local which are used by JEM to avoid
 * bugs on Multicast and other connection
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class Interface {
	
	private NetworkInterface networkInterface = null;
	
	private InetAddress address = null;

	/**
	 * @return the networkInterface
	 */
	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * @param networkInterface the networkInterface to set
	 */
	void setNetworkInterface(NetworkInterface networkInterface) {
		this.networkInterface = networkInterface;
	}

	/**
	 * @return the address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	void setAddress(InetAddress address) {
		this.address = address;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Interface [networkInterface=" + networkInterface + ", address=" + address + "]";
	}
}
