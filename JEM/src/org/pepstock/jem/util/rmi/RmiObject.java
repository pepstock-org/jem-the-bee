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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Common RMI interface, necesary to use Registry container and locator.<br>
 * Only a "alive" method is inserted.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface RmiObject extends Remote {

	/**
	 * Called to know if RMI object is still alive or not. If not, don't return
	 * <code>false</code> but an exception occurs.
	 * 
	 * @return <code>true</code> always
	 * @throws RemoteException occurs if RMI registry, and then object is not
	 *             available
	 */
	boolean alive() throws RemoteException;

}