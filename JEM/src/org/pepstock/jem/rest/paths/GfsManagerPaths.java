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
 * Contains all labels for gfs service to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class GfsManagerPaths {
	
	public static final String TYPE = "type";
	
	public static final String TYPE_PATH_PARAM = "{"+TYPE+"}";

	public static final String RANDOM_CODE = "random-code";
	
	public static final String RANDOM_CODE_PATH_PARAM = "{"+RANDOM_CODE+"}";
	
	/**
	 * Query parameter path name
	 */
	public static final String PATH_NAME_QUERY_STRING = "pathName";

	/**
	 * Query parameter item
	 */
	public static final String ITEM_QUERY_STRING = "item";

	/**
	 * Query parameter last upate
	 */
	public static final String LAST_UPDATE_QUERY_STRING = "lastUpdate";

	/**
	 * Query parameter last upate
	 */
	public static final String COMPLETED_QUERY_STRING = "completed";
	
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.PATH_SEPARATOR +  "gfs";

	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String LIST = CommonPaths.PATH_SEPARATOR +  "list"  + CommonPaths.PATH_SEPARATOR + TYPE_PATH_PARAM;
	
	/**
	 * "bin" parameter on url
	 */
	public static final String UPLOAD = CommonPaths.PATH_SEPARATOR +  "upload" + CommonPaths.PATH_SEPARATOR + TYPE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + RANDOM_CODE_PATH_PARAM;
	
	/**
	 * "bin" parameter on url
	 */
	public static final String DELETE = CommonPaths.PATH_SEPARATOR +  "delete" + CommonPaths.PATH_SEPARATOR + TYPE_PATH_PARAM;
	
	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String GET_CONTENT = CommonPaths.PATH_SEPARATOR +  "get" + CommonPaths.PATH_SEPARATOR + TYPE_PATH_PARAM;

	/**
	 * To avoid any instantiation
	 */
	private GfsManagerPaths() {
		
	}

}
