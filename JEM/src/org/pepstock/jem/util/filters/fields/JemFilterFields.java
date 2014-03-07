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
package org.pepstock.jem.util.filters.fields;

/**
 * Interface that collect all supported filter fields
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 * @param <T> the type of object which fields are related to
 */
public interface JemFilterFields<T> {

	/**
	 * 
	 */
	String DATETIME_PATTERN_DEFAULT = "yyyyMMddHHmmss";
	
	/**
	 * 
	 */
	String DATETIME_PATTERN_DDHHMM = "ddHHmm";
	
	/**
	 * 
	 */
	String DURATION_PATTERN_DESCRIPTION = "ex.: 2d or 3h or 15m";
	
	/**
	 * 
	 */
	String YES_NO_PATTERN_DESCRIPTION = "yes | no";
	
	/**
	 * @return the field name
	 */
	String getName();
	
	/**
	 * @return <code>true</code> if the value associated with this field should match a pattern
	 */
	boolean hasPattern();
	
	/**
	 * @return the pattern
	 */
	String getPattern();
	
}