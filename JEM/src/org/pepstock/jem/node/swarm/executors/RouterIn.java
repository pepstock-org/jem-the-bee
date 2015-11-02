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
import java.util.Date;
import java.util.concurrent.Callable;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.SubmitException;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.SubmitPreJob;
import org.pepstock.jem.node.swarm.SwarmException;
import org.pepstock.jem.node.swarm.SwarmNodeMessage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IdGenerator;

/**
 * Is the Callable responsible to deliver the job to route to the right
 * environment. 
 * <br>
 * Once reached the right environment from the job create the
 * relative prejob and submit it in the CHECKING QUEUE
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class RouterIn implements Callable<Boolean>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The routed job
	 */
	private Job job = null;

	private String routingEnvironment = null;

	/**
	 * Constructs the executor, called by another environment
	 * @param job the routing job
	 * @param routingEnvironment the routing environment
	 */
	public RouterIn(Job job, String routingEnvironment) {
		this.job = job;
		this.routingEnvironment = routingEnvironment;
	}

	/**
	 * Create a prejob from routed job, collect routing data to put in job than
	 * submit the prejob to the CHECKING QUEUE.
	 * 
	 * @throws exception if hazelcast instance is down
	 */
	@Override
	public Boolean call() throws SwarmException {
		// gets the JEM hazelcast instance
		HazelcastInstance hazelcastInstance = Main.getHazelcast();
		// checks if is valid and running
		if (hazelcastInstance == null || !hazelcastInstance.getLifecycleService().isRunning()){
			throw new SwarmException(SwarmNodeMessage.JEMO008E, job);
		}
		// setting job routing info
		job.getRoutingInfo().setId(job.getId());
		job.getRoutingInfo().setRoutedTime(new Date());
		job.getRoutingInfo().setSubmittedTime(job.getSubmittedTime());
		job.getRoutingInfo().setEnvironment(routingEnvironment);
		
		// creates a prejob to be submitted in the current environment
		PreJob preJob = new PreJob();
		preJob.setJclContent(job.getJcl().getContent());
		preJob.setJclType(job.getJcl().getType());
		// gets unique ID
		IdGenerator generator = Main.getHazelcast().getIdGenerator(Queues.JOB_ID_GENERATOR);
		long id = generator.newId();
		// creates job id
		String jobId = Factory.createJobId(job, id);
		preJob.setId(jobId);
		// sets job id
		job.setId(jobId);
		// sets job
		preJob.setJob(job);
		// gets JCL queue
		try {
			SubmitPreJob.submit(hazelcastInstance, preJob);
		} catch (SubmitException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new SwarmException(SwarmNodeMessage.JEMO015E, e);
		}
		return true;
	}
}