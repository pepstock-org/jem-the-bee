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
package org.pepstock.jem.node.executors.resources;

import org.apache.shiro.subject.ExecutionException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDefinition;
import org.pepstock.jem.node.resources.definition.ResourceDefinitionException;

import com.hazelcast.core.IMap;

/**
 * Adds a new resource using the resource definition to add all hidden properties,if there are.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class AddResource extends DefaultExecutor<Boolean>{

	private static final long serialVersionUID = 1L;
	
	private Resource resource = null;

	/**
	 * Creates object passing the resource to be added
	 * @param resource the resource to be added
	 */
	public AddResource(Resource resource) {
		super();
		this.resource = resource;
	}
	/**
	 * Locks, checks if the resource definition exists for the resource type 
	 * 
	 * @return always true
	 * @throws Exception occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		// gets map
		IMap<String, Resource> map = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		try {
			// locks the key
			map.lock(resource.getName());

			// get resource definition
			ResourceDefinition definition = Main.RESOURCE_DEFINITION_MANAGER.getResourceDefinition(resource.getType());
			// adds additional properties
			definition.completeResource(resource);

			// puts on the map
			map.put(resource.getName(), resource);
		} catch (ResourceDefinitionException e) {
			throw new ExecutionException(e);
		} finally {
			// unlocks always the key
			map.unlock(resource.getName());
		}
		return Boolean.TRUE;
	}
}