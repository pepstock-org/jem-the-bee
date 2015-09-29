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
 * Contains all labels for statistics service to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class StatisticsManagerPaths {
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.PATH_SEPARATOR +  "stats";

	/**
	 * Key to define the path to bind get samples method
	 */
	public static final String SAMPLES = CommonPaths.PATH_SEPARATOR +  "samples";

	/**
	 * Key to define the path to bind get current sample method
	 */
	public static final String CURRENT_SAMPLE = CommonPaths.PATH_SEPARATOR +  "currentSample";

	/**
	 * Key to define the path to bind get result of display requestors (for GRS) method
	 */
	public static final String DISPLAY_REQUESTORS = CommonPaths.PATH_SEPARATOR +  "displayRequestors";

	/**
	 * Key to define the path to bind get all redo statements method
	 */
	public static final String REDO_STATEMENTS = CommonPaths.PATH_SEPARATOR +  "redoStatements";
	
	/**
	 * Key to define the path to bind get about method
	 */
	public static final String ABOUT = CommonPaths.PATH_SEPARATOR +  "about";
	
	/**
	 * Key to define the path to bind get JEM information method
	 */
	public static final String INFOS = CommonPaths.PATH_SEPARATOR +  "infos";
	
	/**
	 * To avoid any instantiation
	 */
	private StatisticsManagerPaths() {
		
	}

}
