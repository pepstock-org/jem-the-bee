/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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

import com.thoughtworks.xstream.XStream;

/**
 * This class represent a list field in the resource templates <code>xml</code> file.
 * @see XStream

 * @author Alessandro Zambrini
 */
public class ListFieldTemplate extends MultiSelectableListFieldTemplate {

	private static final long serialVersionUID = -6580958988417969907L;

	/**
	 * Corresponding tag in the resource templates <code>xml</code> file.
	 * @see XStream
	 */
	public static final String MAPPED_XML_TAG = "list-field";
	
	/**
	 * Name of the regExValidator attribute. 
	 * @see XStream
	 */
	public static final String REG_EX_VALIDATOR_ATTRIBUTE = "regExValidator";
	
	/**
	 * The optional regular expression to validate the text field value.
	 */
	private String regExValidator = null;
	
	/**
	 * Returns the optional regular expression to validate the text field value.
	 * @return a regular expression that will be used to validate the field value 
	 */
	public String getRegExValidator() {
		return regExValidator;
	}

	/**
	 * Set a regular expression that will be used to validate the field value.
	 * @param regExValidator the optional regular expression to validate the text field value.
	 */
	public void setRegExValidator(String regExValidator) {
		this.regExValidator = regExValidator;
	}
}
