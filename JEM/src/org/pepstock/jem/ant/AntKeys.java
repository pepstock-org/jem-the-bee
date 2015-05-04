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
package org.pepstock.jem.ant;


/**
 * Contains all constants used inside of ANT source code to be compliance JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class AntKeys {

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set job Name. If is missing, JEM uses
	 * <code>name</code> attribute value for project element as job name.
	 */
	public static final String ANT_JOB_NAME = "jem.job.name";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set environment.
	 */
	public static final String ANT_ENVIRONMENT = "jem.job.environment";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set domain. Is optional.
	 */
	public static final String ANT_DOMAIN = "jem.job.domain";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set affinity. Is optional.
	 */
	public static final String ANT_AFFINITY = "jem.job.affinity";

	/**
	 * It is the <code>name</code> attribute value for property element inside of
	 * project element, to set email addresses to send the notification of the end
	 * of the Job. It is optional.
	 */
	public static final String ANT_EMAILS_NOTIFICATION = "jem.job.emailsNotification";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set the user to use to executed the job. Is optional.
	 */
	public static final String ANT_USER = "jem.job.user";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set hold state of job. Is optional.
	 */
	public static final String ANT_HOLD = "jem.job.hold";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set queue priority of job. Is optional.
	 */
	public static final String ANT_PRIORITY = "jem.job.priority";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set memory in MB for job. Is optional.
	 */
	public static final String ANT_MEMORY = "jem.job.memory";

	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set locking scope. Must be "job", "step" or "task". Is optional. Default is "job"
	 */
	public static final String ANT_LOCKING_SCOPE = "jem.job.lockingScope";
		
	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to set java version name you need.
	 */
	public static final String ANT_JAVA = "jem.job.java";
	
	/**
	 * Is constant to define locking scope to job level
	 */
	public static final String ANT_JOB_SCOPE = "job";

	/**
	 * Is constant to define locking scope to step level
	 */
	public static final String ANT_STEP_SCOPE = "step";

	/**
	 * Is constant to define locking scope to task level
	 */
	public static final String ANT_TASK_SCOPE = "task";
	
	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to add libs to ANt classptah process. Is optional.
	 */
	public static final String ANT_CLASSPATH = "jem.job.classPath";
	
	/**
	 * Is the <code>name</code> attribute value for property element inside of
	 * project element, to add libs to ANT classptah process, at the beginning of classpath. Is optional.
	 */
	public static final String ANT_PRIOR_CLASSPATH = "jem.job.priorClassPath";

	/**
	 * Is the attribute <code>name</code> for property element inside of
	 * project element, to validate the ant project using xslt. Is optional.
	 */
	public static final String ANT_XSLTPROJECTVALIDATOR = "jem.xslt.project.validator";
	
	/**
	 * Is the name use during binding of DataPaths container
	 */
	public static final String ANT_DATAPATHS_BIND_NAME = "jem.dataPaths";
	
	
	/**
	 * Prefix of parameters for data description
	 */
	public static final String ANT_DATA_DESCRIPTION_PREFIX = "jem.dataDescription.";
	
	/**
	 * Prefix of parameters for data sources
	 */
	public static final String ANT_DATA_SOURCE_PREFIX = "jem.dataSource.";
	
	/**
	 * Name of parameter for locks
	 */
	public static final String ANT_LOCK_KEY = "jem.locks";

	/**
	 * To avoid any instantiation
	 */
	private AntKeys() {
	}
	
}