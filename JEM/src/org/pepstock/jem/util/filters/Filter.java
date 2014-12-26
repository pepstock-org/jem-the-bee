/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Cuc" Cuccato
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.filters.fields.NodeFilterFields;

/**
 * This class is an object representation of a String-based filter
 * 
 * @author Marco "Cuc" Cuccato
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

	/**
	 * A default node filter that match all nodes
	 */
	public static final Filter NODE_DEFAULT_FILTER;
	// static default  node filter
	static {
		NODE_DEFAULT_FILTER = new Filter();
		NODE_DEFAULT_FILTER.add(new FilterToken(NodeFilterFields.NAME.getName(), EMPTY));
	}
	
	protected Map<String, String> elements = new HashMap<String, String>();

	/**
	 * Adds a {@link FilterToken} to this {@link Filter}
	 * @param token token to add
	 */
	public void add(FilterToken token) {
		// do the merge of values if default token
		if (token.hasValueOnly() && has(null)) {
			token.setValue(get(null).trim() + SPACE + token.getValue());
		}
		// if has got value, adds to elements
		if (token.hasValue()) {
			elements.put(token.getName(), token.getValue());
		}
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
	public String get(String filterName) {
		return elements.get(filterName);
	}
	
	/**
	 * Removes a {@link FilterToken} from this {@link Filter}
	 * @param token token to remove from map
	 */
	public void remove(FilterToken token) {
		remove(token.getName());
	}
	
	/**
	 * Removes a {@link FilterToken} with provided name
	 * @param filterName filter name element
	 */
	public void remove(String filterName) {
		elements.remove(filterName);
	}
	
	/**
	 * @return the amount of {@link FilterToken}
	 */
	public int count() {
		return elements.size();
	}
	
	/**
	 * Drop all {@link FilterToken}s of this {@link Filter}. Useful for reuse.
	 */
	public void clear() {
		elements.clear();
	}

	/**
	 * @return all names of {@link FilterToken}s
	 */
	public Set<String> names() {
		return new HashSet<String>(elements.keySet());
		
	}
	
	/**
	 * @return all values of {@link FilterToken}s
	 */
	public Collection<String> values() {
		return new ArrayList<String>(elements.values());
	}
	
	/**
	 * @return a {@link FilterToken} array of this {@link Filter}
	 */
	public FilterToken[] toTokenArray() {
		// creates the array
		FilterToken[] array = new FilterToken[names().size()];
		int i=0;
		// scans all names of elements
		for (String name : names()) {
			// creates a filter token
			array[i] = new FilterToken(name, get(name));
			// increments counter
			i++;
		}
		return array;
	}
	
	/**
	 * @return the human-readable representation of this {@link Filter}
	 */
	public String toSearchString() {
		// creates string builder
		StringBuilder sb = new StringBuilder();
		// scans all keys
		for (String currentName : elements.keySet()) {
			// gets value
			String currentValue = elements.get(currentName);
			// if name not null
			if (currentName != null) {
				// creates string
				sb.append(currentName).append(FilterToken.FILTER_TOKEN_SEPARATOR);	
			}
			// add always the value
			sb.append(currentValue);
			sb.append(SPACE);
		}
		return sb.toString().trim();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Filter [elements=" + elements + "]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// calculated hashcode
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// if == TRUE
		if (this == obj){
			return true;
		}
		// if argument is null, FALSE
		if (obj == null){
			return false;
		}
		// if argument is a filter
		if (obj instanceof Filter){
			// casts to filter
			Filter other = (Filter) obj;
			// if elements are null 
			if (elements == null) {
				// and elements of argument are null, EQUALS
				if (other.elements != null){
					return false;
				}
			} else if (!elements.equals(other.elements)){
				// compares the elements
				return false;
			}
			// if here, are EQUALS
			return true;
		}
		// is not a FILTER, FALSE
		return false;
	}

	/**
	 * Constructs a {@link Filter} object
	 * @param searchString the filter {@link String} provided by user
	 * @return a {@link Filter}
	 * @throws ParseException when the filter string is unparsable
	 */
	public static Filter parse(String searchString) throws ParseException {
		// checks if search string is null
		if (searchString == null || searchString.trim().isEmpty()) {
			throw new ParseException("Unparsable null/empty searchString");
		}
		// creates a filter
		Filter toReturn = new Filter();
		// splits the search string
		String[] tokens = searchString.trim().split(SPACE);
		// scans all tokens
		for (String tokenString : tokens) {
			try {
				// creates a filter token, with internal parser
				// of filter token class
				FilterToken token = FilterToken.parse(tokenString);
				// adds token
				toReturn.add(token);
			} catch (ParseException tpe) {
				// NOPE
			}
		}
		return toReturn;
	}
	
	/**
	 * Try to build a filter based on search string. If fail, return the default choice.   
	 * @param searchString the filter string
	 * @param defaultFilter the filter to return if search string parse fail
	 * @return a search string based filter, or a default filter
	 */
	public static Filter parseOrDefault(String searchString, Filter defaultFilter) {
		Filter filter;
		try {
			// tries to parse the search string
			filter = Filter.parse(searchString);
		} catch (Exception e) {
			// if here, is not able to parse
			// returns the default
			LogAppl.getInstance().debug(e.getMessage(), e);
			filter = defaultFilter;
		}
		return filter;
	}
}