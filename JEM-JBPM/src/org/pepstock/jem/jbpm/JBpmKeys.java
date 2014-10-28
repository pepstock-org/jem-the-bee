/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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

import org.pepstock.jem.ant.AntKeys;


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
	public static final String JBPM_JOB_NAME = AntKeys.ANT_JOB_NAME;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set environment. Is optional.
	 */
	public static final String JBPM_ENVIRONMENT = AntKeys.ANT_ENVIRONMENT;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set domain. Is optional.
	 */
	public static final String JBPM_DOMAIN = AntKeys.ANT_DOMAIN;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set affinity. Is optional.
	 */
	public static final String JBPM_AFFINITY = AntKeys.ANT_AFFINITY;

	/**
	 * It is the attribute value for property element inside of
	 * process element, to set email addresses to send the notification of the end
	 * of the Job. It is optional.
	 */
	public static final String JBPM_EMAILS_NOTIFICATION = AntKeys.ANT_EMAILS_NOTIFICATION;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set the user to use to executed the job. Is optional.
	 */
	public static final String JBPM_USER = AntKeys.ANT_USER;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set hold state of job. Is optional.
	 */
	public static final String JBPM_HOLD = AntKeys.ANT_HOLD;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set queue priority of job. Is optional.
	 */
	public static final String JBPM_PRIORITY = AntKeys.ANT_PRIORITY;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set memory in MB for job. Is optional.
	 */
	public static final String JBPM_MEMORY = AntKeys.ANT_MEMORY;

	/**
	 * Is the attribute value for property element inside of
	 * process element, to set locking scope. Must be "job", "step" or "task". Is optional. Default is "job"
	 */
	public static final String JBPM_LOCKING_SCOPE = AntKeys.ANT_LOCKING_SCOPE;
	
	/**
	 * Is constant to define locking scope to job level
	 */
	public static final String JBPM_JOB_SCOPE = AntKeys.ANT_JOB_SCOPE;

	/**
	 * Is constant to define locking scope to step level
	 */
	public static final String JBPM_STEP_SCOPE = AntKeys.ANT_STEP_SCOPE;

	/**
	 * Is constant to define locking scope to task level
	 */
	public static final String JBPM_TASK_SCOPE = AntKeys.ANT_ENVIRONMENT;
	
	/**
	 * Is the attribute value for property element inside of
	 * process element, to add libraries to JBPM classpath process. Is optional.
	 */
	public static final String JBPM_CLASSPATH = AntKeys.ANT_CLASSPATH;
	
	/**
	 * Is the attribute value for property element inside of
	 * process element, to add libraries to JBPM classpath process, at the beginning of classpath. Is optional.
	 */
	public static final String JBPM_PRIOR_CLASSPATH = AntKeys.ANT_PRIOR_CLASSPATH;

	/**
	 * Is the name use during binding of DataPaths container
	 */
	public static final String JBPM_DATAPATHS_BIND_NAME = AntKeys.ANT_DATAPATHS_BIND_NAME;
	
	/**
	 * The workitem name used by JEM to create all resource
	 */
	public static final String JBPM_JEM_WORKITEM_NAME = "Jem";
	
	/**
	 * Prefix of parameters workitem for data description
	 */
	public static final String JBPM_DATA_DESCRIPTION_PREFIX = AntKeys.ANT_DATA_DESCRIPTION_PREFIX;
	
	/**
	 * Prefix of parameters workitem for data sources
	 */
	public static final String JBPM_DATA_SOURCE_PREFIX = AntKeys.ANT_DATA_SOURCE_PREFIX;
	
	/**
	 * Name of parameter workitem for locks
	 */
	public static final String JBPM_LOCK_KEY = AntKeys.ANT_LOCK_KEY;


	/**
	 * To avoid any instantiation
	 */
	private JBpmKeys() {
	}
	
}