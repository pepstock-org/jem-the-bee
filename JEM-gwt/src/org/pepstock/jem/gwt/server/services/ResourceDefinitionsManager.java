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
