/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.listeners;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.Job;
import org.pepstock.jem.Result;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.RequestLock;
import org.pepstock.jem.node.events.JobLifecycleEvent;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Implements the listener interface of Hazelcast cluster to listen when a
 * member is removed from cluster.<br>
 * It's necessary to check who is the coordinator and unlock all locks
 * previously asked.
 * 
 * @author Andrea "Stock" Stocchero
 * @see org.pepstock.jem.node.NodeInfo
 * 
 */
public class NodeListener implements MembershipListener, MessageListener<MembershipEvent> {

	private final List<MembershipEvent> membersRemoved = new LinkedList<MembershipEvent>();

	/**
	 * Empty constructor.
	 */
	public NodeListener() {
		ITopic<MembershipEvent> topic = Main.getHazelcast().getTopic(Queues.REMOVED_NODE_INFO_TOPIC);
		topic.addMessageListener(this);
	}

	/**
	 * Not implement because not necessary
	 * 
	 * @param event event of hazelcast
	 */
	@Override
	public void memberAdded(MembershipEvent event) {
		// do nothing
	}

	/**
	 * Checks who is the first member on the list will work, becoming the
	 * coordinator of group.
	 * 
	 * @see org.pepstock.jem.node.Main#IS_COORDINATOR
	 * @param event event of Hazelcast
	 */

	@Override
	public void memberRemoved(MembershipEvent event) {
		membersRemoved.add(event);
		// if node is shutting down, do nothing
		if (Main.IS_SHUTTING_DOWN.get()){
			return;
		}
		// saves always the node on map,
		// that solves data loss
		NodeInfoUtility.storeNodeInfo(Main.getNode());
		checkMembershipEvent();
	}

	private synchronized void checkMembershipEvent() {
		// get hazelcast cluster and local node
		Cluster cluster = Main.getHazelcast().getCluster();
		Member local = cluster.getLocalMember();

		for (Member member : cluster.getMembers()) {
			// if the first in the list is not the local
			// one, return
			// because only the first must work (it could be already the
			// coordinator but
			// tried to avoid double check
			if (!local.equals(member)){
				return;
			} else {
				// set that local node is coordinator
				// doesn't matter if we override with same value
				// the attribute Main.IS_COORDINATOR
				Main.IS_COORDINATOR.set(true);

				List<MembershipEvent> savedEvents = new LinkedList<MembershipEvent>(membersRemoved);
				for (MembershipEvent savedEvent : savedEvents) {
					// this node is the coordinator so ONLY teh coordinator must
					// work for all group
					actionsForCoordinator(savedEvent);
				}

				// afterwards return, stopping the cycle on FOR
				return;
			}

		}
	}

	/**
	 * Performs all actions necessary when a member is removed from cluster.<br>
	 * Current node will be a coordinator, setting the status UNKNOWN to removed
	 * node. It removes all locks previously asked to GRS system.
	 * 
	 * @param event event of Hazelcast
	 */
	private void actionsForCoordinator(MembershipEvent event) {
		// get member removed
		Member memberRemoved = event.getMember();

		// access to map using Uuid of member removed
		IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
		String key = memberRemoved.getUuid();

		// check is I have on nodes map
		if (membersMap.containsKey(key)) {
			try {
				membersMap.lock(key);
				// get object to set status unknown
				NodeInfo info = membersMap.remove(key);
				cleanUpNode(info);
				NodeInfoUtility.removeNodeInfoFromMapStore(key);
			} catch (Exception ex) {
				LogAppl.getInstance().emit(NodeMessage.JEMC174E, ex);
			} finally {
				membersMap.unlock(key);
			}
		} else {
			try {
				NodeInfo info = NodeInfoUtility.getNodeInfoFromMapStore(key);
				if (info != null) {
					cleanUpNode(info);
					NodeInfoUtility.removeNodeInfoFromMapStore(key);
				} else {
					// if not found, probably this is not correct!!!
					// it means that the list NODES_MAP is not well-maintained
					LogAppl.getInstance().emit(NodeMessage.JEMC024E, memberRemoved.toString(), Queues.NODES_MAP);
				}
			} catch (Exception ex) {
				LogAppl.getInstance().emit(NodeMessage.JEMC174E, ex);
			}
		}
		ITopic<MembershipEvent> topic = Main.getHazelcast().getTopic(Queues.REMOVED_NODE_INFO_TOPIC);
		topic.publish(event);
	}

	/**
	 * 
	 * @param info
	 * @param memberRemoved
	 */
	private void cleanUpNode(NodeInfo info) {
		// first check: has got request for lock pending to unlock?
		if (!info.getRequests().isEmpty()) {
			// get request for locking to check if we had some locks in
			// place
			for (RequestLock request : info.getRequests().values()) {
				// checks if request is null, because for list is null
				if (request != null && !request.getResources().isEmpty()) {
					info.unlockForFailover();
					// clear also the request list
					request.getResources().clear();
				}
			}
		}
		// second check: has got any jobs running during the crash?
		if (!info.getJobs().isEmpty()) {
			for (String job : info.getJobs().keySet()) {
				jobEnded(job, info.getJobs().get(job));
			}
		}

	}

	/**
	 * Moves the orphan job from running queue to output, setting CC 12.
	 * 
	 * @param job to move on output
	 */
	private void jobEnded(String jobId, String jobName) {

		Result result = new Result();
		result.setReturnCode(Result.FATAL);
		result.setExceptionMessage("Node is crashed during job was executing");

		// moves job from running to output
		IMap<String, Job> runningQueue = Main.getHazelcast().getMap(Queues.RUNNING_QUEUE);
		IMap<String, Job> outputQueue = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);

		Job job = null;
		try {
			runningQueue.lock(jobId);
			outputQueue.lock(jobId);

			job = runningQueue.remove(jobId);

			job.setResult(result);
			job.setEndedTime(new Date());
			job.setRunningStatus(Job.NONE);
			LogAppl.getInstance().emit(NodeMessage.JEMC021I, job.toString(), String.valueOf(job.getResult().getReturnCode()));

			outputQueue.put(job.getId(), job);

		} catch (Exception ex) {
			LogAppl.getInstance().emit(NodeMessage.JEMC175E, ex, jobName);
		} finally {
			runningQueue.unlock(jobId);
			outputQueue.unlock(jobId);
		}
		if (job != null) {
			// fires event that the job is ended
			Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.addJobLifecycleEvent(new JobLifecycleEvent(Queues.OUTPUT_QUEUE, job));

			// send a topic to client which is wait for
			ITopic<Job> topic = Main.getHazelcast().getTopic(Queues.ENDED_JOB_TOPIC);
			topic.publish(job);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
	 */
	@Override
	public void onMessage(Message<MembershipEvent> event) {
		Iterator<MembershipEvent> iter = membersRemoved.iterator();
		while (iter.hasNext()) {
			MembershipEvent savedEvent = iter.next();
			if (savedEvent.getMember().getUuid().equalsIgnoreCase(event.getMessageObject().getMember().getUuid())) {
				iter.remove();
				return;
			}
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC223W, event.getMessageObject().getMember());
	}
}