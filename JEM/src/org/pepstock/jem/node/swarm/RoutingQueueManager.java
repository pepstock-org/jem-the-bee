/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Simone "Busy" Businaro
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.JobComparator;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.RoutingQueuePredicate;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.swarm.executors.RouterIn;

import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;

/**
 * 
 * Manages all activities related to routing queue. It's able to listen when new
 * jobs are put in routing queue:
 * {@value org.pepstock.jem.node.Queues#ROUTING_QUEUE} and checks if it can be
 * routed or not that is if the environment to which it refers is actually
 * connected to the "swarm" environment.
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class RoutingQueueManager implements EntryListener<String, Job> {

	private JobComparator comparator = new JobComparator();

	private boolean routeEnded = true;

	/**
	 * Constructor
	 */
	public RoutingQueueManager() {
		IMap<String, Job> routingQueue = Main.getHazelcast().getMap(Queues.ROUTING_QUEUE);
		routingQueue.addEntryListener(this, true);
	}

	/**
	 * If MainSwarm node is ACTIVE route job if isRoutingCommitted = false and
	 * is not in hold
	 * 
	 */
	@Override
	public void entryAdded(EntryEvent<String, Job> event) {
		if (Main.SWARM.getStatus().equals(Status.ACTIVE) && event.getValue().getRoutingInfo().isRoutingCommitted() == null && !event.getValue().getJcl().isHold()) {
			routeJob(event.getValue());
		}
	}

	@Override
	public void entryEvicted(EntryEvent<String, Job> event) {
		// do nothing
	}

	@Override
	public void entryRemoved(EntryEvent<String, Job> event) {
		LogAppl.getInstance().emit(SwarmNodeMessage.JEMO012I, event.getValue());
	}

	/**
	 * If MainSwarm node is ACTIVE route job if isRoutingCommitted = false and
	 * is not in hold
	 */
	@Override
	public void entryUpdated(EntryEvent<String, Job> event) {
		if (Main.SWARM.getStatus().equals(Status.ACTIVE) && event.getValue().getRoutingInfo().isRoutingCommitted() == null && !event.getValue().getJcl().isHold()) {
			routeJob(event.getValue());
		}
	}

	/**
	 * This method will rout all the jobs present in the routing queue that have
	 * an environment like one of the nodes present in the
	 * {@value SwarmQueues#NODES_MAP}.
	 * 
	 * A job is routed through the use of a ExecutorService
	 * 
	 * @param environment the environment of the jobs to be routed
	 */
	public synchronized void routeJobsByAvailableEnvironments() {
		if (Main.SWARM.getStatus().equals(Status.ACTIVE)) {
			IMap<String, NodeInfo> nodesMap = Main.SWARM.getHazelcastInstance().getMap(SwarmQueues.NODES_MAP);
			Collection<NodeInfo> nodes = nodesMap.values();
			// route job only if nodes exist
			if (nodes != null && !nodes.isEmpty()) {
				Set<String> environments = new HashSet<String>();
				Iterator<NodeInfo> nodesiter = nodes.iterator();
				while (nodesiter.hasNext()) {
					environments.add(nodesiter.next().getExecutionEnvironment().getEnvironment());
				}
				IMap<String, Job> routingQueue = Main.getHazelcast().getMap(Queues.ROUTING_QUEUE);
				RoutingQueuePredicate rqp = new RoutingQueuePredicate();
				rqp.setEnvironments(environments);
				Collection<Job> jobs = routingQueue.values(rqp);
				// sort jobs
				List<Job> queuedJobs = new ArrayList<Job>(jobs);
				Collections.sort(queuedJobs, comparator);
				for (Job currJob : queuedJobs) {
					routeJob(currJob);
				}
			}
		}
	}

	/**
	 * This method will rout a single job by firing a DistributedTask via
	 * ExecutorService addressing a single member of the cluster that can handle
	 * the environment of the job.
	 * <p>
	 * Once the job has been routed, it will be removed from the ROUTING QUEUE.
	 * <p>
	 * In the correct environment, starting from the job, a prejob will be
	 * created and submitted in the CHECKING QUEUE.
	 * <p>
	 * Once the job will be put in the OUTPUT QUEUE this environment will be
	 * notified and the job will be submitted in the ROUTED QUEUE.
	 * 
	 * @param currJob the job to be routed
	 * @param member the member to which the job will be routed
	 */
	public synchronized void routeJob(Job currJob) {
		if (Main.SWARM.getStatus().equals(Status.ACTIVE)) {
			setRouteEnded(false);
			IMap<String, Job> routingQueue = Main.getHazelcast().getMap(Queues.ROUTING_QUEUE);
			IMap<String, NodeInfo> nodesMap = Main.SWARM.getHazelcastInstance().getMap(SwarmQueues.NODES_MAP);
			try {
				// lock the entry of the job
				routingQueue.lock(currJob.getId());
				// look if job is still there, I don't know if another node
				// has
				// routed it
				Job job = routingQueue.get(currJob.getId());
				if (job != null && job.getRoutingInfo().isRoutingCommitted() == null) {
					MapSwarmNodePredicate mnp = new MapSwarmNodePredicate();
					mnp.setEnvironment(job.getJcl().getEnvironment());
					Member member = MapSwarmNodesManager.getMember(nodesMap.values(mnp));
					// check if member is still available otherwise do
					// nothing
					if (member != null) {
						LogAppl.getInstance().emit(SwarmNodeMessage.JEMO009I, job);
						// route the job to the specific member
						DistributedTask<Boolean> task = new DistributedTask<Boolean>(new RouterIn(job, Main.EXECUTION_ENVIRONMENT.getEnvironment()), member);
						ExecutorService executorService = Main.SWARM.getHazelcastInstance().getExecutorService();
						// start 2 phase commit
						job.getRoutingInfo().setRoutingCommitted(false);
						routingQueue.put(job.getId(), job);
						executorService.execute(task);
						// if get response from task remove job from routing
						// queue
						if (task.get()) {
							// now routing is commit
							routingQueue.remove(job.getId());
							LogAppl.getInstance().emit(SwarmNodeMessage.JEMO010I, job);
						}
					}
				}
			} catch (Exception e) {
				LogAppl.getInstance().emit(SwarmNodeMessage.JEMO011E, currJob, e);
			} finally {
				if (routingQueue != null) {
					routingQueue.unlock(currJob.getId());
				}
			}
			setRouteEnded(true);
		}
	}

	/**
	 * @return the routeEnded
	 */
	public boolean isRouteEnded() {
		return routeEnded;
	}

	/**
	 * @param routeEnded the routeEnded to set
	 */
	private void setRouteEnded(boolean routeEnded) {
		this.routeEnded = routeEnded;
	}
}