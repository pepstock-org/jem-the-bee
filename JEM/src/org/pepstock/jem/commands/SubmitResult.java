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
package org.pepstock.jem.commands;

/**
 * Wrapper which represents the result of job execution. It contains the return code
 * and job id
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class SubmitResult {

	private int rc;

	private String jobId;

	/**
	 * Empty constructor 
	 */
	public SubmitResult() {
	}

	/**
	 * Constructs teh object using both return code and job id
	 * @param rc the return code of the job
	 * @param jobId
	 */
	public SubmitResult(int rc, String jobId) {
		super();
		this.rc = rc;
		this.jobId = jobId;
	}

	/**
	 * @return the return code of the job
	 */
	public int getRc() {
		return rc;
	}

	/**
	 * @param rc the return code of the job to set
	 */
	public void setRc(int rc) {
		this.rc = rc;
	}

	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubmitResult [rc=" + rc + ", jobId=" + jobId + "]";
	}
	
}