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
package org.pepstock.jem.node.resources.definition;

import java.io.Serializable;

/**
 * This class represent a generic resource field. 
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class AbstractField implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The key of the resource field.
	 */
	private String key = null;
	
	/**
	 * The label of the resource field.
	 */
	private String label = null;
	
	/**
	 * The description of the resource field.
	 */
	private String description = null;
	
	/**
	 * The property that indicates whether the field is mandatory.
	 */
	private boolean mandatory = false;

	/**
	 * Returns the field key.
	 * @return the field key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the field key.
	 * @param key the key of the field.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Returns the field label.
	 * @return the label of this field
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the field label.
	 * @param label the label of the field.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the field description.
	 * @return a description of this field.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the field description.
	 * @param description the description of the field.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the property that indicates whether the field is mandatory.
	 * @return <code>true</code> if this field needs to be filled/selected, <code>false</code> 
	 * if this field is optional and can not be filled/selected
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	
	/**
	 * Sets if this field is mandatory or not.
	 * @param mandatory <code>true</code> if you want this field to be 
	 * mandatory, <code>false</code> otherwise.
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public String toString() {
		return "AbstractField [key=" + key + ", label=" + label + ", description=" + description + ", mandatory=" + mandatory + "]";
	}

}
