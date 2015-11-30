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
package org.pepstock.jem.gwt.server.proxy;

import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class ProxyBean {
	
	private String id = null;
	
	private SocketAddress incomingHost = null;
	
	private SocketAddress outcomingHost = null;
	
	private Socket incomingSocket = null;
	
	private Socket outcomingSocket = null;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * @return the incomingHost
	 */
	public SocketAddress getIncomingHost() {
		return incomingHost;
	}

	/**
	 * @param incomingHost the incomingHost to set
	 */
	public void setIncomingHost(SocketAddress incomingHost) {
		this.incomingHost = incomingHost;
	}

	/**
	 * @return the outcomingHost
	 */
	public SocketAddress getOutcomingHost() {
		return outcomingHost;
	}

	/**
	 * @param outcomingHost the outcomingHost to set
	 */
	public void setOutcomingHost(SocketAddress outcomingHost) {
		this.outcomingHost = outcomingHost;
	}

	/**
	 * @return the incomingSocket
	 */
	public Socket getIncomingSocket() {
		return incomingSocket;
	}

	/**
	 * @param incomingSocket the incomingSocket to set
	 */
	public void setIncomingSocket(Socket incomingSocket) {
		this.incomingSocket = incomingSocket;
	}

	/**
	 * @return the outcomingSocket
	 */
	public Socket getOutcomingSocket() {
		return outcomingSocket;
	}

	/**
	 * @param outcomingSocket the outcomingSocket to set
	 */
	public void setOutcomingSocket(Socket outcomingSocket) {
		this.outcomingSocket = outcomingSocket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "ProxyBean [id=" + id + ", incomingHost=" + incomingHost + ", outcomingHost=" + outcomingHost + "]";
    }
	
}
