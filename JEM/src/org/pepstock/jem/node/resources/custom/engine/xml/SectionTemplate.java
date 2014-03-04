/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.resources.custom.engine.xml;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a section
 * in the resource templates <code>xml</code> file. <br>
 * The fields of a resource may be divided into sections.

 * @see XStream
 * @author Alessandro Zambrini
 */
public class SectionTemplate {

	/**
	 * Corresponding tag in the resource templates <code>xml</code> file.
	 * @see XStream
	 */
	public static final String MAPPED_XML_TAG = "section";

	/**
	 * Name of the name attribute. 
	 * @see XStream
	 */
	public static final String NAME_ATTRIBUTE = "name";

	/**
	 * Name of the fields field. 
	 * @see XStream
	 */
	public static final String FIELDS_FIELD = "fields";

	/**
	 * The name of the section.
	 */
	private String name = null;

	/**
	 * The list of the fields of the section.
	 */
	private LinkedList<AbstractFieldTemplate> fields = new LinkedList<AbstractFieldTemplate>();
	
	/**
	 * Returns the name of the section.
	 * @return the name of the section.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the section.
	 * @param name the name of the section.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Adds a field in the list of the fields.
	 * @param field the field to be added.
	 * @see AbstractFieldTemplate
	 */
	public void add(AbstractFieldTemplate field) {
		this.fields.addLast(field);
	}

	/**
	 * Returns the list of the fields.
	 * @return the list of the fields.
	 */
	public List<AbstractFieldTemplate> getContent() {
		return this.fields;
	}
}
