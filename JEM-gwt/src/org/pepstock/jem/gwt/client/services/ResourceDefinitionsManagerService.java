package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all actions for custom
 * common resources management
 * 
 * @author Marco "Fuzzo" Cuccato
 */
@RemoteServiceRelativePath(Services.RESOURCE_DEFINITIONS)
public interface ResourceDefinitionsManagerService extends RemoteService {

	/**
	 * Returns all custom resource names
	 * 
	 * @return a collection containing all custom resource names
	 * @throws JemException
	 *             if error occours
	 */
	Collection<String> getAllResourceNames() throws JemException;

	/**
	 * Returns all custom {@link ResourceDescriptor}s
	 * 
	 * @return a collection containing all {@link ResourceDescriptor}
	 * @throws JemException
	 *             if error occours
	 */
	Collection<ResourceDescriptor> getAllResourceDescriptors() throws JemException;

	/**
	 * Get the descriptor specified by name
	 * 
	 * @param resourceType
	 *            the name of {@link ResourceDescriptor}
	 * @return the {@link ResourceDescriptor} identified by
	 *         <code>resourceName</code>
	 * @throws JemException
	 *             if error occours
	 */
	ResourceDescriptor getDescriptorOf(String resourceType) throws JemException;
}
