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
package org.pepstock.jem.protocol;

import java.util.Collection;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.protocol.worker.JobIdWorker;
import org.pepstock.jem.protocol.worker.PrintOutputWorker;
import org.pepstock.jem.protocol.worker.SessionWorker;
import org.pepstock.jem.protocol.worker.SubmitJobWorker;

import com.hazelcast.core.IMap;

/**
 * Factory used to create the message with contains all JEM members of the cluster and
 * creates the worker based on code of the message.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class ServerFactory {

	/**
	 * to avoid any instantiation
	 */
	private ServerFactory() {
	}
	
	/**
	 * Creates the message with all the members of cluster. 
	 * @return the message with all the members of cluster
	 * @throws JemException if any error occurs
	 */
	public static Message createMembers() throws JemException{
		// creates the empty string builder for all members
		StringBuilder addresses = new StringBuilder();
		// gets the node map
		IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
		try {
			// gets all nodes
			Collection<NodeInfo> allNodes = membersMap.values();
			// scans all node to get the ip-address and tcp port
			for (NodeInfo currNodeInfo : allNodes) {
				String address = currNodeInfo.getIpaddress() + ":" + currNodeInfo.getTcpPort();
				// checks if is already filled with
				// at least a value
				if (addresses.length() == 0){
					// adds to the collection
					addresses.append(address);
				} else {
					addresses.append(",").append(address);
				}
			}
			// sets the collection
			return ObjectFactory.createMessage(MessageCodes.GET_MEMBERS, addresses.toString(), String.class);
		} catch (Exception ex) {
			throw new JemException(ex);
		}
	}
	
	
	/**
	 * Checks the job ended and starts a worker to send the notification
	 * @param session client session to be notified
	 * @param endedJob ended job instance
	 * @throws JemException if any errors occurs
	 */
	static Message createJobEnded(Session session, Job endedJob) throws JemException{
		String id;
		// check if it was a routed job
		// if nowait is false remove job from ROUTED QUEUE
		if (endedJob.getRoutingInfo().getId() != null) {
			// sets the message ID with routed info
			id = endedJob.getRoutingInfo().getId();
			if (!endedJob.isNowait()) {
				IMap<String, Job> routedQueue = Main.getHazelcast().getMap(Queues.ROUTED_QUEUE);
				routedQueue.remove(endedJob.getRoutingInfo().getId());
			}
		} else {
			// sets the job id as message ID
			id = endedJob.getId();
		}
		// creates the message
		return ObjectFactory.createMessage(id, MessageCodes.ENDED_JOB, endedJob, Job.class);
	}
	
	/**
	 * Returns the worker which should handle the message, read from client socket.<br>
	 * If code of message is not valid, returns null.
	 * 
	 * @param session client session which sent the data
	 * @param buffer buffer with the data
	 * @return the worker otherwise null if the code is not valid
	 * @throws JemException 
	 */
	static Worker createWorker(Session session, Message message) throws JemException{
		Worker worker = null;
		// creates the worker depending on
		// the code of the message
		switch (message.getCode()) {
			case MessageCodes.SESSION_CREATED:
				worker = new SessionWorker(session, message);
				break;
			case MessageCodes.GET_JOBID:
				worker = new JobIdWorker(session, message);
				break;
			case MessageCodes.SUBMIT_JOB:
				worker = new SubmitJobWorker(session, message);
				break;
			case MessageCodes.GET_PRINT_OUTPUT:
				worker = new PrintOutputWorker(session, message);
				break;
			case MessageCodes.HEARTBEAT:
				worker = new Worker(session, message);
				break;
			default:
				worker = null;
		}
		return worker;
	}
}
