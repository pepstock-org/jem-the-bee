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

import java.util.Iterator;

import org.pepstock.jem.Job;

/**
 * Is a custom predicate (used by Hazelcast to filter object from maps) to
 * extract from the output queue:
 * {@value org.pepstock.jem.node.Queues#OUTPUT_QUEUE} only jobs which belongs to
 * a specific set of environments.<br>
 * Uses also filter for job:<br>
 * <ol>
 * <li>the RoutingInfo().isOutputCommitted must be null</li>
 * </li> <br>
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class OutputQueuePredicate extends EnvironmentsPredicate {

	private static final long serialVersionUID = 1L;

	/**
	 * Checks the job passed by Hazelcast matches with Execution Environment of
	 * node. <br>
	 * This method is called on all nodes of cluster, where a piece of map is
	 * present
	 * 
	 * @see com.hazelcast.query.Predicate#apply(com.hazelcast.core.MapEntry)
	 */
	@Override
	public boolean check(Job job) {
		if (job.getRoutingInfo() == null) {
			return false;
		}
		if (job.getRoutingInfo().isOutputCommitted() == null) {
			// if doens't have the same environment, skip it
			Iterator<String> envsIter = getEnvironments().iterator();
			while (envsIter.hasNext()) {
				if (envsIter.next().equalsIgnoreCase(job.getJcl().getEnvironment())) {
					return true;
				}
			}
		}
		return false;
	}
}