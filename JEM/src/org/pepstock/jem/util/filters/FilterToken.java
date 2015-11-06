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
package org.pepstock.jem.util.filters;

import java.io.Serializable;


/**
 * Represent a token of a Filter in the form <code>name:value</code> 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 */
public class FilterToken implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Separator between name and value
	 */
	public static final String DEFAULT_NAME = "$default$";

	/**
	 * Separator between name and value
	 */
	public static final String SEPARATOR = ":";
	
	/**
	 * char to set NOT boolean logic
	 */
	public static final String NOT_CHAR = "!";

	private String name = DEFAULT_NAME;
	
	private String value = null;
	
	private boolean isNot = false;

	/**
	 * Builds an empty {@link FilterToken}
	 */
	public FilterToken() {
	}
	
	/**
	 * Builds a not null {@link FilterToken}
	 * @param filterName name part of token
	 * @param filterValue value part of token
	 */
	public FilterToken(String filterName, String filterValue) {
		// if filter name is not null
		if (filterName != null) {
			// normalize removing blanks and lowering case
			this.name = filterName.trim().toLowerCase();
		} else {
			this.name= DEFAULT_NAME;
		}
		// if value not null
		if (filterValue != null){
			// gets value
			String myValue = filterValue.trim();
			if (myValue.startsWith(NOT_CHAR)){
				if (myValue.length() > 1){
					// sets not
					isNot = true;
					this.value = myValue.substring(1);
				} else {
					// set to null because
					// a negative has been set but 
					// without a right value
					this.value = null;
				}
			} else {
				// assigns the value without blanks
				this.value = myValue;
			}
		}
	}

	/**
	 * @return the token name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the token name 
	 * @param name name of the filter
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the token value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the token value
	 * @param value value of the filter
	 */
	public void setValue(String value) {
		this.value = value;
	}

	
	/**
	 * @return the isNot
	 */
	public boolean isNot() {
		return isNot;
	}

	/**
	 * @param isNot the isNot to set
	 */
	public void setNot(boolean isNot) {
		this.isNot = isNot;
	}

	/**
	 * @return <code>true</code> if this {@link FilterToken} has a non-null and non-empty value
	 */
	public boolean hasValue() {
		return value != null && value.length() > 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (DEFAULT_NAME.equalsIgnoreCase(name) ? "" : name + SEPARATOR) + (isNot ? NOT_CHAR : "") + value;
	}
}