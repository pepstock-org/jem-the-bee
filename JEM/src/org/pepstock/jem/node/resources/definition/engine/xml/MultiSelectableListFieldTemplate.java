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
package org.pepstock.jem.node.resources.definition.engine.xml;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a resource field with multiple possible selections 
 * in the resource templates <code>xml</code> file. <br>
 * That is, it is the representation of a field in which the user can select
 * more than an item.<br>
 * It contains all of the possible values of this field. They all can be selected.

 * @see XStream
 * @author Alessandro Zambrini
 */
public class MultiSelectableListFieldTemplate extends AbstractFieldTemplate {

	private static final long serialVersionUID = 1L;

	/**
	 * Corresponding tag in the resource templates <code>xml</code> file.
	 * @see XStream
	 */
	public static final String MAPPED_XML_TAG = "multi-selectable-list-field";
	
	/**
	 * Name of the values field. 
	 * @see XStream
	 */
	public static final String VALUES_FIELD = "values";
	
	/**
	 * The list of the possible values of the field.
	 */
	private LinkedList<ValueTemplate> values = new LinkedList<ValueTemplate>();

	/**
	 * Adds a value in the list of the possible values of the field.
	 * @param value the value to be added in the values list.
	 * @see ValueTemplate
	 */
	public void add(ValueTemplate value) {
		this.values.addLast(value);
	}

	/**
	 * Returns the list of the possible values of the field.
	 * @return the list of the possible values of the field.
	 */
	public List<ValueTemplate> getContent() {
		return this.values;
	}
}
