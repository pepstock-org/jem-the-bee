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
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocketFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.protocol.HttpService;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * Is the HTTPS LISTENER. It uses a pool of threads to start workers which will
 * manage each request.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RequestListener extends Thread {
	
	private static final String THREAD_NAME = StringUtils.substringAfterLast(RequestListener.class.getName(), ".").toLowerCase();

	private final int MAXIMUM_WORKERS = 20;

	private final ExecutorService executorService = Executors.newFixedThreadPool(MAXIMUM_WORKERS);

	private final HttpConnectionFactory<DefaultBHttpServerConnection> connectionFactory;

	private final ServerSocket serverSocket;

	private final HttpService httpService;

	/**
	 * Builds the object creating the socket server to stay in listening mode.
	 * 
	 * @param port HTTPS port to stay in istening
	 * @param httpService HTTP service 
	 * @param socketFactory SSL socket factory
	 * @throws IOException if any errors occurs 
	 * 
	 */
	public RequestListener(final int port, final HttpService httpService, final SSLServerSocketFactory socketFactory) throws IOException {
		super(THREAD_NAME);
		this.connectionFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
		this.serverSocket = socketFactory.createServerSocket(port);
		this.httpService = httpService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		LogAppl.getInstance().emit(NodeMessage.JEMC046I, String.valueOf(serverSocket.getLocalPort()));
		while (!Thread.interrupted()) {
			try {
				// Set up HTTP connection
				Socket socket = serverSocket.accept();
				HttpServerConnection conn = connectionFactory.createConnection(socket);
				// Start worker thread
				Worker w = new Worker(socket.getInetAddress(), httpService, conn);
				executorService.execute(w);
			} catch (InterruptedIOException ex) {
				break;
			} catch (IOException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC004E, e);
				break;
			}
		}
	}
}