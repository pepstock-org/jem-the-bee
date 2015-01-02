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
package org.pepstock.jem.util.rmi;

import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.pepstock.jem.util.UtilMessage;

/**
 * Client part of RMI architecture.<br>
 * Locates object binded inside of a registry.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RegistryLocator {

	private Registry registry = null;

	/**
	 * Constructs using a port of RMI registry to use.<br>
	 * By default, it uses localhost as server instance.
	 * 
	 * @param port registry port
	 * @throws RemoteException occurs if RMI errors
	 * @throws UnknownHostException occurs if network host name resolution has
	 *             errors
	 */
	public RegistryLocator(int port) throws RemoteException, UnknownHostException {
		if (registry == null) {
			registry = LocateRegistry.getRegistry(port);
			registry.list();
		}
	}

	/**
	 * Returns <code>true</code> if a object is binded inside of registry with
	 * passed name
	 * 
	 * @param name name of object binded
	 * @return <code>true</code> if a object is binded inside of registry with
	 *         passed name
	 * @throws RemoteException occurs if RMI errors
	 */
	public boolean hasRmiObject(String name) throws RemoteException {
		return getRmiObject(name) != null;
	}

	/**
	 * Returns the object binded inside of registry with passed name
	 * 
	 * @param name name of object binded
	 * @return object binded
	 * @throws RemoteException occurs if RMI errors
	 */
	public RmiObject getRmiObject(String name) throws RemoteException {
		try {
			return (RmiObject) registry.lookup(name);
		} catch (NotBoundException nbe) {
			throw new RemoteException(UtilMessage.JEMB003E.toMessage().getFormattedMessage("lookup", nbe.getMessage()), nbe);
		} catch (AccessException ae) {
			throw new RemoteException(UtilMessage.JEMB003E.toMessage().getFormattedMessage("lookup", ae.getMessage()), ae);
		} catch (RemoteException re) {
			throw re;
		}
	}

	/**
	 * Returns all objects bined in registry
	 * 
	 * @return array with all objects binded
	 * @throws RemoteException occurs if RMI errors
	 */
	public Object[] getAllRmiObjects() throws RemoteException {
		try {
			return registry.list();
		} catch (AccessException ae) {
			throw new RemoteException(UtilMessage.JEMB003E.toMessage().getFormattedMessage("list", ae.getMessage()), ae);
		} catch (RemoteException re) {
			throw re;
		}
	}
}