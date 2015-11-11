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

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.Numbers;
import org.pepstock.jem.util.filters.fields.NodeFilterFields;

/**
 * This class is an object representation of a String-based filter
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public class FilterFactory {
	
	/**
	 * A default node filter that match all nodes
	 */
	public static final Filter NODE_DEFAULT_FILTER;
	// static default  node filter
	static {
		NODE_DEFAULT_FILTER = new Filter();
		NODE_DEFAULT_FILTER.add(new FilterToken(NodeFilterFields.NAME.getName(), Filter.EMPTY));
	}
	
	/**
	 * To avoid any instantiation
	 */
	private FilterFactory() {
	}
	
	/**
	 * @param filter filter instance to represents in a string
	 * @return the human-readable representation of this {@link Filter}
	 */
	public static String toSearchString(Filter filter) {
		// creates string builder
		StringBuilder sb = new StringBuilder();
		// scans all keys
		for (FilterToken token : filter.values()) {
			// creates string
			sb.append(token.toString()).append(Filter.SPACE);
		}
		return sb.toString().trim();
	}

	/**
	 * Constructs a {@link FilterFactory} object
	 * @param searchString the filter {@link String} provided by user
	 * @return a {@link FilterFactory}
	 * @throws FilterParseException when the filter string is unparsable
	 */
	public static Filter parse(String searchString) throws FilterParseException {
		// checks if search string is null
		if (searchString == null || searchString.trim().isEmpty()) {
			throw new FilterParseException("Unparsable null/empty searchString");
		}
		// creates a filter
		Filter toReturn = new Filter();
		// splits the search string
		String[] tokens = searchString.trim().split(Filter.SPACE);
		// scans all tokens
		for (String tokenString : tokens) {
			// creates a filter token, with internal parser
			// of filter token class
			FilterToken token = parseToken(tokenString);
			if (token != null && !toReturn.has(token.getName())){
				// adds token
				toReturn.add(token);
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
	public static Filter parse(String searchString, Filter defaultFilter) {
		try {
			// tries to parse the search string
			return FilterFactory.parse(searchString);
		} catch (FilterParseException e) {
			// if here, is not able to parse
			// returns the default
			LogAppl.getInstance().debug(e.getMessage(), e);
			return defaultFilter;
		}
	}
	
	/**
	 * Build a {@link FilterToken} from a human-provided {@link String}
	 * @param tokenString the {@link String} representation of token
	 * @return a {@link FilterToken}
	 * @throws FilterParseException when the parameter is unparsable
	 */
	private static FilterToken parseToken(String tokenString) {
			// checks if empty string
			if (tokenString == null || tokenString.trim().isEmpty()) {
				return null;
			}
			// splits the token 
			String[] nameValue = tokenString.split(FilterToken.SEPARATOR);
			// in case of number of tokens
			switch (nameValue.length) {
			case Numbers.N_2:
				return new FilterToken(nameValue[0], nameValue[1]);
			case Numbers.N_1:
				return new FilterToken(null, nameValue[0]);
			default:
				return null;
			}
	}
}