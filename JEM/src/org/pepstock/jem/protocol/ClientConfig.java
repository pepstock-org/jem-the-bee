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
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class ClientConfig extends Credentials {
	
	private static final long serialVersionUID = 1L;
	
	private List<InetSocketAddress> storedAddresses = new ArrayList<InetSocketAddress>();
	
	/**
	 * FIXME
	 * @param addresses
	 */
	public void addAddresses(String... addresses){
		if (addresses != null && addresses.length > 0){
			for (String address : addresses){
				String host = StringUtils.substringBefore(address, ":");
				int port = Parser.parseInt(StringUtils.substringAfter(address, ":"), -1);
				if (port > -1 && host != null){
					InetSocketAddress socketAddress = new InetSocketAddress(host, port);
					storedAddresses.add(socketAddress);
				}
			}
		}
	}

	void clearAndAddAddresses(List<String> addresses){
		storedAddresses.clear();
		addAddresses(addresses.toArray(new String[0]));
	}
	
	/**
	 * @return the storedAddresses
	 */
	public Collection<InetSocketAddress> getStoredAddresses() {
		return Collections.unmodifiableCollection(storedAddresses);
	}
}
