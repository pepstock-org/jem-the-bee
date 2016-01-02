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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.executors.jobs.GetMessagesLog;
import org.pepstock.jem.node.hazelcast.ExecutorServices;
import org.pepstock.jem.node.hazelcast.IdGenerators;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.protocol.message.JobIdMessage;
import org.pepstock.jem.protocol.message.MembersMessage;
import org.pepstock.jem.protocol.message.PrintOutputMessage;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.Member;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final  class ServerMessageFactory {
	
	//Max length for a long value. It uses numbers of digits of Long.MAX_VALUE
	private static final int LONG_LENGTH = String.valueOf(Long.MAX_VALUE).length();
	
	/**
	 * to avoid any instantiation
	 */
	private ServerMessageFactory() {
	}
	
	static MembersMessage createMembersMessage() throws JemException{
		MembersMessage message = new MembersMessage();
		List<String> addresses = new ArrayList<String>();
		message.setObject(addresses);
		IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
		try {
			
			// check if node release version inside the cluster are different
			Collection<NodeInfo> allNodes = membersMap.values();
			// scans all node to check the version
			for (NodeInfo currNodeInfo : allNodes) {
				String address = currNodeInfo.getIpaddress() + ":" + currNodeInfo.getTcpPort();
				addresses.add(address);
			}
		} catch (Exception ex) {
			throw new JemException(ex);
		}
		return message;
	}

	/**
	 * Creates the unique id for the job, normalizing generated id joined to started time stamp of job.<br>
	 * it pads (on left) with '0' string representation of both id and timestamp
	 * 
	 * @return job id in job format
	 */
	static JobIdMessage createJobIdMessage(){
		JobIdMessage message = new JobIdMessage();
		// creates a job ID asking to Hazelcast for a new long value
		IdGenerator generator = Main.getHazelcast().getIdGenerator(IdGenerators.JOB);
		long id = generator.newId();
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.leftPad(String.valueOf(id), LONG_LENGTH, "0")).append('-').append(StringUtils.leftPad(String.valueOf(System.currentTimeMillis()), LONG_LENGTH, "0"));
		message.setObject(sb.toString());
		return message;
	}

	/**
	 * FIXME
	 */
	static PrintOutputMessage createPrintOutputMessage(Job job) throws JemException{
		PrintOutputMessage msg = new PrintOutputMessage();
		Cluster cluster = Main.getHazelcast().getCluster();
		Member member = cluster.getLocalMember();
		IExecutorService executorService = Main.getHazelcast().getExecutorService(ExecutorServices.NODE);
		Future<String> task = executorService.submitToMember(new GetMessagesLog(job), member);
		// gets content
		try {
			msg.setObject(task.get());
		} catch (InterruptedException e) {
			throw new JemException(e);
		} catch (ExecutionException e) {
			throw new JemException(e);
		}
		return msg;
	}

	
}
