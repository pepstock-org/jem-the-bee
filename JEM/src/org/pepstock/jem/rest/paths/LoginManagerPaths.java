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
package org.pepstock.jem.rest.paths;

/**
 * Contains all labels for login service to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class LoginManagerPaths {
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.PATH_SEPARATOR +   "user";
	
	/**
	 * Key to define the path to bind user management method
	 */
	public static final String GET = CommonPaths.PATH_SEPARATOR +   "get";
	
	/**
	 * Key to define the path to bind login management method
	 */
	public static final String LOGIN = CommonPaths.PATH_SEPARATOR +   "login";
	
	/**
	 * Key to define the path to bind logoff management method
	 */
	public static final String LOGOFF = CommonPaths.PATH_SEPARATOR +   "logoff";
	
	/**
	 * Key to define the path to bind logoff management method saving preferences
	 */
	public static final String LOGOFF_SAVING_PREFERENCES = CommonPaths.PATH_SEPARATOR +   "logoffSavingPreferences";
	
	/**
	 * Key to define the path to bind store preferences method
	 */
	public static final String SAVE_PREFERENCES = CommonPaths.PATH_SEPARATOR +   "savePreferences";

	/**
	 * 
	 */
	private LoginManagerPaths() {
		
	}

}
