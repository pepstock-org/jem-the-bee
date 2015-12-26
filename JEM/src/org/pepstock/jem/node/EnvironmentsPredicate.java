/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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

import java.util.Map.Entry;
import java.util.Set;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.util.InternalAbstractPredicate;

/**
 * Is a custom predicate (used by Hazelcast to filter object from maps) to
 * extract from the queues by enviroments 
 * Uses also filter for job:<br>
 * <ol>
 * <li>the RoutingInfo().isOutputCommitted must be null</li>
 * </li> <br>
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public abstract class EnvironmentsPredicate extends InternalAbstractPredicate<Set<String>> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Empty constructor
	 */
	public EnvironmentsPredicate() {
	}

	/**
	 * Checks the job passed by Hazelcast matches with Execution Environment of
	 * node. <br>
	 * This method is called on all nodes of cluster, where a piece of map is
	 * present
	 * 
	 * @see com.hazelcast.query.Predicate#apply(com.hazelcast.core.MapEntry)
	 */
	@Override
	public boolean apply(@SuppressWarnings("rawtypes")Entry entry) {
		// gets job instance and JCL
		return apply((Job) entry.getValue());
	}

	/**
	 * Checks if the job fits the filter
	 * @param job job instance to check
	 * @return true if fits otherwise false;
	 */
	public final boolean apply(Job job) {
		// gets job instance and JCL
		if (job == null){
			return false;
		}
		Jcl jcl = job.getJcl();
		if (jcl == null){
			return false;
		}
		return check(job);
		
	}
	
	/**
	 * Checks if job must be included or not
	 * @param job job instance to check
	 * @return <code>true</code> if must be included, otherwise <code>false</code>.
	 */
	public abstract boolean check(Job job);
}