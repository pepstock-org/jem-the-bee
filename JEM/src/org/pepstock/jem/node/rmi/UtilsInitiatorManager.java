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
package org.pepstock.jem.node.rmi;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.rmi.RegistryLocator;
import org.pepstock.jem.util.rmi.RmiKeys;

/**
 * Gets the resource locker object, necessary during the job execution to ask
 * for locking of resources.<br>
 * Creates ResourceLock using DataDescriptionImpl object, adding in a list to
 * pass afterwards for locking.
 * 
 * @see org.pepstock.jem.node.rmi.ResourceLocker
 * @see org.pepstock.jem.node.ResourceLock
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class UtilsInitiatorManager {

	/**
	 * To avoid any instantiation
	 */
    private UtilsInitiatorManager() {
    }

	/**
	 * Lookup for Internal utilities, necessary to use to start and drain nodes.<br>
	 * Reads the RMI port of node inside of system property.
	 * @return internl util object
	 * @throws RemoteException 
	 * @throws UnknownHostException 
	 */
	public static InternalUtilities getInternalUtilities() throws RemoteException, UnknownHostException {
		InternalUtilities util = null;

		// get RMI port from system property
		// job task did that
		String port = System.getProperty(RmiKeys.JEM_RMI_PORT);
		if (port == null){
			throw new RemoteException(NodeMessage.JEMC141E.toMessage().getFormattedMessage(RmiKeys.JEM_RMI_PORT));
		}
		// creates the RMI locator
		RegistryLocator locator = new RegistryLocator(Parser.parseInt(port));
		if (locator.hasRmiObject(InternalUtilities.NAME)) {
			// if object is binded, get it
			util = (InternalUtilities) locator.getRmiObject(InternalUtilities.NAME);
		} else {
			// object is not binded so Exception
			throw new RemoteException(NodeMessage.JEMC142E.toMessage().getFormattedMessage(InternalUtilities.NAME));
		}
		return util;
	}

}