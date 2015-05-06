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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicates.AbstractPredicate;
import com.thoughtworks.xstream.XStream;

/**
 * Is a custom predicate (used by Hazelcast to filter object from maps) to
 * extract from input queue only jobs which could be executed by the node.<br>
 * Uses several filters for job:<br>
 * <ol>
 * <li>doesn't must be in HOLD</li>
 * <li>its execution environment must be the same of node</li>
 * <li>its domain must be the same of node or the default one</li>
 * <li>its memory request must be less than used one by node to execute
 * processes</li>
 * <li>its affinities must be included in list of affinities of node or the
 * default one</li>
 * </ol>
 * <br>
 * The object ExecutionEnvironment will be serialized because the predicate will
 * be executed on all nodes of Hazelcast (much faster!)
 * 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class InputQueuePredicate extends AbstractPredicate {

	private static final long serialVersionUID = 1L;

	private ExecutionEnvironment executionEnviroment = null;

	private transient XStream stream = new XStream();

	/**
	 * Empty constructor
	 */
	public InputQueuePredicate() {
	}

	/**
	 * Returns the execution environment
	 * 
	 * @return the executionEnviroment
	 */
	public ExecutionEnvironment getExecutionEnviroment() {
		return executionEnviroment;
	}

	/**
	 * Sets the execution environment
	 * 
	 * @param executionEnviroment the executionEnviroment to set
	 */
	public void setExecutionEnviroment(ExecutionEnvironment executionEnviroment) {
		this.executionEnviroment = executionEnviroment;
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
		Job job = (Job) entry.getValue();
		if (job == null) {
			return false;
		}
		Jcl jcl = job.getJcl();
		if (jcl == null) {
			return false;
		}

		// if is in hold, skips it
		// if doens't have the same environment, skips it
		// if doens't have the same domain or not the default, skips it
		// if required memory too high comparable with available node one, skips
		// it
		if (!jcl.isHold() && matchExecutionEnvironment(jcl) && job.getJcl().getMemory() <= executionEnviroment.getMemory()) {
			// if affinity is default, accepts it
			if (!jcl.getAffinity().equalsIgnoreCase(Jcl.DEFAULT_AFFINITY)) {
				// splits affinities, putting in lower case (to ignore case)
				String[] jobAffinities = jcl.getAffinity().split(Jcl.AFFINITY_SEPARATOR);
				// scans the job affinities
				for (int i = 0; i < jobAffinities.length; i++) {
					// if job affinity isn't in node affinities, skips it
					if (!containsIgnoreCase(executionEnviroment.getAllAffinities(), jobAffinities[i])) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Checks if JCL has the right execution enviroment properties
	 * 
	 * @param jcl jcl to check
	 * @return <code>true</code> if the enviroment matches ith JCL definition
	 */
	private boolean matchExecutionEnvironment(Jcl jcl) {
		return jcl.getEnvironment().equalsIgnoreCase(executionEnviroment.getEnvironment()) && (jcl.getDomain().equalsIgnoreCase(executionEnviroment.getDomain()) || jcl.getDomain().equalsIgnoreCase(Jcl.DEFAULT_DOMAIN));
	}

	/**
	 * DeSerializes ExecutionEnviroment from XML
	 * 
	 * @see com.hazelcast.nio.DataSerializable#readData(java.io.DataInput)
	 */
	@Override
	public void readData(DataInput data) throws IOException {
		String ee = data.readLine();
		executionEnviroment = (ExecutionEnvironment) stream.fromXML(ee);
	}

	/**
	 * Serializes ExecutionEnviroment to XML
	 * 
	 * @see com.hazelcast.nio.DataSerializable#writeData(java.io.DataOutput)
	 */
	@Override
	public void writeData(DataOutput data) throws IOException {
		// replace \n beacause are not supported from serialize engine
		String ee = stream.toXML(executionEnviroment).replace('\n', ' ');
		data.writeBytes(ee);
	}

	private boolean containsIgnoreCase(List<String> list, String string) {
		for (String currItem : list) {
			if (currItem.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
}