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
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.protocol.Server.ThreadPoolDelegate;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;

/**
 * Listen to the add and remove of a node and try to send the message to the
 * client if there is. <br>
 * The client, when the connection is lost, try to connect to another member of
 * cluster and it needs the right list of nodes, therefore the server sends this
 * list to the client when the situation changes
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class ServerMembersListener implements EntryAddedListener<String, NodeInfo>, EntryRemovedListener<String, NodeInfo> {

	private final ThreadPoolDelegate pool;

	private final Selector selector;

	/**
	 * Creates the object storing the thread pool and the IO selector
	 * 
	 * @param pool thread pool which executes workers
	 * @param selector listener of IO events
	 */
	ServerMembersListener(ThreadPoolDelegate pool, Selector selector) {
		this.pool = pool;
		this.selector = selector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hazelcast.map.listener.EntryRemovedListener#entryRemoved(com.hazelcast
	 * .core.EntryEvent)
	 */
	@Override
	public void entryRemoved(EntryEvent<String, NodeInfo> event) {
		sendMembres();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hazelcast.map.listener.EntryAddedListener#entryAdded(com.hazelcast
	 * .core.EntryEvent)
	 */
	@Override
	public void entryAdded(EntryEvent<String, NodeInfo> event) {
		if (event.getValue().getTcpPort() > 0) {
			sendMembres();
		}
	}

	/**
	 * Sends the messages with all members to all clients 
	 */
	private void sendMembres() {
		// creates message
		Message message;
		try {
			message = ServerFactory.createMembers();
		} catch (JemException e) {
			LogAppl.getInstance().emit(ProtocolMessage.JEME013E, e);
			return;
		}
		// scans all keys to get
		// the client session and send the new list
		for (SelectionKey key : selector.keys()) {
			// checks if session is valid
			if (key.isValid()) {
				// gets session
				Session session = (Session) key.attachment();
				// if session exists
				if (session != null) {
					// send new list of members
					pool.execute(new Worker(session, message));
				}
			}
		}
	}

}
