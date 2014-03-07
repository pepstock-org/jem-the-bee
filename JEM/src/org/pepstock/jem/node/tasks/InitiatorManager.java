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
package org.pepstock.jem.node.tasks;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.ResourceLock;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.rmi.ResourceLocker;
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
public class InitiatorManager {
	
	/**
	 * To avoid any instantiation
	 */
	private InitiatorManager() {
		
	}

	/**
	 * Lookup for ResourceLocker, necessary to use to lock resources by GRS.<br>
	 * Reads the RMI port of node inside of system property.
	 * 
	 * @see org.pepstock.jem.node.rmi.ResourceLocker
	 * @see org.pepstock.jem.util.rmi.RmiKeys#JEM_RMI_PORT
	 * 
	 * @return resource locker
	 * @throws RemoteException occurs if RMI errors
	 * @throws UnknownHostException
	 */
	public static ResourceLocker getResourceLocker() throws RemoteException, UnknownHostException {
		ResourceLocker locker = null;

		// get RMI port from system property
		// job task did that
		String port = System.getProperty(RmiKeys.JEM_RMI_PORT);
		if (port == null){
			throw new RemoteException(NodeMessage.JEMC141E.toMessage().getFormattedMessage(RmiKeys.JEM_RMI_PORT));
		}

		// creates the RMI locator
		RegistryLocator locator = new RegistryLocator(Parser.parseInt(port));
		if (locator.hasRmiObject(ResourceLocker.NAME)) {
			// if object is binded, get it
			locker = (ResourceLocker) locator.getRmiObject(ResourceLocker.NAME);
		} else {
			// object is not binded so Exception
			throw new RemoteException(NodeMessage.JEMC142E.toMessage().getFormattedMessage(ResourceLocker.NAME));
		}
		return locker;
	}

	/**
	 * Lookup for CommonResourcer., necessary to use to have resources by JEM.<br>
	 * Reads the RMI port of node inside of system property.
	 * 
	 * @see org.pepstock.jem.node.rmi.CommonResourcer
	 * @see org.pepstock.jem.util.rmi.RmiKeys#JEM_RMI_PORT
	 * 
	 * @return common resourcer 
	 * @throws RemoteException occurs if RMI errors
	 * @throws UnknownHostException
	 */
	public static CommonResourcer getCommonResourcer() throws RemoteException, UnknownHostException {
		CommonResourcer resourcer = null;

		// get RMI port from system property
		// job task did that
		String port = System.getProperty(RmiKeys.JEM_RMI_PORT);
		if (port == null){
			throw new RemoteException(NodeMessage.JEMC141E.toMessage().getFormattedMessage(RmiKeys.JEM_RMI_PORT));
		}

		// creates the RMI locator
		RegistryLocator locator = new RegistryLocator(Parser.parseInt(port));
		if (locator.hasRmiObject(CommonResourcer.NAME)) {
			// if object is binded, get it
			resourcer = (CommonResourcer) locator.getRmiObject(CommonResourcer.NAME);
		} else {
			// object is not binded so Exception
			throw new RemoteException(NodeMessage.JEMC142E.toMessage().getFormattedMessage(CommonResourcer.NAME));
		}
		return resourcer;
	}

	/**
	 * Reads all datasets implementation instances to check if they must be
	 * resources to lock or not.<br>
	 * If yes, ResourceLock instances are created and add to a list.
	 * 
	 * @param ddImpl data description implementation
	 * @param resources list of resources to be locked
	 */
	public static void addResourcesLock(DataDescriptionImpl ddImpl, List<ResourceLock> resources) {
		// sysout data description doesn't be a resource to lock because
		// its implementation if a temporary file created at runtime and
		// locally, so that
		// no locks are necessary
		if (ddImpl.isSysout()){
			return;
		}

		for (DataSetImpl dataset : ddImpl.getDatasets()) {
			// if dataset is inline text or temporary, skip because
			// its implementation if a temporary file created at runtime and
			// locally, so that
			// no locks are necessary
			if (!(dataset.getType() == DataSetType.RESOURCE) && !(dataset.getType() == DataSetType.INLINE) && !(dataset.getType() == DataSetType.TEMPORARY)) {
				// creates resource lock
				// if disposition is SHR mode, then use READ mode of GRS
				// otherwise WRITE
				// remember that GRS thinks in READ or WRITE
				// disposition are SHR, OLD, MOD, NEW
				ResourceLock resource = new ResourceLock(dataset.getName(), (ddImpl.getDisposition().equalsIgnoreCase(Disposition.SHR)) ? ResourceLock.READ_MODE : ResourceLock.WRITE_MODE);
				// add to list
				resources.add(resource);
			}
		}
	}

}