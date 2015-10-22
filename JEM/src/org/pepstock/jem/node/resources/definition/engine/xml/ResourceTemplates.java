/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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
package org.pepstock.jem.node.resources.definition.engine.xml;

import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a list of resource templates
 * in the resource templates <code>xml</code> file. <br>

 * @see XStream
 * @author Alessandro Zambrini
 */
public class ResourceTemplates {
	
	/**
	 * Corresponding tag in the resource templates <code>xml</code> file.
	 * @see XStream
	 */
	public static final String MAPPED_XML_TAG = "resource-templates";
	
	/**
	 * Name of the resourceTemplates field. 
	 * @see XStream
	 */
	public static final String RESOURCE_TEMPLATES_FIELD = "resourceTemplatesSet";
	
	/**
	 * The list of the resource templates. <br>
	 */
	private final Set<ResourceTemplate> resourceTemplatesSet = new HashSet<ResourceTemplate>();
	
	/**
	 * Adds a resource template in the list of the resource templates.
	 * @param resourceTemplate the resource template to be added.
	 * @see ResourceTemplate
	 */
	public void add(ResourceTemplate resourceTemplate) {
		this.resourceTemplatesSet.add(resourceTemplate);
	}

	/**
	 * Returns the list of the resource templates.
	 * @return the list of the resource templates.
	 */
	public Set<ResourceTemplate> getContent() {
		return this.resourceTemplatesSet;
	}

}
