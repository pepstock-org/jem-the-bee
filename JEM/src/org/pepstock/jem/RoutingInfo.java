/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Simone "Busy" Businaro
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
import java.util.Date;

/**
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class RoutingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id = null;

	private Date routedTime = null;

	private Date submittedTime = null;

	private String environment = null;

	private Boolean routingCommitted = null;

	private Boolean outputCommitted = null;

	/**
	 * Empty constructor
	 */
	public RoutingInfo() {

	}

	/**
	 * @param id is the id of job that was calculated from the environment that
	 *            routed the job
	 * @param routedTime is the time when the job was routed
	 * @param submittedTime is the time when the job was submitted in the
	 *            environment that routed the job
	 * @param environment is the environment that routed the job
	 */
	public RoutingInfo(String id, Date routedTime, Date submittedTime, String environment) {
		super();
		this.id = id;
		this.routedTime = routedTime;
		this.submittedTime = submittedTime;
		this.environment = environment;
	}

	/**
	 * @return the id of job that was calculated from the environment that
	 *         routed the job
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id of job that was calculated from the environment that
	 *            routed the job
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the routedTime
	 */
	public Date getRoutedTime() {
		return routedTime;
	}

	/**
	 * @param routedTime the routedTime to set. Is the time when the job was
	 *            routed
	 */
	public void setRoutedTime(Date routedTime) {
		this.routedTime = routedTime;
	}

	/**
	 * @return the environment that routed the job
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment that routed the job
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * @return the time when the job was submitted in the environment that
	 *         routed the job
	 */
	public Date getSubmittedTime() {
		return submittedTime;
	}

	/**
	 * @param submittedTime the time when the job was submitted in the
	 *            environment that routed the job
	 */
	public void setSubmittedTime(Date submittedTime) {
		this.submittedTime = submittedTime;
	}

	/**
	 * A routing job is committed if we send it in the new environment and we
	 * receive acknowledge that as been put in the CHECKING QUEUE of the correct
	 * environment. If a job remain in the ROUTING QUEUE with isCommitted set to
	 * false this means that we did not received information about submission. A
	 * job in the ROUTING QUEUE with isCommitted set to false will not be
	 * routed.
	 * 
	 * @return the committed.
	 * 
	 */
	public Boolean isRoutingCommitted() {
		return routingCommitted;
	}

	/**
	 * A routing job is committed if we send it in the new environment and we
	 * receive acknowledge that as been put in the CHECKING QUEUE of the correct
	 * environment. If a job remain in the ROUTING QUEUE with isCommitted set to
	 * false this means that we did not received information about submission. A
	 * job in the ROUTING QUEUE with isCommitted set to false will not be
	 * routed.
	 * 
	 * @param committed the committed to set
	 */
	public void setRoutingCommitted(Boolean committed) {
		this.routingCommitted = committed;
	}

	/**
	 * If true means that the end of the job has been notified to the
	 * environment that routed the job
	 * 
	 * @return the outputNotify
	 */
	public Boolean isOutputCommitted() {
		return outputCommitted;
	}

	/**
	 * If true means that the end of the job has been notified to the
	 * environment that routed the job
	 * 
	 * @param outputCommitted the outputNotify to set
	 */
	public void setOutputCommitted(boolean outputCommitted) {
		this.outputCommitted = outputCommitted;
	}
}