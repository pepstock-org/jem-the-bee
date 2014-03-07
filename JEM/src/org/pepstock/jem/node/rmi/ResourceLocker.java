/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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

import java.rmi.RemoteException;
import java.util.List;

import org.pepstock.jem.node.ResourceLock;
import org.pepstock.jem.util.rmi.RmiObject;

/**
 * is RMI interface for locking and unlocking resources to GRS.<br>
 * This is used inside of process where job is executing, when data descriptions
 * and datasets are managed.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface ResourceLocker extends RmiObject {

	/**
	 * RMI object ID for binding.
	 */
	String NAME = "RESOURCE_LOCKER";

	/**
	 * Asks to GRS to lock the resources.
	 * 
	 * @param jobId
	 * @param resources resources to lock
	 * @throws RemoteException occurs if errors
	 */
	void lock(String jobId, List<ResourceLock> resources) throws RemoteException;

	/**
	 * Asks to GRS to unlock the resources, previously locked.
	 * 
	 * @param jobId 
	 * @throws RemoteException occurs if errors
	 */
	void unlock(String jobId) throws RemoteException;

}