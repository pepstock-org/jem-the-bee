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

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.protocol.message.EndedJobMessage;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class ServerJobListener implements MessageListener<Job> {
	
	private Server server = null;

	/**
	 * @param server server instance used to get sessions and to execute the writes to sessions
	 */
	ServerJobListener(Server server) {
		super();
		this.server = server;
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
	 */
	@Override
	public void onMessage(Message<Job> event) {
		Job endedJob = event.getMessageObject();
		if (endedJob.getClientSessionId() == null){
			return;
		}
		String sessionId = endedJob.getClientSessionId();
		Selector selector = server.getSelector();
		for (SelectionKey key : selector.keys()){
			Session session = (Session)key.attachment();
			if (session != null){
				if (sessionId.equalsIgnoreCase(session.getId())){
					try {
						checkAndSend(key, session, endedJob);
					} catch (JemException e) {
						// TODO logs
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void checkAndSend(SelectionKey key, Session session, Job endedJob) throws JemException{
		EndedJobMessage message = new EndedJobMessage();
		message.setObject(endedJob);
		
		// check if it was a routed job
		// if nowait is false remove job from ROUTED QUEUE
		if (endedJob.getRoutingInfo().getId() != null) {
			message.setId(endedJob.getRoutingInfo().getId());
			if (!endedJob.isNowait()) {
				IMap<String, Job> routedQueue = Main.getHazelcast().getMap(Queues.ROUTED_QUEUE);
				routedQueue.remove(endedJob.getRoutingInfo().getId());
			}
		} else {
			message.setId(endedJob.getId());
		}
		
		// adds new task for session
		session.getPendingTasksCount().incrementAndGet();
		server.getDelegate().execute(new ServerMessageHandler(key, message));
	}

}
