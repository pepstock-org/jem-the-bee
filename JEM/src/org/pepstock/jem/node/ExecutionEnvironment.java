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
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.AbstractExecutionEnvironment;
import org.pepstock.jem.Jcl;

/**
 * Contains the environment, domain and affinity information for the node. They
 * are configured in JEM configuration file inside the
 * <code>&lt;environment&gt;</code>, <code>&lt;domain&gt;</code> and
 * <code>&lt;affinity&gt;</code> elements.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ExecutionEnvironment extends AbstractExecutionEnvironment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Maximum number of submitters
	 */
	//minus 1 to avoid FindBugs issue
	public static final int MAXIMUM_PARALLEL_JOBS = Integer.MAX_VALUE-1;
	
	/**
	 * Minimum number of submitters
	 */
	public static final int MINIMUM_PARALLEL_JOBS = 1;
	
	/**
	 * Default number of submitters, equals to minimum one
	 */
	public static final int DEFAULT_PARALLEL_JOBS = MINIMUM_PARALLEL_JOBS;
	
	/**
	 * Maximum amount of memory.
	 */
	// minus 1 to avoid FindBugs issue
	public static final int MAXIMUM_MEMORY = Integer.MAX_VALUE-1;
	
	/**
	 * Minimum amount of memory, equals to minimum one
	 */
	public static final int MINIMUM_MEMORY = 0;

	private List<String> staticAffinities = new LinkedList<String>();
	
	private List<String> dynamicAffinities = new LinkedList<String>();
	
	private int memory = Jcl.DEFAULT_MEMORY;
	
	private int parallelJobs = DEFAULT_PARALLEL_JOBS;
	
	/**
	 * Empty constructor
	 */
	public ExecutionEnvironment() {
	}

	/**
	 * Returns the collections of names of affinity that the node uses to submit jobs
	 * 
	 * @return the list of names name of affinity
	 */
	public List<String> getAllAffinities() {
		List<String> allAffinities = new LinkedList<String>();
		allAffinities.addAll(staticAffinities);
		allAffinities.addAll(dynamicAffinities);
		return allAffinities;
	}
	
	/**
	 * @return the staticAffinities
	 */
	public List<String> getStaticAffinities() {
		return staticAffinities;
	}

	/**
	 * @param staticAffinities the staticAffinities to set
	 */
	public void setStaticAffinities(List<String> staticAffinities) {
		this.staticAffinities = staticAffinities;
	}

	/**
	 * @return the dynamicAffinities
	 */
	public List<String> getDynamicAffinities() {
		return dynamicAffinities;
	}

	/**
	 * @param dynamicAffinities the dynamicAffinities to set
	 */
	public void setDynamicAffinities(List<String> dynamicAffinities) {
		this.dynamicAffinities = dynamicAffinities;
	}

	/**
	 * Returns the memory which will be used by node to start the process
	 * 
	 * @return the memory in MB
	 */
	public int getMemory() {
		return memory;
	}

	/**
	 * Sets the memory which will be used by node to start the process
	 * 
	 * @param memory the memory in MB to set
	 */
	public void setMemory(int memory) {
		this.memory = memory;
	}

	/**
	 * Returns parallelism to use in node 
	 * @return the parallelJobs
	 */
	public int getParallelJobs() {
		return parallelJobs;
	}

	/**
	 * Sets parallelism to use in node
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
		return "ExecutionEnvironment [environment=" + getEnvironment() + ", domain=" + getDomain() + ", staticAffinities=" + staticAffinities + ", dynamicAffinities=" + dynamicAffinities + ", memory=" + memory + ", parallelJobs=" + parallelJobs + "]";
	}

}