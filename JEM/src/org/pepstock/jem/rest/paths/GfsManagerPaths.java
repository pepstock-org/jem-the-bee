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
package org.pepstock.jem.rest.paths;

/**
 * Contains all labels for gfs service to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class GfsManagerPaths {
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.QUERYSTRING_SEPARATOR +  "gfs";

	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String FILE_LIST = CommonPaths.QUERYSTRING_SEPARATOR +  "ls";
	
	/**
	 * "bin" parameter on url
	 */
	public static final String FILE_UPLOAD = CommonPaths.QUERYSTRING_SEPARATOR +  "upload";
	
	/**
	 * "bin" parameter on url
	 */
	public static final String FILE_DELETE = CommonPaths.QUERYSTRING_SEPARATOR +  "delete";
	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String OUTPUT_FILE_CONTENT_PATH = CommonPaths.QUERYSTRING_SEPARATOR +  "cat";

	/**
	 * To avoid any instantiation
	 */
	private GfsManagerPaths() {
		
	}

}
