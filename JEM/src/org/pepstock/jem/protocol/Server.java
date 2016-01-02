package org.pepstock.jem.protocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.pepstock.jem.Job;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeException;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;
import org.pepstock.jem.node.ShutDownInterface;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.hazelcast.Topics;
import org.pepstock.jem.protocol.message.ExceptionMessage;

import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;

public class Server extends Thread implements ShutDownInterface{
	
	private static final int NO_DATA = -1;
	
	private static final BlockingQueue<Runnable> RUNNABLES = new SynchronousQueue<Runnable>();
	
	private final ThreadPoolDelegate delegate = new ThreadPoolDelegate();
	
	private InetSocketAddress socketAddress = null;
	
    private ServerSocketChannel channel;
    
    private Selector selector;
    
    private DefaultFuture<Boolean> started = new DefaultFuture<Boolean>();
        
    /**
     * Create the TCP server
     */
    Server(InetSocketAddress socketAddress) {
    	this.socketAddress = socketAddress;
    }

  	/**
	 * @return the delegate
	 */
	ThreadPoolDelegate getDelegate() {
		return delegate;
	}

	/**
	 * @return the selector
	 */
	Selector getSelector() {
		return selector;
	}

	@Override
    public void run() {
		IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
		String name = membersMap.addEntryListener(new ServerMembersListener(this), true);
		// gets topic object and adds itself as listener
		ITopic<Job> topic = Main.getHazelcast().getTopic(Topics.ENDED_JOB);
		String topicName = topic.addMessageListener(new ServerJobListener(this));
        try {
			channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			channel.bind(socketAddress);
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_ACCEPT);
			listen();
		} catch (ClosedChannelException e) {
			started.setExcetpionAndNotify(new ExecutionException(e));
		} catch (IOException e) {
			started.setExcetpionAndNotify(new ExecutionException(e));
		}
        topic.removeMessageListener(topicName);
        membersMap.removeEntryListener(name);
    }
	
	Future<Boolean> isStarted(){
		return started;
	}
  	
  	private void listen(){

        started.setObjectAndNotify(Boolean.TRUE);
        while (channel.isOpen()) {
            try {
                if (selector.select() != 0) {
                	handleSelector();
                }

//            	if (selector.select(5000) != 0) {
//                	handleSelector();
//                }
//                
//                for (SelectionKey key : selector.keys()){
//                	System.err.println("PRINT: "+key.attachment());
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
  	}
    
    private void handleSelector() throws IOException{
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
        	SelectionKey key = iterator.next();
            try {
				handleKey(key);
			} finally {
				iterator.remove();
			}
        }
    }

    private void handleKey(SelectionKey key) {
    	// checks if is invalid
    	// after a cancel of key, the key will remaining invalid forever
    	if (!key.isValid()){
    		return;
    	}
        if (key.isAcceptable()){
            // a new connection has been obtained. This channel is
            // therefore a socket server.
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            // accept the new connection on the server socket. Since the
            // server socket channel is marked as non blocking
            // this channel will return null if no client is connected.
            try {
				SocketChannel clientSocketChannel = serverSocketChannel.accept();
				if (clientSocketChannel != null) {
				    // set the client connection to be non blocking
				    clientSocketChannel.configureBlocking(false);
				    SelectionKey clientKey = clientSocketChannel.register(selector, SelectionKey.OP_READ, SelectionKey.OP_WRITE);
				    Session session = new Session(clientSocketChannel);
				    // TODO puts logs
				    System.err.println("Created "+session);
				    clientKey.attach(session);
				    session.getOpen().set(true);
				}
			} catch (ClosedChannelException e) {
			    // TODO puts logs
			    e.printStackTrace();
			} catch (IOException e) {
			    // TODO puts logs
				e.printStackTrace();
			}
         } else if (key.isReadable()) {
        	Session session = (Session)key.attachment();
        	// the session must be always available in selectionKey
        	// and the session must be marked as open
        	// if not skip message
        	if (session == null || !session.getOpen().get()){
        		return;
        	}
            // data is available for read
            // buffer for reading
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
            int reads = NO_DATA;
			try {
				reads = session.read(buffer);
				if (reads == NO_DATA){
					throw new ClosedChannelException();
				}
			} catch (IOException e) {
				 // TODO puts logs
				e.printStackTrace();
				// If this channel is closed
				// If another thread closes this channel while the read operation is in progress
				// If another thread interrupts the current thread while the read operation is in progress, 
				// thereby closing the channel and setting the current thread's interrupt status
				if (e instanceof ClosedChannelException ||
					e instanceof AsynchronousCloseException ||
					e instanceof ClosedByInterruptException ||
					reads == NO_DATA){
					closeSession(key);
				}
			    return;
			}
			buffer.position(0);
			System.err.println(Hex.encodeHex(buffer.array()));
			// adds new task for session
			session.getPendingTasksCount().incrementAndGet();
			// message handler on thread pool 
			delegate.execute(new ServerMessageHandler(key, buffer));
        }
    }
    

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.ShutDownInterface#shutdown()
	 */
	@Override
	public void shutdown() throws NodeException, NodeMessageException {
		for (SelectionKey key : selector.keys()){
			Session session = (Session)key.attachment();
			if (session != null){
				closeSession(key, true);
			}
		}
		try {
			channel.close();
			selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	void closeSession(SelectionKey key){
		closeSession(key, false);
	}
	
    void closeSession(SelectionKey key, boolean force){
    	if (key == null){
    		return;
    	}
    	Session session = (Session)key.attachment();
    	if (session == null){
    		key.cancel();
    		return;
    	}
    	session.getOpen().set(false);
    	if (session.getPendingTasksCount().get() == 0 || force){
    		System.err.println("Closed "+session);
    		key.cancel();
    		try {
				session.getChannel().close();
			} catch (IOException e) {
				// TODO puts logs
			}
    	}
    }
    
    void writeException(Session session, Exception exception, Message<?> message){
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		ExceptionMessage msg = new ExceptionMessage();
		msg.setId(message.getId());
		msg.setObject(sw.toString());
		try {
			session.write(msg.serialize());
			System.err.println(msg);
		} catch (IOException e) {
			// TODO logs
			e.printStackTrace();
		} catch (JemException e) {
			// TODO logs
			e.printStackTrace();
		}
    }
    
	/**
	 * Custom Thread pool, with only 1 core thread and maximum set to 20.<br>
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
			super.afterExecute(runnable, exception);
			// if message handler, check if there is any exception
			if (runnable instanceof ServerMessageHandler){
				ServerMessageHandler msgHandler = (ServerMessageHandler)runnable;
				Session session = msgHandler.getSession();
				session.getPendingTasksCount().decrementAndGet();
				if (msgHandler.getException() != null){
					if (session.getOpen().get()){
						Message<?> message =  msgHandler.getMessage();
						writeException(session, msgHandler.getException(), message);
						
						// cloze the session ONLY
						// if the message is ASYNC
						// if not, it sends back the exception
						if (message.getId() == null){
							session.getOpen().set(false);
						}
					}
				} 
				if (!session.getOpen().get()){
					closeSession(msgHandler.getKey());
				}
			} else {
				// TODO logs cambiando messaggio
				LogAppl.getInstance().emit(NodeMessage.JEMC217E, runnable.getClass().getName());
			}
		}
	}

}
