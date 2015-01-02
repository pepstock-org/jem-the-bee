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
package org.pepstock.jem.node.resources.definition.fields;

import java.io.Serializable;

import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;

/**
 * This class represents a free-text field, with optional regular expression validator
 * @author Marco "Fuzzo" Cuccato
 */
public class TextFieldDescriptor extends SingleValueFieldDescriptor implements Serializable, ResourcePartDescriptor, SingleValueField {

	private static final long serialVersionUID = 8869915360528902211L;

	private String regExValidator = null;

	/**
	 * For serialization
	 */
	public TextFieldDescriptor() {
		this(null, null);
	}
	
	/**
	 * Builds the field
	 * @param key
	 * @param label
	 */
	public TextFieldDescriptor(String key, String label) {
		super(key, label);
	}

	/**
	 * @return a regular expression that will be used to validate the field value 
	 */
	public String getRegExValidator() {
		return regExValidator;
	}

	/**
	 * Set a regular expression that will be used to validate the field value
	 * @param regExValidator
	 */
	public void setRegExValidator(String regExValidator) {
		this.regExValidator = regExValidator;
	}

	@Override
	public String toString() {
		return "TextFieldDescriptor [toString()=" + super.toString() + ", regExValidator=" + regExValidator + "]";
	}

}
