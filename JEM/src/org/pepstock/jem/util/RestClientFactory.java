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
package org.pepstock.jem.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory to create new RestClient, both for multi threading and for single threading.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public final class RestClientFactory {
	
	private static final Map<String, RestClient> CLIENTS = new ConcurrentHashMap<String, RestClient>();

	/**
	 * To avoid any instatiation
	 */
	private RestClientFactory() {
		
	}

	/**
	 * Returns a REST client, passing the URI base of REST
	 * @param uriString URI base string
	 * @return a REST client
	 */
	public static RestClient getClient(String uriString){
		return getClient(uriString, false);
	}
	
	/**
	 * Returns a REST client, passing the URI base of REST
	 * @param uriString URI base string
	 * @param multiThreading <code>true</code> if you want a REST client multi threading. Default is <code>false</code>. 
	 * @return new REST client
	 */
	public static RestClient getClient(String uriString, boolean multiThreading){
		// uses a internal cache
		if (CLIENTS.containsKey(uriString)){
			return CLIENTS.get(uriString);
		}
		// based on multi threading flag
		RestClient newClient = null;
		if (multiThreading){
			newClient = new MultiRestClient(uriString);
		} else {
			newClient = new SingleRestClient(uriString);
		}
		// puts new client on cache
		CLIENTS.put(uriString, newClient);
		return newClient;
	}
	
}
