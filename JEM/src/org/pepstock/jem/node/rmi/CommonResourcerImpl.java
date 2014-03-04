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
package org.pepstock.jem.node.rmi;

import java.rmi.RemoteException;

import javax.naming.Reference;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.custom.ResourceDefinition;
import org.pepstock.jem.node.resources.custom.ResourceDefinitionException;
import org.pepstock.jem.node.security.Permissions;

import com.hazelcast.core.IMap;

/**
 * is RMI implementation uses by step to lookup a common resource by name.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class CommonResourcerImpl extends AuthorizedDefaultRmiObject implements CommonResourcer {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the object calling the super class.
	 * 
	 * @throws RemoteException occurs if errors
	 */
	public CommonResourcerImpl() throws RemoteException {
		super();
	}

	/**
	 * Checks if the key is present inside the map managed by Hazelcast.<br>
	 * If exists, returns the resource object. 
	 * 
	 * @throws RemoteException if the resource anme is not present
	 * @see org.pepstock.jem.node.rmi.CommonResources#attributes(java.lang.String)
	 */
	@Override
	public Resource lookup(String jobId, String resourceName) throws RemoteException {
		// checks if you have resource read permission or
		// data source usage permission
		try {
			checkAuthorization(jobId, Permissions.RESOURCES_READ);
		} catch (Exception e){
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			checkAuthorization(jobId, Permissions.DATASOURCES_ALL + Permissions.PERMISSION_SEPARATOR + resourceName);
		}
		// gets common resource Map
		IMap<String, Resource> map = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		// the key exists?
		if (map.containsKey(resourceName)){
			// if yes, returns the object
			Resource resource = null;
			try {
				map.lock(resourceName);
				resource = map.get(resourceName);
			} finally {
				map.unlock(resourceName);
			}
			return resource;
		}
		// otherwise exception
		throw new RemoteException(NodeMessage.JEMC122E.toMessage().getFormattedMessage(resourceName));
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.rmi.CommonResourcer#lookupCustomResource(java.lang.String)
	 */
	@Override
	public Reference lookupCustomResource(String jobId, String resourceType) throws RemoteException {
		// checks if you have resource read permission
		checkAuthorization(jobId, Permissions.RESOURCES_READ);
			try {
				if (Main.CUSTOM_RESOURCE_DEFINITION_MANAGER.hasCustomResourceDefinition(resourceType)) {
					ResourceDefinition resourceDefinition = Main.CUSTOM_RESOURCE_DEFINITION_MANAGER.getCustomResourceDefinition(resourceType);
					return resourceDefinition.getResourceReference();
				}
			} catch (ResourceDefinitionException e) {
				throw new RemoteException(e.getMessage(), e);
			}
		return null;
	}

}