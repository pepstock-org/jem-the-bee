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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeException;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.NodeMessageException;
import org.pepstock.jem.node.ShutDownInterface;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.hazelcast.Topics;
import org.pepstock.jem.node.security.keystore.SelfSignedCertificate;

import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
/**
 * This is the manager of TCP connections from clients to submit jobs.
 * <br>
 * IT uses the NIO non blocking to do that.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class Server extends Thread implements ShutDownInterface, HandshakeListener{
	
	private static final int NO_DATA = -1;
    
    private static final String CERTIFICATE_PASSWORD = UUID.randomUUID().toString();

	private static final BlockingQueue<Runnable> RUNNABLES = new SynchronousQueue<Runnable>();
	
	private final ThreadPoolDelegate delegate = new ThreadPoolDelegate();
	
	private InetSocketAddress socketAddress = null;
	
    private ServerSocketChannel channel;
    
    private Selector selector;
    
    private DefaultFuture<Boolean> started = new DefaultFuture<Boolean>();
    
    private SSLContext sslcontext = null;
        
    /**
     * Create the TCP server.
     * @param sockt address to use to bind
     * @throws Exception if any error on SSL context creation
     */
    Server(InetSocketAddress socketAddress) throws Exception {
    	this.socketAddress = socketAddress;
    	createSSLContext();
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// gets from hazelcast
		// references to NODES map and job ended TOPIC
		// adds listeners which will send information about add/removes nodes to the client
		// and notifies the ending of the jobs to the client which submitted it.
		IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
		ITopic<Job> topic = Main.getHazelcast().getTopic(Topics.ENDED_JOB);
		// names used by HZ to identify the listeners
		String name = null;
		String topicName = null;;
        try {
        	// opens the selector to listen IO events
        	selector = Selector.open();
        	// adds the listeners to HZ
        	name = membersMap.addEntryListener(new ServerMembersListener(delegate, selector), true);
        	topicName = topic.addMessageListener(new ServerJobListener(delegate, selector));
			// opens the listener
        	channel = ServerSocketChannel.open();
        	// sets asynch IO
			channel.configureBlocking(false);
			// bind to the socket address
			channel.bind(socketAddress);
			// register the selector to get events of ACCEPT socket (new clients)
			channel.register(selector, SelectionKey.OP_ACCEPT);
			// then it stays in listener
			listen();
		} catch (ClosedChannelException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// sets the exception to the future
			// created at startup 
			started.setExcetpionAndNotify(new ExecutionException(e));
		} catch (IOException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// sets the exception to the future
			// created at startup 
			started.setExcetpionAndNotify(new ExecutionException(e));
		}
        // removes the listeners from HZ
        topic.removeMessageListener(topicName);
        membersMap.removeEntryListener(name);
    }
	
	/**
	 * Returns the future to understand when the server is ready
	 * @return the future to understand when the server is ready
	 */
	Future<Boolean> isStarted(){
		return started;
	}
  	
	/**
	 * Uses the selector to stay in wait for IO events
	 */
  	private void listen(){
  		// sets to the startup future that the server is ready
        started.setObjectAndNotify(Boolean.TRUE);
        // until channel is open
        // it waits for events
        while (channel.isOpen()) {
            try {
            	// it waits here...
            	// every time IO has got more than 1 events
            	// it continues
                if (selector.select() != 0) {
                	// handles events
                	handleSelector();
                }
            } catch (IOException e) {
            	LogAppl.getInstance().emit(ProtocolMessage.JEME003W, e);
            }
        }
  	}
    
  	/**
  	 * Handles all events which have been collected by selector
  	 */
    private void handleSelector() {
    	// gets from selector of the selected key which have got at least 1 event
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        // iterates it
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
        	// gets the selection key related to the event
        	SelectionKey key = iterator.next();
        	// checks if is valid
        	// after a cancel of key, the key will remain invalid forever
        	if (key != null && key.isValid()){
        		// checks the event type
        		// and then it handles
        		if (key.isAcceptable()){
        			accept(key);
        		} else if (key.isReadable()) {
        			read(key);
        		} else if (key.isWritable()){
        			write(key);
        		}
        	}
        	// ALWAYS it must be removed from the selected list
        	// because already handled
        	iterator.remove();
        }
    }

    /**
     * Handles the ACCEPT event received using the key.
     * @param key selection key related to the event
     */
    private void accept(SelectionKey key) {
    	// a new connection has been obtained. This channel is
    	// therefore a socket server.
    	ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    	// accept the new connection on the server socket. Since the
    	// server socket channel is marked as non blocking
    	// this channel will return null if no client is connected.
    	try {
    		SocketChannel clientSocketChannel = serverSocketChannel.accept();
    		if (clientSocketChannel != null) {
    			// creates a new session
    			Session session = new Session(sslcontext, false);
    			// sets the socket channel of client
    			session.setSocketChannel(clientSocketChannel);
    			session.setListener(this);
    			session.startHandshake();
    			// set the client connection to be non blocking
    			clientSocketChannel.configureBlocking(false);
    			// register the socket to get all READ events
    			SelectionKey clientKey = clientSocketChannel.register(selector, SelectionKey.OP_READ);
    			// stores the session instance into selected key
    			clientKey.attach(session);
    		}
    	} catch (ClosedChannelException e) {
    		LogAppl.getInstance().emit(ProtocolMessage.JEME005E, e);
    	} catch (IOException e) {
    		LogAppl.getInstance().emit(ProtocolMessage.JEME005E, e);
    	}
    }
    
    /**
     * Handles the READ event received using the key.
     * @param key selection key related to the event
     */
    private void read(SelectionKey key) {
    	// gets the stored session from key
    	Session session = (Session)key.attachment();
    	// the session must be always available in selectionKey
    	// and the session must be marked as open
    	// if not skip message
    	if (session == null || session.getSessionStatus().equals(SessionStatus.DISCONNECTED)){
    		return;
    	}
    	// data is available for read
    	// buffer for reading
    	ByteBuffer buffer = ByteBuffer.allocate(ObjectFactory.MAXIMUM_BUFFER_SIZE);
    	// checks if still hadnshaking running
        if (session.getSessionStatus().equals(SessionStatus.HANDSHAKING)){
        	try {
        		// does handshake
				session.doHandshake();
			} catch (IOException e) {
				// ignore
    			LogAppl.getInstance().ignore(e.getMessage(),e);
    			closeSession(key);
			}
        	return;
        }
    	// prepares to read
    	int reads = NO_DATA;
    	try {
    		reads = session.read(buffer);
    		// if amount of bytes read is -1, the client socket 
    		// has been closed
    		if (reads == NO_DATA){
    			throw new ClosedChannelException();
    		}
    	} catch (IOException e) {
    		// If this channel is closed
    		// If another thread closes this channel while the read operation is in progress
    		// If another thread interrupts the current thread while the read operation is in progress, 
    		// If no data has been read
    		// thereby closing the session
    		if (e instanceof ClosedChannelException ||
    				e instanceof AsynchronousCloseException ||
    				e instanceof ClosedByInterruptException ||
    				reads == NO_DATA){
    			LogAppl.getInstance().ignore(e.getMessage(),e);
    			closeSession(key);
    		} else {
        		LogAppl.getInstance().emit(ProtocolMessage.JEME006E, e);
    		}
    		return;
    	}

    	try {
    		// creates the message
    		List<Message> messages = ObjectFactory.deserialize(buffer);
    		for (Message message : messages){
    			// if here, it was able to red the stream of bytes
    			// then it creates a worker to execute to parse the content
    			Worker worker = ServerFactory.createWorker(session, message);
    			// if worker is null, the protocol is not valid
    			if (worker != null){
    				// message handler on thread pool 
    				delegate.execute(worker);
    			} else {
    				throw new JemException();
    			}
    		}
		} catch (JemException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// gets the code 
			// to add to the exception 
			// because is invalid if here
			buffer.position(0);
			int code = buffer.getInt();
			LogAppl.getInstance().emit(ProtocolMessage.JEME007E, session.getSocketChannel(), code);
		}
   }
    
    /**
     * Handles the WRITE event created for the key.
     * @param key selection key related to the event
     */
    private void write(SelectionKey key) {
    	// gets the stored session from key
    	Session session = (Session)key.attachment();
    	// the session must be always available in selectionKey
    	// and the session must be marked as open
    	// if not skip message
    	if (session == null || session.getSessionStatus().equals(SessionStatus.DISCONNECTED)){
    		return;
    	}
    	// scans all the messages stored into session
    	// to write to the client
    	while(!session.getMessagesToWrite().isEmpty()){
    		// gets the first element without removing
    		Message message = session.getMessagesToWrite().element();
    		int writes = NO_DATA;
    		try {
    			// if the message is to force closure
    			// it closes
    			if (message.getCode() == MessageCodes.FORCE_CLOSE_SESSION){
    				throw new ClosedChannelException();
    			}
    			// serialize the message into a byte buffer
    			ByteBuffer buffer = ObjectFactory.serialize(message);
    			// writes to the socket
    			writes = session.write(buffer);
    			// removes message from queue
    			session.getMessagesToWrite().remove();
    		} catch (Exception e) {
    			LogAppl.getInstance().emit(ProtocolMessage.JEME008E, e);
        		// If this channel is closed
        		// If another thread closes this channel while the read operation is in progress
        		// If another thread interrupts the current thread while the read operation is in progress, 
        		// If no data has been written
        		// thereby closing the session
    			if (e instanceof ClosedChannelException ||
    					e instanceof AsynchronousCloseException ||
    					e instanceof ClosedByInterruptException ||
    					writes < 1){
    				closeSession(key);
    			}
    			return;
    		} finally {
    			// if key is still valid (therefore not exception)
    			// sets the to key the ONLY interest to READ operations
    			if (key.isValid()){
    				key.interestOps(SelectionKey.OP_READ);
    			}
    		}
    	}
    }
 
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.ShutDownInterface#shutdown()
	 */
	@Override
	public void shutdown() throws NodeException, NodeMessageException {
		// scans all keys
		// if there are keys related to client (with a session)
		// closes all sessions
		for (SelectionKey key : selector.keys()){
			Session session = (Session)key.attachment();
			if (session != null){
				closeSession(key);
			}
		}
		// here closes socket channel and the selector
		try {
			channel.close();
			selector.close();
		} catch (IOException e) {
			LogAppl.getInstance().emit(ProtocolMessage.JEME009E, e);
		}
	}
    
	/**
	 * Closes the session by the selection key
	 * @param key selection key of the socket
	 */
	private void closeSession(SelectionKey key){
    	// checks if is invalid
    	// after a cancel of key, the key will remain invalid forever
    	if (key == null || !key.isValid()){
    		return;
    	}
    	// gets the stored session
    	Session session = (Session)key.attachment();
    	// if not exists
    	// it closes ONLY the key
    	if (session == null){
    		key.cancel();
    		return;
    	}
    	// closes the key
    	key.cancel();
    	try {
    		// closes the socket of client
    		session.close();
        	LogAppl.getInstance().emit(ProtocolMessage.JEME010I, session);
    	} catch (IOException e) {
    		LogAppl.getInstance().ignore(e.getMessage(), e);
    	}
    }
	
    /*
     * If this is a secure server, we now setup the SSLContext we'll
     * use for creating the SSLEngines throughout the lifetime of
     * this process.
     */
    private void createSSLContext() throws Exception {
		ByteArrayInputStream baos = SelfSignedCertificate.getCertificate(CERTIFICATE_PASSWORD);
		KeyStore keystore  = KeyStore.getInstance("jks");
		// loads the keystore
		keystore.load(baos, CERTIFICATE_PASSWORD.toCharArray());
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		
		// initialiazes the key manager
		kmfactory.init(keystore, CERTIFICATE_PASSWORD.toCharArray());
		KeyManager[] keymanagers = kmfactory.getKeyManagers();
		// creates SSL socket factory
		sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(keymanagers, null, null);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.protocol.HandshakeListener#handshakeEnded()
	 */
	@Override
	public void handshakeEnded(Session session) {
		LogAppl.getInstance().emit(ProtocolMessage.JEME004I, session.toString());
	}
    
	/**
	 * Custom Thread pool, with only 1 core thread and maximum set to 20.<br>
	 * It uses from workers to parse and act when a message is arrived
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 3.0
	 */
	class ThreadPoolDelegate extends ThreadPoolExecutor {
		
		private static final int CORE_POOL_SIZE = 1;
		
		private static final int MAX_POOL_SIZE = 20;
		
		private static final int KEEP_ALIVE = 10;

		/**
		 * Creates a thread pool with only 1 core thread and infinite maximum.
		 */
		public ThreadPoolDelegate() {
			super(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, RUNNABLES);
		}
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
		 */
		@Override
		protected void afterExecute(Runnable runnable, Throwable exception) {
			// executes always the super
			super.afterExecute(runnable, exception);
			// if a worker
			if (runnable instanceof Worker){
				// gets worker
				Worker msgHandler = (Worker)runnable;
				// gets session
				Session session = msgHandler.getSession();
				// if there is an error and the client is still on starting phase
				// CLOSES the session adding a special message into queue
				if (!msgHandler.isCorrectlyCompleted() && session.getSessionStatus().equals(SessionStatus.STARTING)){
					Message msg = ObjectFactory.createMessage(MessageCodes.FORCE_CLOSE_SESSION);
					session.getMessagesToWrite().add(msg);
				}
				// checks if there is something to write
				if (!session.getMessagesToWrite().isEmpty()){
					// gets the selection key and adds the OP_WRITE interest
					SelectionKey key = session.getSocketChannel().keyFor(selector);
					key.interestOps(SelectionKey.OP_WRITE);
					// wakes up the selector so it can perform the writes
					selector.wakeup();
				}
			} else {
				LogAppl.getInstance().emit(ProtocolMessage.JEME011E, runnable.getClass().getName());
			}
		}
	}
}
