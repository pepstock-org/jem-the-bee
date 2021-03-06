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

/**
 * Implementation of {@link MultiSelectableListFieldDescriptor} that supports free-typed values and optional regular expression validator. 
 * @author Marco "Fuzzo" Cuccato
 */
public class TextListFieldDescriptor extends MultiSelectableListFieldDescriptor {
	
	private static final long serialVersionUID = 7342073674857834445L;
	
	private String regExValidator = null;
	private String listLabel = null;
	
	/**
	 * Builds the field
	 */
	public TextListFieldDescriptor() {
	}

	/**
	 * Builds the field
	 * @param key
	 * @param inputLabel the label for the input field
	 * @param listLabel the label ontop of value list
	 */
	public TextListFieldDescriptor(String key, String inputLabel, String listLabel) {
		super(key, inputLabel);
		this.listLabel = listLabel;
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

	/**
	 * @return the label text on top of value list
	 */
	public String getListLabel() {
		return listLabel;
	}

	/**
	 * Set the label text on top of value list
	 * @param listLabel the list label text
	 */
	public void setListLabel(String listLabel) {
		this.listLabel = listLabel;
	}
	
	/**
	 * @return the input panel label
	 * @see AbstractFieldDescriptor#getLabel() 
	 */
	public String getInputLabel() {
		return getLabel();
	}

	/**
	 * @param inputLabel the desire input panel label
	 * @see AbstractFieldDescriptor#setLabel(String)
	 */
	public void setInputLabel(String inputLabel) {
		setLabel(inputLabel);
	}

	@Override
	public String toString() {
		return "TextListFieldDescriptor [toString()=" + super.toString() + ", regExValidator=" + regExValidator + ", listLabel=" + listLabel + "]";
	}
	
}
