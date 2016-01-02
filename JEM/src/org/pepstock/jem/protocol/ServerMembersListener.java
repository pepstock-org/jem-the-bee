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

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.protocol.message.MembersMessage;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class ServerMembersListener implements EntryAddedListener<String, NodeInfo>, EntryRemovedListener<String, NodeInfo> {
	
	private final Server server;

	/**
	 * @param server server instance used to get sessions and to execute the writes to sessions
	 */
	ServerMembersListener(Server server) {
		this.server = server;
	}


	/* (non-Javadoc)
	 * @see com.hazelcast.map.listener.EntryRemovedListener#entryRemoved(com.hazelcast.core.EntryEvent)
	 */
	@Override
	public void entryRemoved(EntryEvent<String, NodeInfo> event) {
		sendMembres();
	}


	/* (non-Javadoc)
	 * @see com.hazelcast.map.listener.EntryAddedListener#entryAdded(com.hazelcast.core.EntryEvent)
	 */
	@Override
	public void entryAdded(EntryEvent<String, NodeInfo> event) {
		if (event.getValue().getTcpPort() > 0){
			sendMembres();
		}
	}	
	
	private void sendMembres() {
		MembersMessage message;
		try {
			message = ServerMessageFactory.createMembersMessage();
		} catch (JemException e1) {
			// TODO logs
			e1.printStackTrace();
			return;
		}
		Selector selector = server.getSelector();
		for (SelectionKey key : selector.keys()){
			Session session = (Session)key.attachment();
			if (session != null){
				try {
					// adds new task for session
					session.getPendingTasksCount().incrementAndGet();
					server.getDelegate().execute(new ServerMessageHandler(key, message));
				} catch (JemException e) {
					// TODO LOGS
					e.printStackTrace();
				}
			}
		}
	}

}
