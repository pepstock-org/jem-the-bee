/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.events;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.Queues;

/**
 * Internal event, with the status change and job instance
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobLifecycleEvent {

	private String status = Queues.INPUT_QUEUE;

	private Job job = null;

	/**
	 * Constructs a event with status and job instance.<br>
	 * Status values could be the constants on Queues object.
	 * 
	 * @see org.pepstock.jem.node.Queues#INPUT_QUEUE
	 * @see org.pepstock.jem.node.Queues#RUNNING_QUEUE
	 * @see org.pepstock.jem.node.Queues#OUTPUT_QUEUE
	 * 
	 * @param status status changed
	 * @param job job instance
	 */
	public JobLifecycleEvent(String status, Job job) {
		this.job = job;
		this.status = status;
	}

	/**
	 * Returns the changed status of job
	 * 
	 * @return status string value
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the changed status of job
	 * 
	 * @param status status string value
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Returns the job instance
	 * 
	 * @return job instance
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * Sets the job instance
	 * 
	 * @param job job instance
	 */
	public void setJob(Job job) {
		this.job = job;
	}

}