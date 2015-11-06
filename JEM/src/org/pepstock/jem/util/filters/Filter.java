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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is an object representation of a String-based filter
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public class Filter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Empty string
	 */
	public static final String EMPTY = "";
	/**
	 * ONE blank
	 */
	public static final String SPACE = " ";
	/**
	 * TWO blanks
	 */
	public static final String DOUBLE_SPACE = "  ";
	
	private Map<String, FilterToken> tokens = new HashMap<String, FilterToken>();

	/**
	 * Adds a {@link FilterToken} to this {@link Filter}
	 * @param token token to add
	 */
	public void add(FilterToken token) {
		tokens.put(token.getName(), token);
	}
	
	/**
	 * Check if this {@link Filter} has a {@link FilterToken} with provided name
	 * @param filterName the name of the {@link FilterToken}
	 * @return <code>true</code> if it has, <code>false</code> otherwise
	 */
	public boolean has(String filterName) {
		return get(filterName) != null;
	}
	
	/**
	 * Get the value of a {@link FilterToken}
	 * @param filterName the name of the {@link FilterToken}
	 * @return the value of the {@link FilterToken} that match the parameter
	 */
	public FilterToken get(String filterName) {
		return tokens.get(filterName);
	}
	
	/**
	 * Get the value of a {@link FilterToken}
	 * @param filterName the name of the {@link FilterToken}
	 * @return the value of the {@link FilterToken} that match the parameter
	 */
	public String getValue(String filterName) {
		FilterToken token = get(filterName);
		return (token != null) ? token.getValue() : null;
	}
	
	/**
	 * Returns true if there isn't any token, otherwise false.
	 * @return true if there isn't any token, otherwise false
	 */
	public boolean isEmpty(){
		return tokens.isEmpty();
	}

	/**
	 * @return all values of {@link FilterToken}s
	 */
	public Collection<FilterToken> values() {
		return tokens.values();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Filter [elements=" + tokens + "]";
	}
}