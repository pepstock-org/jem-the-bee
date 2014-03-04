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
package org.pepstock.jem.node.multicast.messages;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessageException;

/**
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class MulticastMessageFactory {

	/**
	 * To avoid any instntiation
	 */
	private MulticastMessageFactory() {
		
	}

	/**
	 * 
	 * @param message the multicast message
	 * @return an instance of the following classes based on the message:
	 *         <p>
	 *         {@link ClientRequest}
	 *         <p>
	 *         {@link NodeResponse}
	 *         <p>
	 *         {@link ShutDown}
	 *         <p>
	 *         or null if the message cannot be converted in one of the listed
	 *         classes
	 */
	public static MulticastMessage getMessage(String message) {
		MulticastMessage multicastMessage = null;
		try {
			multicastMessage = ClientRequest.unmarshall(message);
			return multicastMessage;
		} catch (NodeMessageException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		try {
			multicastMessage = NodeResponse.unmarshall(message);
			return multicastMessage;
		} catch (NodeMessageException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		try {
			multicastMessage = ShutDown.unmarshall(message);
			return multicastMessage;
		} catch (NodeMessageException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		return null;
	}
}
