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
 * Contains all labels for jobs service to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 *
 */
public final class JobsManagerPaths  {
	
	/**
	 * Path parameter name to define the queue of job/s
	 */
	public static final String QUEUE = "queue";

	/**
	 * Path parameter REST format to define the queue of job/s
	 */
	public static final String QUEUE_PATH_PARAM = "{"+QUEUE+"}";

	/**
	 * Path parameter name to define the jobid to search
	 */
	public static final String JOBID = "jobid";
	
	/**
	 * Path parameter REST format to define the jobid to search
	 */
	public static final String JOBID_PATH_PARAM = "{"+JOBID+"}";

	/**
	 * Path parameter name to define if the cancel is in FORCE mode
	 */
	public static final String FORCE = "force";
	
	/**
	 * Path parameter REST format to define if the cancel is in FORCE mode
	 */
	public static final String FORCE_PATH_PARAM = "{"+FORCE+"}";
	
	/**
	 * Query parameter to get to perform query on history or not
	 */
	public static final String ON_HISTORY_QUERY_STRING  = "history";
	
	/**
	 * Query parameter to get the type of JCL to submit
	 */
	public static final String JCL_TYPE_QUERY_STRING  = "type";

	/**
	 * Query parameter to get the JEM URL of JCL to submit
	 */
	public static final String JCL_URL_QUERY_STRING  = "url";

	/**
	 * Query parameter to get the properties of JCL to submit
	 */
	public static final String JCL_PROPERTIES_QUERY_STRING  = "properties";
	
	/**
	 * JCL properties separator
	 */
	public static final String JCL_PROPERTY_SEPARATOR  = ",";
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.PATH_SEPARATOR + "jobs";

	/**
	 * Key to define the path to bind list of jobs method
	 */
	public static final String LIST = CommonPaths.PATH_SEPARATOR + "list" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM;

	/**
	 * Key to define the path to bind hold action method
	 */
	public static final String HOLD = CommonPaths.PATH_SEPARATOR + "hold" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * Key to define the path to bind release action method
	 */
	public static final String RELEASE = CommonPaths.PATH_SEPARATOR + "release"+ CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;
	
	/**
	 * Key to define the path to bind cancel action method
	 */
	public static final String CANCEL = CommonPaths.PATH_SEPARATOR + "cancel"+ CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM + CommonPaths.PATH_SEPARATOR + FORCE_PATH_PARAM;
	
	/**
	 * Key to define the path to bind purge job action method
	 */
	public static final String PURGE = CommonPaths.PATH_SEPARATOR + "purge" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;
	
	/**
	 * Key to define the path to bind update job action method
	 */
	public static final String UPDATE = CommonPaths.PATH_SEPARATOR + "update" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * Key to define the path to bind submit action method
	 */
	public static final String SUBMIT = CommonPaths.PATH_SEPARATOR + "submit";

	/**
	 * Key to define the path to bind get job output tree method
	 */
	public static final String OUTPUT_TREE = CommonPaths.PATH_SEPARATOR + "outputTree" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String OUTPUT_FILE = CommonPaths.PATH_SEPARATOR + "outputFile" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * Key to define the path to bind get job jcl content method
	 */
	public static final String JCL = CommonPaths.PATH_SEPARATOR + "jcl"+ CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;
	
	/**
	 * Key to define the path to bind get job status content method
	 */
	public static final String STATUS = CommonPaths.PATH_SEPARATOR + "status";
	
	/**
	 * Key to define the path to bind get job by id
	 */
	public static final String GET_BY_ID = CommonPaths.PATH_SEPARATOR + "getById" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * Key to define the path to bind get job by id
	 */
	public static final String GET_ONLY_BY_ID = CommonPaths.PATH_SEPARATOR + "getOnlyById" + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * Key to define the path to bind get all jcl type inside the JEM
	 */
	public static final String GET_JCL_TYPES = CommonPaths.PATH_SEPARATOR + "getJclTypes";
	
	/**
	 * Key to define the path to bind get system activity of running job
	 */
	public static final String SYSTEM_ACTIVITY = CommonPaths.PATH_SEPARATOR + "systemActivty" + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * To avoid any instantiation
	 */
    private JobsManagerPaths() {
    }
	
}
