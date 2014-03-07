/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.security;

import java.io.Serializable;
import java.util.List;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class UserPreference implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String valueString = null;
	
	private List<String> valueList = null;
	
	/**
	 * Returns true if value is a string
	 * @return returns true if value is a string
	 */
	public boolean isValueString(){
		return valueString !=null;
	}

	/**
	 * @return the valueString
	 */
	public String getValueString() {
		return valueString;
	}

	/**
	 * @param valueString the valueString to set
	 */
	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	/**
	 * @return the valueList
	 */
	public List<String> getValueList() {
		return valueList;
	}

	/**
	 * @param valueList the valueList to set
	 */
	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserPreference [valueString=" + valueString + ", valueList=" + valueList + "]";
	}
}
