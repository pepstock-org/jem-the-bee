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
package org.pepstock.jem.node.swarm.executors;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.hazelcast.Topics;
import org.pepstock.jem.node.swarm.SwarmException;
import org.pepstock.jem.node.swarm.SwarmNodeMessage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;

/**
 * This class is responsible to send back the job that was executed after it was
 * routed so to inform the client that submitted it that it is ended
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class RouterOut implements Callable<Boolean>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The routed job
	 */
	private Job job = null;

	/**
	 * 
	 * @param job the routed job
	 */
	public RouterOut(Job job) {
		this.job = job;
	}

	/**
	 * Send back the job to the environment that rout it to the correct
	 * environment. It than put the ended job to the topic queue so to inform
	 * the client that submitted it that it is ended
	 * 
	 * @throws Exception
	 */
	@Override
	public Boolean call() throws SwarmException {
		HazelcastInstance hazelcastInstance = Main.getHazelcast();
		if (hazelcastInstance == null || !hazelcastInstance.getLifecycleService().isRunning()){
			throw new SwarmException(SwarmNodeMessage.JEMO008E, job);
		}
		IMap<String, Job> routedQueue = hazelcastInstance.getMap(Queues.ROUTED_QUEUE);
		// if job is in waiting mode than add job to ROUTED QUEUE
		if (!job.isNowait()) {
			routedQueue.put(job.getRoutingInfo().getId(), job);
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO013I, job);
			// gets topic object and adds itself as listener
			ITopic<Job> topic = hazelcastInstance.getTopic(Topics.ENDED_JOB);
			topic.publish(job);
		}
		return true;
	}
}