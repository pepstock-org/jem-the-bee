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
package org.pepstock.jem.springbatch.tasks;

import java.io.Serializable;

import org.pepstock.jem.springbatch.tasks.DataSource;
/**
 * Bean used in SPRING to sets property of Data source bean.
 * 
 * @see DataSource
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class Property implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name = null;
	
	private String value = null;
	
	private boolean custom = false;

	/**
	 * Empty constructor
	 */
	public Property() {
	}
	
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
		return "Property [name=" + name + ", value=" + value + ", custom=" + custom + "]";
	}
}