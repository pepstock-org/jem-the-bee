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
package org.pepstock.jem.jbpm.tasks;

import java.io.Serializable;

/**
 * Is used for data source definition, when you want to override a a default
 * value of resource definition.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2	
 *
 */
public class Property implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String name = null;
	
	private boolean custom = false;
	
	private StringBuilder value = new StringBuilder();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @param text content of dataset element
	 */
	public void addText(String text) {
		value.append(text);
	}

	/**
	 * Returns the text value.
	 * 
	 * @return the text value
	 */
	public StringBuilder getText() {
		return value;
	}

	/**
	 * @return the custom
	 */
	public boolean isCustom() {
		return custom;
	}


	/**
	 * @param custom the custom to set
	 */
	public void setCustom(boolean custom) {
		this.custom = custom;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "Property [name=" + name + ", custom=" + custom + ", value=" + value + "]";
    }
	
}