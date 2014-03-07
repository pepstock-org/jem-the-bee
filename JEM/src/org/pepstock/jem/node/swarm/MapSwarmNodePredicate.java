/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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
package org.pepstock.jem.node.swarm;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pepstock.jem.node.NodeInfo;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicates.AbstractPredicate;

/**
 * Is a custom predicate (used by Hazelcast to filter object from maps) to
 * extract from swarm nodes map the nodes that belong to a specific environmnet<br>
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class MapSwarmNodePredicate extends AbstractPredicate {

	private static final long serialVersionUID = 1L;

	private String environment = null;

	/**
	 * Empty constructor
	 */
	public MapSwarmNodePredicate() {
	}

	/**
	 * Returns the environment
	 * 
	 * @return the environment
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * Sets the environment
	 * 
	 * @param environment
	 *            the executionEnviroment to set
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
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
	public boolean apply(@SuppressWarnings("rawtypes") MapEntry entry) {
		// gets job instance and JCL
		NodeInfo nodeInfo = (NodeInfo) entry.getValue();
		if (nodeInfo == null) {
			return false;
		}
		if (nodeInfo.getExecutionEnvironment() == null) {
			return false;
		}
		if (nodeInfo.getExecutionEnvironment().getEnvironment() == null) {
			return false;
		}
		if (nodeInfo.getExecutionEnvironment().getEnvironment().equalsIgnoreCase(environment)) {
			return true;
		}
		return false;
	}

	/**
	 * DeSerializes ExecutionEnviroment from XML
	 * 
	 * @see com.hazelcast.nio.DataSerializable#readData(java.io.DataInput)
	 */
	@Override
	public void readData(DataInput data) throws IOException {
		String ee = data.readLine();
		environment = ee;
	}

	/**
	 * Serializes Environment to XML
	 * 
	 * @see com.hazelcast.nio.DataSerializable#writeData(java.io.DataOutput)
	 */
	@Override
	public void writeData(DataOutput data) throws IOException {
		data.writeBytes(environment);
	}

}