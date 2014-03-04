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
package org.pepstock.jem.node.configuration;

import java.io.Serializable;

import org.pepstock.jem.AbstractExecutionEnvironment;
import org.pepstock.jem.Jcl;

/**
 * Contains the environment, domain and affinity information for the node.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ExecutionEnvironment extends AbstractExecutionEnvironment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String affinity = Jcl.DEFAULT_AFFINITY;
	
	private AffinityFactory affinityFactory = null;
	
	private String parallelJobs = null;
	
	private String memory = null;

	/**
	 * Empty constructor
	 */
	public ExecutionEnvironment() {
	}

	/**
	 * Returns the name of affinity that the node uses to submit jobs.
	 * 
	 * @return the name of affinity
	 */
	public String getAffinity() {
		return affinity;
	}

	/**
	 * Sets the name of affinity that the node uses to submit jobs.
	 * 
	 * @param affinity the name of affinity
	 */
	public void setAffinity(String affinity) {
		this.affinity = affinity;
	}

	/**
	 * Returns the factory to load affinities and set memory limit
	 * 
	 * @return the affinityFactory
	 * @see AffinityFactory
	 */
	public AffinityFactory getAffinityFactory() {
		return affinityFactory;
	}

	/**
	 * Sets the factory to load affinities and set memory limit
	 * 
	 * @param affinityFactory the affinityFactory to set
	 * @see AffinityFactory
	 */
	public void setAffinityFactory(AffinityFactory affinityFactory) {
		this.affinityFactory = affinityFactory;
	}

	/**
	 * Returns parallelism to use in node 
	 * @return the parallelJobs
	 */
	public String getParallelJobs() {
		return parallelJobs;
	}

	/**
	 * Sets parallelism to use in node
	 * @param parallelJobs the parallelJobs to set
	 */
	public void setParallelJobs(String parallelJobs) {
		this.parallelJobs = parallelJobs;
	}

	/**
	 * Returns the memory attribute
	 * 
	 * @return the memory
	 */
	public String getMemory() {
		return memory;
	}

	/**
	 * Sets the memory attribute
	 * 
	 * @param memory the memory to set
	 */
	public void setMemory(String memory) {
		this.memory = memory;
	}
}