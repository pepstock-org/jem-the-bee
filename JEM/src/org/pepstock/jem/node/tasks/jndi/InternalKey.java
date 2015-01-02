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
package org.pepstock.jem.node.tasks.jndi;

import java.io.Serializable;

/**
 * Protected bean which contains a key used inside of absolute map to avoid that
 * anyone can access to the data. This key is used to call the singleton by java proxy. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
class InternalKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String value = null;

	/**
	 * Empty constructor, visible only for package
	 */
	InternalKey() {
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
}
