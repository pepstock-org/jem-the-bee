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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.GwtTransient;

/**
 * Job is a simple container for steps, there are many configuration options of
 * which a developers must be aware. Furthermore, there are many considerations
 * for how a Job will be run and how its meta-data will be stored during that
 * run.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Job implements Serializable, Comparable<Object> {

	private static final long serialVersionUID = 1L;

	/**
	 * Status of job when asked for resources and is still waiting for
	 */
	public static final int WAITING_FOR_RESOURCES = -1;

	/**
	 * Status of job is not in running phase
	 */
	public static final int NONE = 0;

	/**
	 * Status of job when the control of job execution is to job task process
	 */
	public static final int RUNNING = 1;

	private String id = null;

	private String name = null;

	private String user = null;

	private String orgUnit = null;

	private Date submittedTime = new Date();

	private Date startedTime = new Date();

	private Date endedTime = null;

	private Jcl jcl = null;

	private String memberId = null;

	private String memberLabel = null;

	private String processId = null;

	private Result result = null;

	private int runningStatus = NONE;

	private RoutingInfo routingInfo = new RoutingInfo();

	private boolean nowait = false;

	/**
	 * Put @GWTTransinet to improve serialization performance.
	 */
	@GwtTransient
	private List<String> inputArguments = new ArrayList<String>();

	private Step currentStep = null;

	/**
	 * Constructor without any arguments
	 */
	public Job() {
	}

	/**
	 * Returns the id string for job, or null if none
	 * 
	 * @return id string for job
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id string for job.
	 * 
	 * @param id id for job
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the name string for job.
	 * 
	 * @param name the name string for job
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name string for job, or null if none
	 * 
	 * @return the name string for job
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the user who submitted the job. if null, use the properties, by
	 * "user.name" key.
	 * 
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Returns <code>true</code> if the user of job is different from jcl user.<br>
	 * The job can be executed if the user is authorized to use other users.
	 * 
	 * @return <code>true</code> if the user of job is different from jcl user.<br>
	 */
	public boolean isUserSurrogated() {
		if (jcl != null && user != null && jcl.getUser() != null) {
			return !user.equalsIgnoreCase(jcl.getUser());
		}
		return false;
	}

	/**
	 * Sets the user who submitted the job.
	 * 
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Returns the organizational unit related to user
	 * 
	 * @return the organizational unit
	 */
	public String getOrgUnit() {
		return orgUnit;
	}

	/**
	 * Sets the organizational unit related to user
	 * 
	 * @param group the organizational unit to set
	 */
	public void setOrgUnit(String group) {
		this.orgUnit = group;
	}

	/**
	 * Returns the jcl object for job, or null if none.
	 * 
	 * @return jcl object for job
	 */
	public Jcl getJcl() {
		return jcl;
	}

	/**
	 * Sets jcl object for job. This method sets, if defined in JCL: 1. job name
	 * 2. environment 3. domain 4. affinity 5. priority 6. hold
	 * 
	 * @param jcl jcl object
	 */
	public void setJcl(Jcl jcl) {
		this.jcl = jcl;
	}

	/**
	 * Sets the member of cluster id (named node) where job has been submitted.
	 * 
	 * @param memberId member name
	 */
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	/**
	 * Returns the member of cluster id (named node) where job has been
	 * submitted, or null if none
	 * 
	 * @return member name
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * Returns the member of cluster label (named node) where job has been
	 * submitted, or null if none
	 * 
	 * @return the memberLabel
	 */
	public String getMemberLabel() {
		return memberLabel;
	}

	/**
	 * Sets the member of cluster label (named node) where job has been
	 * submitted.
	 * 
	 * @param memberLabel the memberLabel to set
	 */
	public void setMemberLabel(String memberLabel) {
		this.memberLabel = memberLabel;
	}

	/**
	 * Returns the process ID of job currently in execution, otherwise
	 * <code>null</code>.
	 * 
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * /** Sets the process ID of job currently in execution.
	 * 
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * Sets the date object when the job is created. Never used.
	 * 
	 * @param submittedTime the date object of job creation
	 */
	public void setSubmittedTime(Date submittedTime) {
		this.submittedTime = submittedTime;
	}

	/**
	 * Returns the date object when the job is created.
	 * 
	 * @return the date object of job creation
	 */
	public Date getSubmittedTime() {
		return submittedTime;
	}

	/**
	 * Returns the date object when the job started execution.
	 * 
	 * @return the date object of job creation
	 */
	public Date getStartedTime() {
		return startedTime;
	}

	/**
	 * Sets the date object when the job started execution
	 * 
	 * @param startedTime the startedTime to set
	 */
	public void setStartedTime(Date startedTime) {
		this.startedTime = startedTime;
	}

	/**
	 * Sets the date object when the job is ended.
	 * 
	 * @param endedTime the date object of job ending
	 */
	public void setEndedTime(Date endedTime) {
		this.endedTime = endedTime;
	}

	/**
	 * Returns the date object when the job is ended.
	 * 
	 * @return the date object of job ending
	 */
	public Date getEndedTime() {
		return endedTime;
	}

	/**
	 * Returns line arguments passed during the submitting of job, by
	 * <code>-D</code> way into <code>System.getProperties</code>.<br>
	 * 
	 * @return the lineArguments
	 */
	public List<String> getInputArguments() {
		return inputArguments;
	}

	/**
	 * Sets properties, passed during the submitting of job, by <code>-D</code>
	 * way into <code>System.getProperties</code>
	 * 
	 * @param lineArguments the lineArguments to set
	 */
	public void setInputArguments(List<String> lineArguments) {
		this.inputArguments = lineArguments;
	}

	/**
	 * Set the running status
	 * 
	 * @return the runningStatus
	 */
	public int getRunningStatus() {
		return runningStatus;
	}

	/**
	 * Returns the running status. Could be NONE, WIATING_FOR_RESOURCES or
	 * RUNNING
	 * 
	 * @param runningStatus the runningStatus to set
	 */
	public void setRunningStatus(int runningStatus) {
		this.runningStatus = runningStatus;
	}

	/**
	 * Returns the result object of job execution, or null if none.
	 * 
	 * @see org.pepstock.jem.Result
	 * @return the result object of job execution
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * Sets the result object of job execution.
	 * 
	 * @see org.pepstock.jem.Result
	 * @param result the result object of job execution
	 */
	public void setResult(Result result) {
		this.result = result;
	}

	/**
	 * Sets the step object of job currently in execution. Used internally.
	 * 
	 * @see org.pepstock.jem.Step
	 * @param currentStep the step object of job currently in execution
	 */
	public void setCurrentStep(Step currentStep) {
		this.currentStep = currentStep;
	}

	/**
	 * Results the step object of job currently in execution. Used internally.
	 * 
	 * @see org.pepstock.jem.Step
	 * @return the step object of job currently in execution
	 */
	public Step getCurrentStep() {
		return currentStep;
	}

	/**
	 * Returns the string representation of job, with job id and name, if there
	 * is.
	 * 
	 * @return job id and name
	 */
	@Override
	public String toString() {
		if (name != null) {
			return "Job [name=" + name + ", id=" + id + "]";
		}
		return "Job [id=" + id + "]";
	}

	/**
	 * Compares the object with job. Returns true if the object parameter is
	 * instance of Job and the id is the same.
	 * 
	 * @param o object to compare
	 * @return true if object is instance of Job, with the same id, or false if
	 *         not.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Job) {
			Job st = (Job) o;
			return st.getId().equalsIgnoreCase(getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (id != null){
			return id.hashCode();
		} else {
			return super.hashCode();
		}
	}

	/**
	 * Compares the object with job. Used to sort, returns 1 if priority of
	 * object parameter is less then this job priority, -1 if viceversa. If
	 * priorities are equals, compares started time and return result of
	 * "compareTo" of Date object. If object parameter is not a job, return 0
	 * 
	 * @param o object to compare
	 * @return returns 1 if priority of object parameter is less then this job
	 *         priority, -1 if viceversa. If priorities are equals, compares
	 *         started time and return result of "compareTo" of Date object. If
	 *         object parameter is not a job, return 0
	 */

	@Override
	public int compareTo(Object o) {
		if (o instanceof Job) {
			Job ojob = (Job) o;
			Jcl ojcl = ojob.getJcl();
			if (ojcl.getPriority() < getJcl().getPriority()) {
				return 1;
			} else if (ojcl.getPriority() > getJcl().getPriority()) {
				return -1;
			} else {
				return getSubmittedTime().compareTo(ojob.getSubmittedTime());
			}
		}
		return 0;
	}

	/**
	 * @return the routingInfo
	 */
	public RoutingInfo getRoutingInfo() {
		return routingInfo;
	}

	/**
	 * @param routingInfo the routingInfo to set
	 */
	public void setRoutingInfo(RoutingInfo routingInfo) {
		this.routingInfo = routingInfo;
	}

	/**
	 * @return the nowait that indicates if the job is been submitted in nowait
	 *         mode or not. If nowait is true this means that the sumbitter will
	 *         not wait for the end of the execution otherwise it will wate
	 */
	public boolean isNowait() {
		return nowait;
	}

	/**
	 * @param nowait the nowait to set. If nowait is true this means that the
	 *            sumbitter will not wait for the end of the execution otherwise
	 *            it will wate
	 */
	public void setNowait(boolean nowait) {
		this.nowait = nowait;
	}
}