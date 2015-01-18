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
package org.pepstock.jem.node.https;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * Runnable created to manage each HTTPS request, form connection point of view.<br>
 * is a Runnable because it runs inside a pool of threads.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Worker implements Runnable {
	
    private final HttpService httpService;
    private final HttpServerConnection httpConnection;
    private final InetAddress clientAddress;

	/**
	 * Creates the object with HTTP entities
	 * 
	 * @param clientAddress client address to pass to handler by HttpContext
	 * @param httpService HTTP service 
	 * @param httpConnection connection with the client
	 */
	public Worker(
			final InetAddress clientAddress,
            final HttpService httpService,
            final HttpServerConnection httpConnection) {
        super();
        this.clientAddress = clientAddress;
        this.httpService = httpService;
        this.httpConnection = httpConnection;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// creates a custom context
        HttpContext context = new BasicHttpContext(null);
        // adds the IP address of the client
        // necessary when the job ends and client is waiting the end of the job
        context.setAttribute(SubmitHandler.JOB_SUBMIT_IP_ADDRESS_KEY, clientAddress.getHostAddress());
        try {
        	// till connection is open
            while (!Thread.interrupted() && httpConnection.isOpen()) {
            	// starts the HTTP request
            	httpService.handleRequest(httpConnection, context);
            }
        } catch (ConnectionClosedException ex) {
        	// client close the connection
        	LogAppl.getInstance().ignore(ex.getMessage(), ex);
        	LogAppl.getInstance().emit(NodeMessage.JEMC023E, "Client closed connection ("+clientAddress.getHostAddress()+")");
        } catch (IOException ex) {
        	// any I/O error
        	LogAppl.getInstance().ignore(ex.getMessage(), ex);
        	LogAppl.getInstance().emit(NodeMessage.JEMC023E, "I/O error ("+clientAddress.getHostAddress()+")");
        } catch (HttpException ex) {
        	// Protocol exception
        	LogAppl.getInstance().ignore(ex.getMessage(), ex);
        	LogAppl.getInstance().emit(NodeMessage.JEMC023E, "Unrecoverable HTTP protocol violation ("+clientAddress.getHostAddress()+")");
        } finally {
        	// ALWAYS close connection
            try {
                httpConnection.shutdown();
            } catch (IOException e) {
            	LogAppl.getInstance().ignore(e.getMessage(), e);
            }
        }
	}
}