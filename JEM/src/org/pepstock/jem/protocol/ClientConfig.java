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
package org.pepstock.jem.protocol;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.util.Parser;

/**
 * Contains all information necessary to connect to JEM cluster.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class ClientConfig extends Credentials {
	
	private static final long serialVersionUID = 1L;
	
	private List<InetSocketAddress> storedAddresses = new ArrayList<InetSocketAddress>();
	
	/**
	 * Adds a list of addresses which represents JEM cluster
	 * @param addresses nodes of JEM cluster, format [ip]:[port]
	 */
	public void addAddresses(String... addresses){
		// checks if parameter is correct
		if (addresses != null && addresses.length > 0){
			// scans all addresses
			for (String address : addresses){
				// gets ip address of host
				String host = StringUtils.substringBefore(address, ":");
				// gets port
				int port = Parser.parseInt(StringUtils.substringAfter(address, ":"), -1);
				// if has got correct data
				if (port > -1 && host != null){
					// adds to addresses list
					InetSocketAddress socketAddress = new InetSocketAddress(host, port);
					storedAddresses.add(socketAddress);
				}
			}
		}
	}

	/**
	 * Called when the client receives a message from server with all JEM nodes.
	 * @param addresses nodes of JEM cluster, format [ip]:[port], comma separated
	 */
	void clearAndAddAddresses(String addresses){
		// checks parameter
		if (addresses != null){
			// splits by comma
			String[] values = addresses.split(",");
			// checks after split if correct
			if (values != null && values.length > 0){
				// clears the set and adds all addresses
				storedAddresses.clear();
				addAddresses(values);
			}
		}
	}
	
	/**
	 * @return the storedAddresses
	 */
	public Collection<InetSocketAddress> getStoredAddresses() {
		return Collections.unmodifiableCollection(storedAddresses);
	}
}
