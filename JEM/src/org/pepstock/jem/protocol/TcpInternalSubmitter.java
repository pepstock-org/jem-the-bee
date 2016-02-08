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

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeException;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;
import org.pepstock.jem.node.ShutDownInterface;
import org.pepstock.jem.node.configuration.ConfigurationException;

/**
 * Starts a TPC listener to submit JOB inside job.<br>
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class TcpInternalSubmitter implements ShutDownInterface {
	
	private static TcpInternalSubmitter INSTANCE = new TcpInternalSubmitter();
	
	private Server server  = null;
	
	/**
	 * To avoid any instantiation
	 */
	private TcpInternalSubmitter() {
	}
	
	/**
	 * Returns the private instance beinga singleton
	 * @return a static instance
	 */
	public static TcpInternalSubmitter getInstance(){
		return INSTANCE;
	}
	
	/**
	 * @return the server
	 */
	private Server getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	private void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Starts the TCP listener.
	 * @param socketAddress Socket address to bind in listening
	 * 
	 * @throws ConfigurationException if any errors occurs
	 */
	public static synchronized void start(InetSocketAddress socketAddress) throws ConfigurationException {
		if (INSTANCE.getServer() != null){
			throw new ConfigurationException(ProtocolMessage.JEME002E.toMessage().getFormattedMessage());
		}
		// creates the listener
		try {
			Server server = new Server(socketAddress);

			server.setDaemon(false);
			server.start();

			Future<Boolean> future = server.isStarted(); 

			if (future.get()){
				LogAppl.getInstance().emit(ProtocolMessage.JEME001I, socketAddress.toString());
				INSTANCE.setServer(server);
			}
        } catch (Exception e) {
			throw new ConfigurationException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.ShutDownInterface#shutdown()
	 */
	@Override
	public void shutdown() throws NodeException, NodeMessageException {
		// called when JEM node is shutting down
		if (server != null){
			// if server is available
			// closes it
			server.shutdown();
			LogAppl.getInstance().emit(NodeMessage.JEMC069I, StringUtils.substringAfterLast(TcpInternalSubmitter.class.getName(), "."));
		}
		
	}
}