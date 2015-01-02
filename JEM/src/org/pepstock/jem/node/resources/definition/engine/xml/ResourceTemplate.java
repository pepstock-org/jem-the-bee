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

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a resource template 
 * in the resource templates <code>xml</code> file. <br>
 * It contains the description of the resource.

 * @see XStream
 * @author Alessandro Zambrini
 */
public class ResourceTemplate {

	/**
	 * Corresponding tag in the resource templates <code>xml</code> file.
	 * @see XStream
	 */
	public static final String MAPPED_XML_TAG = "resource-template";

	/**
	 * Name of the sections field. 
	 * @see XStream
	 */
	public static final String SECTIONS_FIELD = "sections";

	/**
	 * Resource template type.
	 */
	private String type = null;
	
	/**
	 * Resource template type.
	 */
	private String description = null;

	/**
	 * The list of the sections of the resource template. <br>
	 * All the field of a resource may be divided into sections.
	 */
	private List<SectionTemplate> sections = new LinkedList<SectionTemplate>();
	
		
	/**
	 * Return the type of the resource template.
	 * @return the type of the resource template.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the resource template.
	 * @param type the type of the resource template.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Adds a section in the list of the sections of the resource template.
	 * @param section the section to be added in the sections list.
	 * @see SectionTemplate
	 */
	public void add(SectionTemplate section) {
		this.sections.add(section);
	}

	/**
	 * Returns the list of the sections of the the resource template.
	 * @return the list of the sections of the the resource template.
	 */
	public List<SectionTemplate> getContent() {
		return this.sections;
	}
}
