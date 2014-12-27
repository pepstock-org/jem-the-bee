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

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputListItem;

/**
 * POJO container a job and output list item with output file to load.<br>
 * Uses the annotation XmlRootElement to be serialized.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */
@XmlRootElement
public class JobOutputListArgument implements Serializable{

    private static final long serialVersionUID = 1L;

	private Job job = null;
	
	private OutputListItem item = null;
	
	/**
	 * Empty constructor
	 */
	public JobOutputListArgument() {
	}

	/**
	 * Returns job instance
	 * @return the job
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * Sets job instance
	 * @param job the job to set
	 */
	public void setJob(Job job) {
		this.job = job;
	}

	/**
	 * Returns output list item
	 * @return the item
	 */
	public OutputListItem getItem() {
		return item;
	}

	/**
	 * Sets output list item
	 * @param item the item to set
	 */
	public void setItem(OutputListItem item) {
		this.item = item;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "JobOutputFileContent [job=" + job + ", item=" + item + "]";
    }

}