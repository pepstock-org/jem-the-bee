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
package org.pepstock.jem.node.resources.custom.engine.xml;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a single selection custom resource field in the resource templates <code>xml</code> file. <br>
 * This kind of field represent a field in which to make only one choice between many: radio button or combo box. <br>
 * It contains all of the possible values of this field. Only one can be selected.
 * @see XStream
 *  
 * @author Alessandro Zambrini
 */
public class SingleSelectableListFieldTemplate extends SingleValueFieldTemplate {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Corresponding tag in the resource templates <code>xml</code> file.
	 * @see XStream
	 */
	public static final String MAPPED_XML_TAG = "single-selectable-list-field";
	
	/**
	 * Name of the renderAsRadio attribute. 
	 * @see XStream
	 */
	public static final String RENDER_AS_RADIO_ATTRIBUTE = "renderAsRadio";
	
	/**
	 * Name of the values field. 
	 * @see XStream
	 */
	public static final String VALUES_FIELD = "values";

	/**
	 * The property that indicates whether the field is a radio button (<code>true</code> value)
	 * or a combo box (<code>false</code> value).
	 */
	private boolean renderAsRadio = false;
	
	/**
	 * The list of the possible values of the field.
	 */
	private LinkedList<ValueTemplate> values = new LinkedList<ValueTemplate>();
	
	/**
	 * Returns the property that indicates whether the field is a radio button (<code>true</code> value)
	 * or a combo box (<code>false</code> value).
	 * @return <code>true</code> if this field is a radio button, <code>false</code> 
	 * if this field is a combo box.
	 */
	public boolean isRenderAsRadio() {
		return renderAsRadio;
	}

	/**
	 * Sets if this field is a radio button (<code>true</code> value)
	 * or a combo box (<code>false</code> value).
	 * @param renderAsRadio <code>true</code> if you want this field to be 
	 * radio button, <code>false</code> for combo box.
	 */
	public void setRenderAsRadio(boolean renderAsRadio) {
		this.renderAsRadio = renderAsRadio;
	}
	
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
