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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.TimeUtils;

/**
 * This is the manager of TCP connection to the server to JEM node.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class Connector extends Thread implements HandshakeListener {
	
	private static final int NO_DATA = -1;

	private final Client client;
    
    private Selector selector;

    private Session session = null;
    
    private long lastHeartbeat = 0L;
    
    private SSLContext sslcontext = null;
    
 	
    /**
     * Creates TCP connector using the client instance
	 * @param client client instance
	 */
	Connector(Client client){
		this.client = client;
	}

	/**
	 * Closes the connector
	 */
	void close(){
		try {
			// closes the session
			session.close();
			// TODO logs
			System.err.println("Closed "+session);
			// close the selector of NIO
			selector.close();
		} catch (IOException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
		}
	}
	
	/**
	 * Resets the heartbeat time
	 */
	void resetHeartbeat(){
		lastHeartbeat = System.currentTimeMillis();
	}
	
	/**
	 * Sends a heartbeat message to JEM to know if the server is still up and running
	 */
	void sendHeartbeat(){
		// gets the selection key and adds the OP_WRITE interest
		SelectionKey key = session.getSocketChannel().keyFor(selector);
		// if key is null, means that the session is closed
		if (key != null && key.isValid()){
			// adds msg to the queue of session
			session.getMessagesToWrite().add(ObjectFactory.createMessage(MessageCodes.HEARTBEAT));
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}
	
	/**
	 * Sends a message to JEM to communicate with the cluster
	 * @param message message to be sent
	 */
	void send(Message message) {
		// adds msg to the queue of session
		session.getMessagesToWrite().add(message);
		// gets the selection key and adds the OP_WRITE interest
		SelectionKey key = session.getSocketChannel().keyFor(selector);
		key.interestOps(SelectionKey.OP_WRITE);
		// wakes up the selector so it can perform the writes
		selector.wakeup();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			// creates the SSL context for client
			createSSLContext();
			// creates the session
			session = new Session(sslcontext, true);
			// sets teh unique ID of session
			this.session.setId(client.getSessionInfo().getId());
			// opens the selector to receive events about read/write
			selector = Selector.open();
			// connect to JEM
			connect();
		} catch (Exception e) {
			// ignore the exception because is sent to the future
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// sets the exception to the future
			client.getFutureForStartup().setExcetpionAndNotify(new ExecutionException(e));
			close();
			return;
		}
		// if here, is connected
		try {
			// stay in listener for IO events
			listen();
		} catch (IOException e) {
			// ignore the exception 
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// closes the client
			client.close();
		} catch (JemException e) {
			// ignore the exception
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// closes the client
			client.close();
		}
	}
	
	/**
	 * Connects to JEM node, searching for a node available
	 * @throws JemException occurs when no JEM node accept the connection
	 */
	private void connect() throws JemException{
		// scans all nodes provided into client configuration
		for (InetSocketAddress address : client.getClientConfig().getStoredAddresses()){
			try {
				System.err.println(address);
				// opens socket
				SocketChannel channel = SocketChannel.open();
				// sets socket options
				channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				channel.setOption(StandardSocketOptions.SO_RCVBUF, ObjectFactory.MAXIMUM_BUFFER_SIZE);
				channel.setOption(StandardSocketOptions.SO_SNDBUF, ObjectFactory.MAXIMUM_BUFFER_SIZE);
				// connects to server
				channel.connect(address);
				// sets to be async
				channel.configureBlocking(false);
				// registers for READ events
				channel.register(selector, SelectionKey.OP_READ);
				// sets scoket channel to session
				session.setSocketChannel(channel);
				// sets itself as handshake listener
				// to understand when handshake is ended
				session.setListener(this);
				// starts handshake
				session.startHandshake();
		       	lastHeartbeat = System.currentTimeMillis();
				return;
			} catch (ClosedChannelException e) {
				e.printStackTrace();
				// nothing
				LogAppl.getInstance().ignore(e.getMessage(), e);
			} catch (IOException e) {
				e.printStackTrace();
				// nothing
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
		// FIXME with message
		// if here, JEM is not available
		throw new JemException("JEM not available!");
	}
	
	/**
	 * Stay in listen on selector for IO events. It performs also the heartbeat if
	 * session is connected
	 * @throws IOException if any errors occurs
	 * @throws JemException if any errors occurs
	 */
	private void listen() throws IOException, JemException{
		// continues cycle if the selector is open
		while (selector.isOpen()) {
			try {
				// listen for an amount of time if it receives events
				if (selector.select(5000) != 0) {
					// it has received events
					// then it handles
					handleSelector();
				} else if (session.getSessionStatus().equals(SessionStatus.CONNECTED)){
					// if session is connected
					// starts heartbeat
					// gets the elapsed time lasted from last heartbeat
					long elapsedTime = System.currentTimeMillis() - lastHeartbeat;
					// if greater than an amount of seconds
					// the connection with server is gone
					if (elapsedTime > TimeUtils.SECOND * 10){
						throw new ClosedChannelException();
					} else {
						// sends a new heartbeat
						sendHeartbeat();
					}
				}
			} catch (ClosedChannelException e) {
				// if here, the connection is gone
				// it tries to reconnect to another JEM node
				System.err.println("Reconnect if can");
				connect();
			}
		}
	}
    
	/**
	 * Handles all messages coming from JEM node
	 * @throws ClosedChannelException if the server closes the connection
	 * @throws JemException if any other error occurs
	 */
    private void handleSelector() throws ClosedChannelException, JemException {
    	// checks again if the selector is open
    	if (selector.isOpen()){
    		// gets all selected keys of events
    		Set<SelectionKey> selectedKeys = selector.selectedKeys();
    		// scans all keys
    		Iterator<SelectionKey> iterator = selectedKeys.iterator();
    		while (iterator.hasNext()) {
    			SelectionKey key = iterator.next();
    			try {
    		     	// checks if is invalid
    		    	// after a cancel of key, the key will remaining invalid forever
    		    	if (key.isValid()){
        		    	// receives something from server
        		        if (key.isReadable()) {
        		        	// reads message
        		        	read(key);
        		        } else if (key.isWritable()) {
        		        	// writes message to JEM node
        		        	write(key);
        		        }
    		    	}
    			} finally {
    				// ALWAYS the selected key MUST be removed
    				iterator.remove();
    			}
    		}
    	}
    }

    /**
     * Reads the buffer from JEM node
     * @param key selection key related to IO event
	 * @throws ClosedChannelException if the server closes the connection
	 * @throws JemException if any other error occurs
     */
    private void read(SelectionKey key) throws JemException, ClosedChannelException {
    	// data is available for read
    	// buffer for reading
    	ByteBuffer buffer = ByteBuffer.allocate(ObjectFactory.MAXIMUM_BUFFER_SIZE);

    	// if session is still in the hadshake, continue with handshake
    	if (session.getSessionStatus().equals(SessionStatus.HANDSHAKING)){
    		try {
    			// performs handshake
    			session.doHandshake();
    		} catch (IOException e) {
    			throw new JemException(e);
    		}
    		return;
    	}
    	// sets the amount of data read
    	int reads = NO_DATA;
    	try {
    		// reads buffer from session
    		reads = session.read(buffer);
    		// if no data, server is closed
    		if (reads == NO_DATA){
    			throw new ClosedChannelException();
    		}
    	} catch (IOException e) {
    		// closes the session
    		closeSession(key);
    		// if the exception is related to the closure of JEM node
    		// throws the closed exception
    		if (e instanceof ClosedChannelException ||
    				e instanceof AsynchronousCloseException ||
    				e instanceof ClosedByInterruptException ||
    				reads == NO_DATA){
    			throw new ClosedChannelException();
    		}
    		// throws a JEM exception 
    		throw new JemException(e);
    	}
    	// if here, we have the buffer
    	// and now the client is able to manage it
    	try {
    		client.messageReceived(session, buffer);
    	} catch (Exception e) {
    		// for any exception, closes the session 
    		closeSession(key);
    		if (e instanceof JemException){
    			throw (JemException)e;
    		}
    		throw new JemException(e);
    	}
    }
    
    /**
     * Writes the buffer to JEM node
     * @param key selection key related to IO event
	 * @throws ClosedChannelException if the server closes the connection
	 * @throws JemException if any other error occurs
     */
    private void write(SelectionKey key) throws JemException, ClosedChannelException {
    	// inside the session there is the list of message to send
    	// to JEM node
    	while(!session.getMessagesToWrite().isEmpty()){
    		// gets the first element without removing it
    		Message message = session.getMessagesToWrite().element();
    		// serializes into a buffer
    		ByteBuffer buffer = ObjectFactory.serialize(message);
    		int writes = NO_DATA;
    		try {
    			// writes to JEM node
    			writes = session.write(buffer);
    			// removes the element from queue
    			session.getMessagesToWrite().remove();
    		} catch (IOException e) {
    			// if the exception is related to the closure of JEM node
        		// throws the closed exception
    			if (e instanceof ClosedChannelException ||
    					e instanceof AsynchronousCloseException ||
    					e instanceof ClosedByInterruptException ||
    					writes < 1){
    				throw new ClosedChannelException();
    			}
    			// checks if there is a future related to the message
    			if (client.getFutures().containsKey(message.getId())){
    				// gets future
    				DefaultFuture<?> future = client.getFutures().get(message.getId());
    				// and sets the exception
    				future.setExcetpionAndNotify(new ExecutionException(e));
    			}
    			throw new JemException(e);
    		} finally {
    			// moves the interest to ONLY read events
    			key.interestOps(SelectionKey.OP_READ);
    		}
    	}
    }
    
    /**
     * Closes the session
     * @param key selection key related to IO events
     */
    private void closeSession(SelectionKey key){
    	// closes the key
    	key.cancel();
    	try {
    		// closes session
    		session.close();
    	} catch (IOException e) {
    		LogAppl.getInstance().ignore(e.getMessage(), e);
    	}
    }
    
    /**
     * Creates the SSL context with flexible trust manager
     * @throws Exception if any error occurs during SSL context creation
     */
	private void createSSLContext() throws Exception {
		// creates the trust manager
		TrustManager[] trmanagers = { new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// nop
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// nop
			}
		} };
		// creates SSL socket factory
		sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, trmanagers, null);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.protocol.HandshakeListener#handshakeEnded()
	 */
	@Override
	public void handshakeEnded(Session session) {
		// FIXME logs
		System.err.println("Created "+session);
		// if handshake is ended
		// sends the sesssion info to connect the JEM cluster
		SessionInfo info = client.getSessionInfo();
       	send(ObjectFactory.createMessage(info.getFutureId(), MessageCodes.SESSION_CREATED, info, SessionInfo.class));
	}
}