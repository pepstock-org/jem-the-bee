/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRJBPMY; without even the implied warranty of
    MERCHJBPMABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.jbpm;



/**
 * Contains all constants used inside of JBPM source code to be compliance JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public final class JBpmKeys {

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set job Name. Is optional.
	 */
	public static final String JBPM_JOB_NAME = "jem.job.name";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set environment. Is optional.
	 */
	public static final String JBPM_ENVIRONMENT = "jem.job.environment";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set domain. Is optional.
	 */
	public static final String JBPM_DOMAIN = "jem.job.domain";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set affinity. Is optional.
	 */
	public static final String JBPM_AFFINITY = "jem.job.affinity";

	/**
	 * It is the attribute value for property element inside of
	 * process element, to set email addresses to send the notification of the end
	 * of the Job. It is optional.
	 */
	public static final String JBPM_EMAILS_NOTIFICATION = "jem.job.emailsNotification";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set the user to use to executed the job. Is optional.
	 */
	public static final String JBPM_USER = "jem.job.user";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set hold state of job. Is optional.
	 */
	public static final String JBPM_HOLD = "jem.job.hold";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set queue priority of job. Is optional.
	 */
	public static final String JBPM_PRIORITY = "jem.job.priority";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set memory in MB for job. Is optional.
	 */
	public static final String JBPM_MEMORY = "jem.job.memory";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set locking scope. Must be "job", "step" or "task". Is optional. Default is "job"
	 */
	public static final String JBPM_LOCKING_SCOPE = "jem.job.lockingScope";
	
	/**
	 * Is constant to define locking scope to job level
	 */
	public static final String JBPM_JOB_SCOPE = "job";

	/**
	 * Is constant to define locking scope to step level
	 */
	public static final String JBPM_STEP_SCOPE = "step";

	/**
	 * Is constant to define locking scope to task level
	 */
	public static final String JBPM_TASK_SCOPE = "task";
	
	/**
	 * Is the attribute value for property element inside of
	 * process element, to add libraries to JBPM classpath process. Is optional.
	 */
	public static final String JBPM_CLASSPATH = "jem.job.classPath";
	
	/**
	 * Is the attribute value for property element inside of
	 * process element, to add libraries to JBPM classpath process, at the beginning of classpath. Is optional.
	 */
	public static final String JBPM_PRIOR_CLASSPATH = "jem.job.priorClassPath";

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set java version to use. Is optional.
	 */
	public static final String JBPM_JAVA = "jem.job.java";
	
	/**
	 * Is the name use during binding of DataPaths container
	 */
	public static final String JBPM_DATAPATHS_BIND_NAME = "jem.dataPaths";
	
	/**
	 * The workitem name used by JEM to create all resource
	 */
	public static final String JBPM_JEM_WORKITEM_NAME = "Jem";
	
	/**
	 * Prefix of parameters workitem for data description
	 */
	public static final String JBPM_DATA_DESCRIPTION_PREFIX = "jem.dataDescription.";
	
	/**
	 * Prefix of parameters workitem for data sources
	 */
	public static final String JBPM_DATA_SOURCE_PREFIX = "jem.dataSource.";
	
	/**
	 * Name of parameter workitem for locks
	 */
	public static final String JBPM_LOCK_KEY = "jem.locks";


	/**
	 * To avoid any instantiation
	 */
	private JBpmKeys() {
	}
	
}