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
 * This class represent a single-selectedValue resource field in the resource templates <code>xml</code> file.
 * @see XStream

 * @author Alessandro Zambrini
 */
public abstract class SingleValueFieldTemplate extends AbstractFieldTemplate {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the defaultValue attribute. 
	 * @see XStream
	 */
	public static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";

	/**
	 * The default selected value of the this field.
	 */
	private String defaultValue = null;

	/**
	 * Returns the default selected value of the this field.
	 * @return a String that represent the default selected value of the this field.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default selected value of this field.
	 * @param defaultValue a String that will be used as default selected value of the this field.
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
