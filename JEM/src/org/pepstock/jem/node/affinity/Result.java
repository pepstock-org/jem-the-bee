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
package org.pepstock.jem.node.affinity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains memory value and affinities labels, created by a custom loader, which are used
 * by node to understand what jobs could be executed in the node 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Result implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int memory = Integer.MAX_VALUE;
	
	private int parallelJobs = Integer.MAX_VALUE;
	
	private List<String> affinities = new ArrayList<String>();

	/**
	 * Empty constructor
	 */
	public Result() {
	}

	/**
	 * Returns the memory estimated necessary to execute the job and used by node
	 * 
	 * @return the memory in MB
	 */
	public int getMemory() {
		return memory;
	}

	/**
	 * Sets the memory estimated necessary to execute the job and used by node
	 * 
	 * @param memory the memory in MB to set
	 */
	public void setMemory(int memory) {
		this.memory = memory;
	}

	/**
	 * Returns the collection of strings which are affinity labels
	 * 
	 * @return the affinities labels
	 */
	public List<String> getAffinities() {
		return affinities;
	}

	/**
	 * Sets the collection of strings which are affinity labels
	 * 
	 * @param affinities the affinities to set
	 */
	public void setAffinities(List<String> affinities) {
		this.affinities = affinities;
	}
	
	
	
	/**
	 * Returns the maximum number of jobs which can be executed at the same time
	 * 
	 * @return the parallelJobs
	 */
	public int getParallelJobs() {
		return parallelJobs;
	}

	/**
	 * Sets the maximum number of jobs which can be executed at the same time
	 * 
	 * @param parallelJobs the parallelJobs to set
	 */
	public void setParallelJobs(int parallelJobs) {
		this.parallelJobs = parallelJobs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Result [memory=" + memory + ", parallelJobs=" + parallelJobs + ", affinities=" + affinities + "]";
	}


}