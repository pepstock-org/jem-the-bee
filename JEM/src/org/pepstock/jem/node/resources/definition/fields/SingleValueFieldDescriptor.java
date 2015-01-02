/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.node.resources.definition.fields;

import java.io.Serializable;

import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;

/**
 * This class represent a single-selectedValue resource field.
 * Value si intended to be free-typed. Regular Expression validation of it is supported.
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class SingleValueFieldDescriptor extends AbstractFieldDescriptor implements Serializable, ResourcePartDescriptor {

	private static final long serialVersionUID = 2636121542239371494L;

	private String selectedValue = null;
	private String defaultValue = null;

	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public SingleValueFieldDescriptor(String key, String label) {
		super(key, label);
	}
	
	/**
	 * @return the field selectedValue
	 */
	public String getSelectedValue() {
		return selectedValue;
	}

	/**
	 * Sets the field selectedValue
	 * @param value the field selectedValue
	 */
	public void setSelectedValue(String value) {
		this.selectedValue = value;
	}

	/**
	 * @return a String that will be used as default (pre-compiled) field selectedValue 
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default field selectedValue
	 * @param defaultValue a String that will be used as default (pre-compiled) field falue
	 */
	public void setDefaultValue(String defaultValue) {
		if (defaultValue != null && defaultValue.trim().isEmpty()) {
			this.defaultValue = null;
		} else {
			this.defaultValue = defaultValue;
		}
	}

	/**
	 * @return <code>true</code> if this field has a default value
	 */
	public boolean hasDefaultValue() {
		return defaultValue != null;
	}

	@Override
	public String toString() {
		return "SingleValueFieldDescriptor [toString()=" + super.toString() + ", selectedValue=" + selectedValue + ", defaultValue=" + defaultValue + "]";
	}
	
}
