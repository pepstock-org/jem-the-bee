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
package org.pepstock.jem.protocol.worker;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.protocol.Message;
import org.pepstock.jem.protocol.MessageCodes;
import org.pepstock.jem.protocol.ObjectFactory;
import org.pepstock.jem.protocol.ProtocolMessage;
import org.pepstock.jem.protocol.ResponseWorker;
import org.pepstock.jem.protocol.ServerFactory;
import org.pepstock.jem.protocol.Session;
import org.pepstock.jem.protocol.SessionInfo;
import org.pepstock.jem.protocol.SessionStatus;

import com.hazelcast.core.IMap;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class SessionWorker extends ResponseWorker {

	/**
	 * @param session
	 * @param request
	 */
	public SessionWorker(Session session, Message request) {
		super(session, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.protocol.Worker#execute()
	 */
	@Override
	public void execute(Session session) throws JemException {
		if (!session.getSessionStatus().equals(SessionStatus.STARTING)) {
			// TODO fare nuovo messaggio
			throw new JemException("Invalid protocol at the current status " + session.getSessionStatus().name());
		}

		SessionInfo info = ObjectFactory.fromMessage(getMessage(),SessionInfo.class);
		session.setUser(info.getUser());
		session.setId(info.getId());

		// if JEM is configured to have the Socket Interceptor on HC
		// the client MUST provide a SIGNATURE (using own private key) with
		// the user crypted inside
		// FIXME
		// if
		// (Main.getHazelcastConfig().getNetworkConfig().getSocketInterceptorConfig().isEnabled()){
		// }
		// checks if password and env are same,
		// comparing with the HC configuration
		if (!Main.getHazelcastConfig().getGroupConfig().getName().equalsIgnoreCase(info.getGroup()) || !Main.getHazelcastConfig().getGroupConfig().getPassword().equalsIgnoreCase(info.getPassword())) {
			// if not equals, exception
			LogAppl.getInstance().emit(NodeMessage.JEMC288W, session.getSocketChannel());
			throw new MessageException(NodeMessage.JEMC288W, session.getSocketChannel());
		}
		add(ObjectFactory.createMessage(getMessage().getId(), getMessage().getCode()));

		session.setSessionStatus(SessionStatus.CONNECTED);
		LogAppl.getInstance().emit(ProtocolMessage.JEME016I, session.toString());

		// checks if jobs ended in the meantime of client reconnection
		if (!info.getJobIds().isEmpty()) {
			for (String jobId : info.getJobIds()) {
				Message jobMsg = createEndedJob(jobId);
				if (jobMsg != null) {
					add(jobMsg);
				}
			}
		}

		// send list of members
		add(ServerFactory.createMembers());
	}

	private Message createEndedJob(String jobId) throws JemException {
		IMap<String, Job> jobsMap = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);
		if (jobsMap.containsKey(jobId)){
			return ObjectFactory.createMessage(jobId, MessageCodes.ENDED_JOB, jobsMap.get(jobId), Job.class);
		} else {
			return null;
		}
	}

}
