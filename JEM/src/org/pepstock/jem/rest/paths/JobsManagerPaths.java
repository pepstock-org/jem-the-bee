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
 *
 */
public final class JobsManagerPaths  {
	
	public static final String QUEUE = "queue";
	
	public static final String QUEUE_PATH_PARAM = "{"+QUEUE+"}";

	public static final String JOBID = "jobid";
	
	public static final String JOBID_PATH_PARAM = "{"+JOBID+"}";

	public static final String FORCE = "force";
	
	public static final String FORCE_PATH_PARAM = "{"+FORCE+"}";

	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.PATH_SEPARATOR + "jobs";

	/**
	 * Key to define the path to bind input management method
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
	 * Key to define the path to bind purge jobs action method
	 */
	public static final String PURGE = CommonPaths.PATH_SEPARATOR + "purge" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;
	
	/**
	 * Key to define the path to bind purge jobs action method
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
	public static final String JOB_STATUS = CommonPaths.PATH_SEPARATOR + "jobStatus";
	
	/**
	 * Key to define the path to bind get job by id
	 */
	public static final String JOB_BY_ID = CommonPaths.PATH_SEPARATOR + "jobById" + CommonPaths.PATH_SEPARATOR + QUEUE_PATH_PARAM + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;
	
	/**
	 * Key to define the path to bind get system activity of running job
	 */
	public static final String JOB_SYSTEM_ACTIVITY = CommonPaths.PATH_SEPARATOR + "jobSystemActivty" + CommonPaths.PATH_SEPARATOR + JOBID_PATH_PARAM;

	/**
	 * To avoid any instantiation
	 */
    private JobsManagerPaths() {
    }
	
}
