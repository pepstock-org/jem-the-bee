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
package org.pepstock.jem.rest.services;

import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.Message;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Abstract REST manager, which contains th REST client to use on REST calls.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class AbstractRestManager {
	
	private RestClient client = null; 
	
	private String mainPath = null;

	/**
 	 * Creates a new REST manager using a RestClient
	 * @param restClient REST client instance
	 * @param mainPath fist path of RESt service
	 */
	public AbstractRestManager(RestClient restClient, String mainPath) {
		this.client = restClient;
		this.mainPath = mainPath;
	}

	/**
	 * @return the client
	 */
	RestClient getClient() {
		return client;
	}
	
	/**
	 * @return the mainPath
	 */
	String getMainPath() {
		return mainPath;
	}

	@SuppressWarnings("unchecked")
	<T> T getValue(ClientResponse response, Class<T> clazz){
		Message msg = response.getEntity(Message.class);
		return (T) msg.getValue();
	}
}
