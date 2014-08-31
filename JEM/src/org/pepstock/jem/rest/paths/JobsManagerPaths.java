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
 * Contains all labels for jobs service to use to create REST URL.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public final class JobsManagerPaths  {
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String MAIN = CommonPaths.QUERYSTRING_SEPARATOR + "jobs";

	/**
	 * Key to define the path to bind input management method
	 */
	public static final String INPUT = CommonPaths.QUERYSTRING_SEPARATOR + "input";

	/**
	 * Key to define the path to bind running management method
	 */
	public static final String RUNNING = CommonPaths.QUERYSTRING_SEPARATOR + "running";

	/**
	 * Key to define the path to bind output management method
	 */
	public static final String OUTPUT = CommonPaths.QUERYSTRING_SEPARATOR + "output";

	/**
	 * Key to define the path to bind routing management method
	 */
	public static final String ROUTING = CommonPaths.QUERYSTRING_SEPARATOR + "routing";

	/**
	 * Key to define the path to bind hold action method
	 */
	public static final String HOLD = CommonPaths.QUERYSTRING_SEPARATOR + "hold";

	/**
	 * Key to define the path to bind release action method
	 */
	public static final String RELEASE = CommonPaths.QUERYSTRING_SEPARATOR + "release";
	
	/**
	 * Key to define the path to bind cancel action method
	 */
	public static final String CANCEL = CommonPaths.QUERYSTRING_SEPARATOR + "cancel";
	
	/**
	 * Key to define the path to bind purge jobs action method
	 */
	public static final String PURGE = CommonPaths.QUERYSTRING_SEPARATOR + "purge";
	
	/**
	 * Key to define the path to bind purge jobs action method
	 */
	public static final String UPDATE = CommonPaths.QUERYSTRING_SEPARATOR + "update";

	/**
	 * Key to define the path to bind submit action method
	 */
	public static final String SUBMIT = CommonPaths.QUERYSTRING_SEPARATOR + "submit";

	/**
	 * Key to define the path to bind get job output tree method
	 */
	public static final String OUTPUT_TREE = CommonPaths.QUERYSTRING_SEPARATOR + "outputTree";

	/**
	 * Key to define the path to bind get job output file content method
	 */
	public static final String OUTPUT_FILE_CONTENT = CommonPaths.QUERYSTRING_SEPARATOR + "outputFileContent";

	/**
	 * Key to define the path to bind get job jcl content method
	 */
	public static final String JCL_CONTENT = CommonPaths.QUERYSTRING_SEPARATOR + "jclContent";
	
	/**
	 * Key to define the path to bind get job status content method
	 */
	public static final String JOB_STATUS = CommonPaths.QUERYSTRING_SEPARATOR + "jobStatus";
	
	/**
	 * Key to define the path to bind get job by id
	 */
	public static final String JOB_BY_ID = CommonPaths.QUERYSTRING_SEPARATOR + "jobById";
	
	/**
	 * Key to define the path to bind get ended job by id
	 */
	public static final String ENDED_JOB_BY_ID = CommonPaths.QUERYSTRING_SEPARATOR + "endedJobById";
	
	/**
	 * Key to define the path to bind get system activity of running job
	 */
	public static final String JOB_SYSTEM_ACTIVITY = CommonPaths.QUERYSTRING_SEPARATOR + "jobSystemActivty";

	/**
	 * To avoid any instantiation
	 */
    private JobsManagerPaths() {
    }
	
}
