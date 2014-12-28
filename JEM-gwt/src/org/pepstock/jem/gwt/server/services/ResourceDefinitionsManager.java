/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.server.services;

import java.util.Collection;

import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.node.executors.resources.GetResourceDescriptor;
import org.pepstock.jem.node.executors.resources.GetResourceDescriptors;
import org.pepstock.jem.node.executors.resources.GetResourceNames;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;

/**
 * This service manages all common custom resource definitions inside JEM.
 *  
 * @author Marco "Fuzzo" Cuccato
 */
public class ResourceDefinitionsManager extends DefaultService {

	/**
	 * Gets all custom resource names. Is intended to be used by GUI to build new resource selector.
	 * @return a collection of String
	 * @throws ServiceMessageException 
	 */
	public Collection<String> getAllResourceNames() throws ServiceMessageException  {
		String permission = Permissions.RESOURCES_READ;
		checkAuthorization(new StringPermission(permission));
		
		DistributedTaskExecutor<Collection<String>> task = new DistributedTaskExecutor<Collection<String>>(new GetResourceNames(), getMember());
		return task.getResult();
	}
	
	/**
	 * Get the {@link ResourceDescriptor} identified by <code>resourceName</code>
	 * @param resourceType the resource name
	 * @return the resource descriptor
	 * @throws ServiceMessageException 
	 */
	public ResourceDescriptor getDescriptorOf(String resourceType) throws ServiceMessageException  {
		String permission = Permissions.RESOURCES_READ;
		checkAuthorization(new StringPermission(permission));
	
		DistributedTaskExecutor<ResourceDescriptor> task = new DistributedTaskExecutor<ResourceDescriptor>(new GetResourceDescriptor(resourceType), getMember());
		return task.getResult();
	}

	/**
	 * Gets all custom common resource descriptors. Is intended to be used by GUI to builds panel.
	 * @return a collection of {@link ResourceDescriptor}
	 * @throws ServiceMessageException 
	 */
	public Collection<ResourceDescriptor> getAllDescriptors() throws ServiceMessageException  {
		String permission = Permissions.RESOURCES_READ;
		checkAuthorization(new StringPermission(permission));

		DistributedTaskExecutor<Collection<ResourceDescriptor>> task = new DistributedTaskExecutor<Collection<ResourceDescriptor>>(new GetResourceDescriptors(), getMember());
		return task.getResult();
	}

}
