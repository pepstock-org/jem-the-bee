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
package org.pepstock.jem.rest.services;

/**
 * utility to build the rright URL path to perform the REST call, replacing the variables.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class PathReplacer {

	private String path = null;

	/**
	 * Creates the object using the path of REST service
	 * @param path URL path of service
	 */
	private PathReplacer(String path) {
		super();
		this.path = path;
	}

	/**
	 * Returns the object using the path of REST service
	 * @param path URL path of service
	 * @return  the replacer for additional calls
	 */
	static PathReplacer path(String path){
		return new PathReplacer(path);
	}
	
	/**
	 * Replaces inside the URL path service the variable
	 * @param parm variable to change
	 * @param value value to substitute
	 * @return the replacer for additional calls
	 */
	PathReplacer replace(String parm, String value){
		path = path.replace(parm, value);
		return this;
	}

	/**
	 * Returns the path with all performed substitutions.
	 * @return the URL path
	 */
	String build(){
		return path;
	}
}
