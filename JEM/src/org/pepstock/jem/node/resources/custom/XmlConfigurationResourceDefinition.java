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
package org.pepstock.jem.node.resources.custom;

import org.pepstock.jem.node.resources.custom.engine.ResourceTemplateReader;

/**
 * This is an abstract class that implements {@link ResourceDefinition}. <br>
 * In particular this class is responsible for loading the {@link ResourceDescriptor} from a 
 * <code>xml</code> resource template file for resource user interface.
 * When a new resource type is needed, <code>ResourceDefinition</code> must be implemented creating
 * a new specific class, for example <code>TestResourceDefinition</code>. <br>
 * If no file configuration is needed by <code>TestResourceDefinition</code>, it may
 * implement <code>ResourceDefinition</code>. <br>
 * Otherwise if file configuration is needed <code>TestResourceDefinition</code> must
 * extends <code>XmlConfigurationResourceDefinition</code>.
 * 
 * @see ResourceDefinition
 * @see ResourceDescriptor
 * @see ResourceTemplateReader
 * 
 * @author Alessandro Zambrini
 */
public abstract class XmlConfigurationResourceDefinition implements ResourceDefinition{
	
	private static final long serialVersionUID = -6651706310600815923L;
	
	/**
	 * It is the {@link ResourceDescriptor} loaded from <code>xml</code> file.
	 */
	private ResourceDescriptor resourceDescriptor = null;
	
	/**
	 * Sets the {@link ResourceDescriptor}.
	 * @param resourceDescriptor the {@link ResourceDescriptor} to set.
	 */
	public void setResourceDescriptor(ResourceDescriptor resourceDescriptor){
		this.resourceDescriptor = resourceDescriptor;
	}
	
	/**
	 * This method returns the {@link ResourceDescriptor} obtained reading the 
	 * <code>xml</code> file.
	 * @return the {@link ResourceDescriptor} associated to the new type of Resource.
	 */
	public ResourceDescriptor getResourceDescriptor(){
		return this.resourceDescriptor;
	}
	
	
	/**
	 * It loads the {@link ResourceDescriptor} from a <code>xml</code> resource template file for resource user interface.
	 * 
	 * @param resourceTemplateFile the resource template file path with the definition of the resource user interface.
	 * @throws ResourceDefinitionException if the resource template file directory does not exist, or is not a directory,
	 * or is the File System root directory.
	 * 
	 * @see ResourceTemplateReader
	 */
	public void loadResourceTemplateFile(String resourceTemplateFile) throws ResourceDefinitionException{
		ResourceTemplateReader resourceTemplateReader = new ResourceTemplateReader(resourceTemplateFile, this);
		resourceTemplateReader.initialize();
	}

}
