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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicates.AbstractPredicate;

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
public abstract class EnvironmentsPredicate extends AbstractPredicate {

	private static final long serialVersionUID = 1L;

	private Set<String> environments = null;

	/**
	 * Empty constructor
	 */
	public EnvironmentsPredicate() {
	}

	/**
	 * Returns the execution environments
	 * 
	 * @return the executionEnviroments
	 */
	public Set<String> getEnvironments() {
		return environments;
	}

	/**
	 * Sets the execution environments
	 * 
	 * @param environments the environments to set
	 */
	public void setEnvironments(Set<String> environments) {
		this.environments = environments;
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
	public final boolean apply(@SuppressWarnings("rawtypes") MapEntry entry) {
		// gets job instance and JCL
		Job job = (Job) entry.getValue();
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

	/**
	 * DeSerializes environments from Set
	 * 
	 * @see com.hazelcast.nio.DataSerializable#readData(java.io.DataInput)
	 */
	@Override
	public void readData(DataInput data) throws IOException {
		String ee = data.readLine();
		String setString = ee.substring(1, ee.length() - 1);
		String[] tokens = setString.split(",");
		environments = new HashSet<String>();
		for (String currToken : tokens) {
			environments.add(currToken.trim());
		}
	}

	/**
	 * Serializes Set of environment to String
	 * 
	 * @see com.hazelcast.nio.DataSerializable#writeData(java.io.DataOutput)
	 */
	@Override
	public void writeData(DataOutput data) throws IOException {
		String ee = environments.toString();
		data.writeBytes(ee);
	}

}