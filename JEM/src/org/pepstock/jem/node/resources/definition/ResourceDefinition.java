/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.node.resources.definition;

import java.io.Serializable;

import javax.naming.Reference;

import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.impl.jms.JmsReference;

/**
 * Interface that must be implemented to add to JEM a new Resource Type. <br>
 * For example you need to add to JEM a new resource type: TestType. You need to to as follows: <br>
 * <li> Create a new {@link Reference} class, for example <code>TestResourceReference</code>
 * (for example similar to {@link JmsReference}) <br>
 * <li> Create a new {@link ResourceDescriptor}, for example <code>TestResourceDescriptor</code> that describes the
 * the editor for that type of resources. <br>
 * <li> Implement this interface <code>ResourceDefinition</code>, for example <code>TestResourceDefinition</code>.
 * {@link #getReference()} must return <code>TestResourceReference</code>
 * {@link #getDescriptor()} must return <code>TestResourceDescriptor</code>
 * <br>
 * Alternatively {@link XmlConfigurationResourceDefinition} must be extended if
 * the <code>ResourceDescriptor</code> must be loaded from file.
 * 
 * @see XmlConfigurationResourceDefinition
 * @author Alessandro Zambrini
 *
 */
public interface ResourceDefinition extends Serializable{
	
	/**
	 * This method returns the {@link Reference} associated to the new type of Resource.
	 * @return the {@link Reference} associated to the new type of Resource.
	 */
	Reference getReference();

	/**
	 * This method returns the {@link ResourceDescriptor} associated to the new type of Resource.
	 * @return the {@link ResourceDescriptor} associated to the new type of Resource.
	 */
	ResourceDescriptor getDescriptor();
	
	/**
	 * This method is called when a resource is added, to enrich with the hidden properties
	 * @param resource resource to be enriched
	 */
	void completeResource(Resource resource);

	/**
	 * Validates a resource, when both a new resource is added and an existing one is changed.
	 * This method should check if all necessary properties are set and if unnecessary properties are set as well.
	 * @param resource resource to be checked
	 * @throws ResourceDefinitionException if any errors occurs during check
	 */
	void validateResource(Resource resource) throws ResourceDefinitionException;
}
