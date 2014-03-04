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
package org.pepstock.jem;

import java.io.Serializable;


/**
 * Abstract which contains the environment, domain and affinity information for the node. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class AbstractExecutionEnvironment implements Serializable {
	
	/**
	 * Default domain, value "default".
	 */
	public static final String DEFAULT_DOMAIN = "***";

	private static final long serialVersionUID = 1L;

	private String environment = null;

	private String domain = DEFAULT_DOMAIN;

	/**
	 * Empty constructor
	 */
	public AbstractExecutionEnvironment() {
	}

	/**
	 * Returns the name of cluster (see Hazelcast "group" definition) that the
	 * node uses to submit jobs.
	 * 
	 * @return the name of cluster
	 */
	public final String getEnvironment() {
		return environment;
	}

	/**
	 * Sets the name of cluster that the node uses to submit jobs.
	 * 
	 * @param environment the name of cluster
	 */
	public final void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * Returns the name of domain. Domain is a subset of nodes that the node
	 * uses to submit jobs. Default is Job.DEFAULT_DOMAIN
	 * 
	 * @see org.pepstock.jem.Job#DEFAULT_DOMAIN
	 * @return the name of domain
	 */
	public final String getDomain() {
		return domain;
	}

	/**
	 * Sets the name of domain that the node uses to submit jobs.
	 * 
	 * @param domain the name of domain
	 */
	public final void setDomain(String domain) {
		this.domain = domain;
	}
	
}