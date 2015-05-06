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
package org.pepstock.jem.node.resources.impl.rmi;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * Is a registry implementation, which wraps a registry instance.
 * <br>
 * If configured in read only, the user can't bind, unbind or rebind any object.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RegistryWrapper implements Registry {
	
	private static final String EXCEPTION_STRING = "Registry in readonly! It can't bind, unbind, rebind objects.";
	
	private Registry delegate = null;
	
	private boolean readOnly = false;

	/**
	 * Builds the registry wrapper, having the arguments, the RMI registry and 
	 * if it has been configured in readOnly.
	 * @param delegate a registry instance to use as delegated
	 * @param readOnly if the user can use only lookup and list
	 */
	RegistryWrapper(Registry delegate, boolean readOnly) {
		super();
		this.delegate = delegate;
		this.readOnly = readOnly;
	}

	/* (non-Javadoc)
	 * @see java.rmi.registry.Registry#lookup(java.lang.String)
	 */
	@Override
	public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
		return delegate.lookup(name);
	}

	/* (non-Javadoc)
	 * @see java.rmi.registry.Registry#bind(java.lang.String, java.rmi.Remote)
	 */
	@Override
	public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {
		if (readOnly){
			throw new RemoteException(EXCEPTION_STRING);
		}
		delegate.bind(name, obj);
	}

	/* (non-Javadoc)
	 * @see java.rmi.registry.Registry#unbind(java.lang.String)
	 */
	@Override
	public void unbind(String name) throws RemoteException, NotBoundException, AccessException {
		if (readOnly){
			throw new RemoteException(EXCEPTION_STRING);
		}
		delegate.unbind(name);
	}

	/* (non-Javadoc)
	 * @see java.rmi.registry.Registry#rebind(java.lang.String, java.rmi.Remote)
	 */
	@Override
	public void rebind(String name, Remote obj) throws RemoteException, AccessException {
		if (readOnly){
			throw new RemoteException(EXCEPTION_STRING);
		}
		delegate.rebind(name, obj);
	}

	/* (non-Javadoc)
	 * @see java.rmi.registry.Registry#list()
	 */
	@Override
	public String[] list() throws RemoteException, AccessException {
		return delegate.list();
	}
}