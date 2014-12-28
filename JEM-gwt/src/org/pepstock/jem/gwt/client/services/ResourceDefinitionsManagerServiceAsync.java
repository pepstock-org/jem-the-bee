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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Marco "Fuzzo" Cuccato
 */
public interface ResourceDefinitionsManagerServiceAsync {

	/**
	 * Return a collection containing all custom {@link ResourceDescriptor}
	 * 
	 * @param callback
	 */
	void getAllResourceDescriptors(AsyncCallback<Collection<ResourceDescriptor>> callback);

	/**
	 * Returns all custom resource names
	 * 
	 * @param callback
	 */
	void getAllResourceNames(AsyncCallback<Collection<String>> callback);

	/**
	 * Return the descriptor of resource identified by <code>resourceName</code>
	 * 
	 * @param resourceType
	 *            the resource name
	 * @param callback
	 */
	void getDescriptorOf(String resourceType, AsyncCallback<ResourceDescriptor> callback);

}
