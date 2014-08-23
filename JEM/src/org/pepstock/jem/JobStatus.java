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
package org.pepstock.jem;

import java.io.Serializable;
import java.util.Collection;


/**
 * Entity which collects all information about a job on all queues'
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class JobStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Collection<Job> jobsInput = null;

    private Collection<Job> jobsRunning = null;
    
    private Collection<Job> jobsOutput = null;
    
    private Collection<Job> jobsRouting = null;
    
	/**
	 * Empty constructor
	 */
	public JobStatus() {
		
	}

	/**
	 * Returns jobs in input queue
	 * @return the jobsInput
	 */
	public Collection<Job> getJobsInput() {
		return jobsInput;
	}

	/**
	 * Sets jobs in input queue
	 * @param jobsInput the jobsInput to set
	 */
	public void setJobsInput(Collection<Job> jobsInput) {
		this.jobsInput = jobsInput;
	}

	/**
	 * Returns jobs in running queue
	 * @return the jobsRunning
	 */
	public Collection<Job> getJobsRunning() {
		return jobsRunning;
	}

	/**
	 * Sets jobs in input queue
	 * @param jobsRunning the jobsRunning to set
	 */
	public void setJobsRunning(Collection<Job> jobsRunning) {
		this.jobsRunning = jobsRunning;
	}

	/**
	 * Returns jobs in output queue
	 * @return the jobsOutput
	 */
	public Collection<Job> getJobsOutput() {
		return jobsOutput;
	}

	/**
	 * Sets jobs in output queue
	 * @param jobsOutput the jobsOutput to set
	 */
	public void setJobsOutput(Collection<Job> jobsOutput) {
		this.jobsOutput = jobsOutput;
	}

	/**
	 * Returns jobs in routing queue
	 * @return the jobsRouting
	 */
	public Collection<Job> getJobsRouting() {
		return jobsRouting;
	}

	/**
	 * Sets jobs in routing queue
	 * @param jobsRouting the jobsRouting to set
	 */
	public void setJobsRouting(Collection<Job> jobsRouting) {
		this.jobsRouting = jobsRouting;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "JobStatus [jobsInput=" + jobsInput + ", jobsRunning=" + jobsRunning + ", jobsOutput=" + jobsOutput + ", jobsRouting=" + jobsRouting + "]";
    }

	
}