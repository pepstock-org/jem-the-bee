/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
 * This class represents a checkbox field
 * @author Marco "Fuzzo" Cuccato
 */
public class CheckBoxFieldDescriptor extends SingleValueFieldDescriptor implements Serializable, ResourcePartDescriptor, BooleanValueField {

	private static final long serialVersionUID = 0L;

	/**
	 * For serialization
	 */
	public CheckBoxFieldDescriptor() {
		this(null, null);
	}
	
	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public CheckBoxFieldDescriptor(String key, String label) {
		super(key, label);
	}

	/**
	 * @return the defaultValue
	 */
	public boolean isDefaultValue() {
		return Boolean.parseBoolean(super.getDefaultValue());
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(boolean defaultValue) {
		super.setDefaultValue(String.valueOf(defaultValue));
	}

	@Override
	public String toString() {
		return "CheckBoxFieldDescriptor [toString()=" + super.toString() + "]";
	}
	
}
