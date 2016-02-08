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
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.protocol.Server.ThreadPoolDelegate;

import com.hazelcast.core.MessageListener;

/**
 * Listen to the end of the job and try to send the message to the client if there is
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class ServerJobListener implements MessageListener<Job> {
	
	private final ThreadPoolDelegate pool;
	
	private final Selector selector;

	/**
	 * Creates the object storing the thread pool and the IO selector
	 * 
	 * @param pool thread pool which executes workers
	 * @param selector listener of IO events
	 */
	ServerJobListener(ThreadPoolDelegate pool, Selector selector) {
		this.pool = pool;
		this.selector = selector;
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
	 */
	@Override
	public void onMessage(com.hazelcast.core.Message<Job> event) {
		// gets teh job from event
		Job endedJob = event.getMessageObject();
		// if job doesn't have any session ID
		// means that hasn't been submitted by NIO client
		// therefore nothing to do
		if (endedJob.getClientSessionId() == null){
			return;
		}
		// gets session ID
		String sessionId = endedJob.getClientSessionId();
		// scans all selection keys registered into selector
		// to get teh session related to the ended job
		for (SelectionKey key : selector.keys()){
			// checks if the session is valid
			if (key.isValid()){
				// gets session
				Session session = (Session)key.attachment();
				// if there is
				if (session != null){
					// checks if the id is the same
					if (sessionId.equalsIgnoreCase(session.getId())){
						try {
							// execute the worker of ended job
							pool.execute(new Worker(session, ServerFactory.createJobEnded(session, endedJob)));

						} catch (JemException e) {
							LogAppl.getInstance().emit(ProtocolMessage.JEME012E, e, session.toString());
						}
					}
				}
			}
		}
	}
	

}
