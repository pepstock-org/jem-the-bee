/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
