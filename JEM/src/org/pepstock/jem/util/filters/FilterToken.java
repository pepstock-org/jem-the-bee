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
	public static final String FILTER_TOKEN_SEPARATOR = ":";

	private String name = null;
	private String value = null;

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
		} 
		// if value not null
		if (filterValue != null){
			// assigns the value without blanks
			this.value = filterValue.trim();
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
	 * @return <code>true</code> if this {@link FilterToken} has a non-null and non-empty name
	 */
	public boolean hasName() {
		return name != null && name.length() > 0;
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
	 * @return <code>true</code> if this {@link FilterToken} has a non-null and non-empty value
	 */
	public boolean hasValue() {
		return value != null && value.length() > 0;
	}
	
	/**
	 * @return <code>true</code> if this token has only the value (and <code>null</code> name)
	 */
	public boolean hasValueOnly() {
		return !hasName() && hasValue();
	}
	
	/**
	 * @param filterName
	 * @return <code>true</code> if the name of this {@link FilterToken} equals the parameter
	 */
	public boolean is(String filterName) {
		return this.name.equalsIgnoreCase(filterName.trim());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + FILTER_TOKEN_SEPARATOR + value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// calculate a own hashcode 
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// if argument == to this
		if (this == obj){
			return true;
		}
		// if argument is null FALSE
		if (obj == null){
			return false;
		}
		// if argument is instance of filtertoken
		if (obj instanceof FilterToken){
			FilterToken other = (FilterToken) obj;
			// compares name
			if (name == null) {
				// if both null, equals
				if (other.name != null){
					return false;
				}
			} else if (!name.equals(other.name)){
				// if the names are different
				return false;
			}
			// compares values
			if (value == null) {
				// if are both null ok
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				// if the values are different
				return false;
			}
			// if here, they are equals
			return true;
		}
		// if here, the argument is not a filtertoken
		return false;
	}

	/**
	 * Build a {@link FilterToken} from a human-provided {@link String}
	 * @param tokenString the {@link String} representation of token
	 * @return a {@link FilterToken}
	 * @throws ParseException when the parameter is unparsable
	 */
	public static FilterToken parse(String tokenString) throws ParseException {
		try {
			// checks if empty string
			if (tokenString == null || tokenString.trim().isEmpty()) {
				throw new ParseException("Unparsable null/empty tokenString");
			}
			// splits the token 
			String[] nameValue = tokenString.split(FILTER_TOKEN_SEPARATOR);
			// in case of number of tokens
			switch (nameValue.length) {
			case 2:
				return new FilterToken(nameValue[0], nameValue[1]);
			case 1:
				return new FilterToken(null, nameValue[0]);
			default:
				throw new ArrayIndexOutOfBoundsException();
			}
		} catch (Exception e) {
			throw new ParseException("Unparsable tokenString '" + tokenString + "; cause: " + e.getMessage(), e);
		}
	}
}