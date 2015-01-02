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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.JobSystemActivity;


/**
 * Represents job system activity content wrapper, for jobs which are running, necessary for rest calls.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@XmlRootElement
public class JobSystemActivityContent extends ReturnedObject implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private JobSystemActivity jobSystemActivity = null;

	/**
	 * Empty constructor
	 */
	public JobSystemActivityContent() {
		
	}

	/**
	 * @return the jobSystemActivity
	 */
	public JobSystemActivity getJobSystemActivity() {
		return jobSystemActivity;
	}

	/**
	 * @param jobSystemActivity the jobSystemActivity to set
	 */
	public void setJobSystemActivity(JobSystemActivity jobSystemActivity) {
		this.jobSystemActivity = jobSystemActivity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JobSystemActivityContent [jobSystemActivity=" + jobSystemActivity + "]";
	}

}
