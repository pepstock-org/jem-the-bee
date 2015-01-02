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
package org.pepstock.jem;

import java.io.Serializable;

/**
 * JCL (Job Control Language) is the class which contains all statements to
 * describe and execute jobs. It is possible to have different languages to
 * control jobs and for this reason is abstract about that.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */

public abstract class AbstractJcl extends AbstractExecutionEnvironment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Default affinity, value "default".
	 */
	public static final String DEFAULT_AFFINITY = "***";

	/**
	 * Default memory for the job, in MB
	 */
	public static final int DEFAULT_MEMORY = 128;

	/**
	 * Default priority for the job
	 */
	public static final int DEFAULT_PRIORITY = 10;
	
	private String jobName = null;
	
	private String affinity = DEFAULT_AFFINITY;
	
	private int memory =  DEFAULT_MEMORY;
	
	private int priority = DEFAULT_PRIORITY;
	
	private boolean hold = false;
	
	/**
	 * Field containing the email addresses where to send the end
	 * Job notification. The addresses are semicolon separated.
	 */
	private String emailNotificationAddresses = null;

	private String user = null;

	/**
	 * Constructor without any arguments
	 */
	public AbstractJcl() {
	}

	/**
	 * Returns the name string for job, related to this JCL, or null if none.
	 * 
	 * @return the name string for job
	 */
	public final String getJobName() {
		return jobName;
	}

	/**
	 * Sets the name string for job, related to this JCL after validation.
	 * 
	 * @param jobName the name string for job
	 */
	public final void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Sets the name of affinity which job must be executed in. If none of
	 * members is defined with this domain, job will stay in "input" queue,
	 * waiting for an initiator with this domain.
	 * 
	 * @return the name of affinity
	 */
	public final String getAffinity() {
		return affinity;
	}

	/**
	 * Sets the name of affinity which job must be executed in. If none of
	 * members is defined with this domain, job will stay in "input" queue,
	 * waiting for an initiator with this domain.
	 * 
	 * @param affinity the name of affinity
	 */
	public final void setAffinity(String affinity) {
		this.affinity = affinity;
	}
	
	/**
	 * Gets the email addresses where to send
	 * end Job notification email.
	 * 
	 * @return the email addresses for notification
	 */
	public final String getEmailNotificationAddresses() {
		return emailNotificationAddresses;
	}

	/**
	 * Sets the email addresses where to send the
	 * end Job notification email.
	 * 
	 * @param emailNotificationAddresses the email addresses for notification, semicolon separated.
	 */
	public final void setEmailNotificationAddresses(String emailNotificationAddresses) {
		this.emailNotificationAddresses = emailNotificationAddresses;
	}
	
	/**
	 * Return true if Jcl has the email addresses for notification: <br>
	 * checks if <code>emailNotificationAddresses</code> is different from null
	 * or an empty string.
	 * 
	 * @return true if Jcl has the email addresses for notification, false
	 *         otherwise.
	 */
	public final boolean hasEmailNotificationAddresses() {
		if (null == this.emailNotificationAddresses || "".equalsIgnoreCase(this.emailNotificationAddresses.trim())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the memory estimated necessary to execute the job
	 * 
	 * @return the memory in MB
	 */
	public final int getMemory() {
		return memory;
	}

	/**
	 * Sets the memory estimated necessary to execute the job
	 * 
	 * @param memory the memory in MB to set
	 */
	public final void setMemory(int memory) {
		this.memory = memory;
	}

	/**
	 * Returns the priority for job, in "input" and "output" queues. Default is
	 * 10.
	 * 
	 * @return the priority of job when queued
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * Sets the priority for job, in "input" and "output" queues.
	 * 
	 * @param priority the priority of job when queued
	 */
	public final void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Returns if job is blocked in "input" and "output" queues. Default is
	 * false.
	 * 
	 * @return true if job is hold, otherwise false
	 */
	public final boolean isHold() {
		return hold;
	}

	/**
	 * Sets if job is blocked in "input" and "output" queues. Default is false.
	 * 
	 * @param hold true if job is hold, otherwise false
	 */
	public final void setHold(boolean hold) {
		this.hold = hold;
	}

	/**
	 * Returns the user set inside of JCL.
	 * 
	 * @return the user
	 */
	public final String getUser() {
		return user;
	}

	/**
	 * Sets the user to use during the submission of the job
	 * 
	 * @param user the user to set
	 */
	public final void setUser(String user) {
		this.user = user;
	}
	
	
}