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
package org.pepstock.jem.util;

import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * is a substituter of variables in UNIX format ${var}
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3	
 *
 */
public class VariableSubstituter {

	/**
	 * To avoid any instatiation
	 */
	private VariableSubstituter() {
		
	}

	/**
	 * Substitutes in text all possible variables which are keys of properties
	 * 
	 * @param text text string to update
	 * @param variables list of variables
	 * @return text modified and feeded by values of variables
	 */
	public static String substitute(String text, Properties variables) {
		// sets return value to initial text
		String returnValue = text;
		// gets variables
	    Set<Object> vars = variables.keySet();
	    for (Object variable : vars){
	    	String key = variable.toString();
	    	String value = variables.getProperty(key);
	    	// unix pattern 
	    	String pattern = "${" + key + "}";
	    	// to avoid never end loop, checks if value of variable is equal
	    	// to name of variable
	    	if (!key.equalsIgnoreCase(pattern)){
	    		// replaces variable
	    		returnValue = StringUtils.replace(returnValue, pattern, value);
	    	}
	    }
	    return returnValue;
	}
}