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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.Job;

/**
 * POJO container of jobs list and the queue which is containing the jobs.<br>
 * Uses the annotation XmlRootElement to be serialized.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
@XmlRootElement
public class Jobs extends ReturnedObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private Collection<Job> jobs = null;

	private String queueName = null;

	private String id = null;

	private boolean cancelForce = false;

	/**
	 * Empty constructor
	 */
	public Jobs() {
	}

	/**
	 * Returns the queue name
	 * 
	 * @see org.pepstock.jem.node.Queues
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Sets the queue name
	 * 
	 * @see org.pepstock.jem.node.Queues
	 * @param queueName the queueName to set
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * Returns the jobs collection
	 * 
	 * @return the jobs
	 */
	public Collection<Job> getJobs() {
		return jobs;
	}

	/**
	 * Sets the jobs collection
	 * 
	 * @param jobs the jobs to set
	 */
	public void setJobs(Collection<Job> jobs) {
		this.jobs = jobs;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the cancelForce
	 */
	public boolean isCancelForce() {
		return cancelForce;
	}

	/**
	 * @param cancelForce the cancelForce to set
	 */
	public void setCancelForce(boolean cancelForce) {
		this.cancelForce = cancelForce;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Jobs [jobs=" + jobs + ", queueName=" + queueName + ", id=" + id + ", cancelForce=" + cancelForce + "]";
	}

}