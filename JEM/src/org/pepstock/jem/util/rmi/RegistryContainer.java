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
package org.pepstock.jem.util.rmi;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.util.UtilMessage;

/**
 * Contains the RMI registry where binds objects.<br>
 * Is a singleton to have only one registry for JVM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RegistryContainer {

	private static RegistryContainer CONTAINER = null;

	private Registry registry = null;

	private int port = Registry.REGISTRY_PORT;

	/**
	 * Constructs object with port to use by RMI registry to stay in listening
	 * mode.
	 * 
	 * @param port port to use by RMI registry
	 * @throws RemoteException occurs if RMI has errors
	 */
	private RegistryContainer(int port) throws RemoteException {
		registry = LocateRegistry.createRegistry(port, null, new NodeRmiSocketFactory());
		this.port = port;
	}

	/**
	 * Is the static method to have the object (typical for a singleton). Before
	 * to call it, create method must be performed, oitherwise a runtime
	 * exception occurs.
	 * 
	 * @return container instance
	 */
	public static RegistryContainer getInstance() {
		if (CONTAINER != null) {
			return CONTAINER;
		}
		throw new MessageRuntimeException(UtilMessage.JEMB001E);
	}

	/**
	 * Creates a instance of container, with port to use by RMI registry to stay
	 * in listening mode.
	 * 
	 * @param port port to use by RMI registry
	 * @return container instance
	 * @throws RemoteException occurs if RMI has errors
	 */

	public static synchronized RegistryContainer createInstance(int port) throws RemoteException {
		if (CONTAINER == null) {
			CONTAINER = new RegistryContainer(port);
		}
		return CONTAINER;
	}

	/**
	 * Returns the port number of RMI registry.
	 * 
	 * @return port to use by RMI registry
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the RMI registry instance.
	 * 
	 * @return RMI registry instance
	 */
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Binds RMI objects inside the registry, ready to use
	 * 
	 * @param name name use to bind object
	 * @param obj RMI object instance
	 * @throws RemoteException occurs if RMI has errors
	 */
	public void addRmiObject(String name, RmiObject obj) throws RemoteException {
		// before binds, unbinds existing objects with the same name
		try {
			registry.unbind(name);
		} catch (NotBoundException nbe) {
			// ignore
			LogAppl.getInstance().ignore(nbe.getMessage(), nbe);
			// nop
			LogAppl.getInstance().emit(UtilMessage.JEMB006I, name);
		} catch (AccessException ae) {
			// ignore
			LogAppl.getInstance().ignore(ae.getMessage(), ae);
			// nop
			LogAppl.getInstance().emit(UtilMessage.JEMB006I, name);
		} catch (RemoteException re) {
			// ignore
			LogAppl.getInstance().ignore(re.getMessage(), re);
			// nop
			LogAppl.getInstance().emit(UtilMessage.JEMB006I, name);
		}

		// And then binds teh passed object by name
		try {
			registry.bind(name, obj);
		} catch (AlreadyBoundException abe) {
			throw new RemoteException(UtilMessage.JEMB003E.toMessage().getFormattedMessage(abe.getMessage()), abe);
		} catch (AccessException ae) {
			throw new RemoteException(UtilMessage.JEMB003E.toMessage().getFormattedMessage(ae.getMessage()), ae);
		} catch (RemoteException re) {
			throw re;
		}
	}
}