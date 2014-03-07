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
package org.pepstock.jem.gwt.client.panels.administration.workload;

import org.pepstock.jem.gwt.client.charts.KeyData;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class Workload extends KeyData{

	private int jobsSubmitted = 0;
	
	private int jclsChecked = 0;

	/**
	 * @return the jobsSubmitted
	 */
	public int getJobsSubmitted() {
		return jobsSubmitted;
	}

	/**
	 * @param jobsSubmitted the jobsSubmitted to set
	 */
	public void setJobsSubmitted(int jobsSubmitted) {
		this.jobsSubmitted = jobsSubmitted;
	}

	/**
	 * @return the jclsChecked
	 */
	public int getJclsChecked() {
		return jclsChecked;
	}

	/**
	 * @param jclsChecked the jclsChecked to set
	 */
	public void setJclsChecked(int jclsChecked) {
		this.jclsChecked = jclsChecked;
	}
}