package org.pepstock.jem.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.protocol.message.SessionCreatedMessage;

public class Connector extends Thread  {
	
	private static final int NO_DATA = -1;

	private final Client client;
    
    private Selector selector;

    private Session session = null;

    private final DefaultFuture<Client> startUpFuture = new DefaultFuture<Client>();
    
    private final ReentrantReadWriteLock lockForConnecting = new ReentrantReadWriteLock();
    	
    /**
	 * @param clientConfig
     * @throws JemException 
	 */
	Connector(Client client){
		this.client = client;
	}

	/**
	 * @return the startUpFuture
	 */
	DefaultFuture<Client> getStartUpFuture() {
		return startUpFuture;
	}
	
	
	void close(){
		try {
			session.getChannel().close();
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void write(Message<?> message) throws JemException, ClosedChannelException {
		System.err.println(System.currentTimeMillis()+" "+message.getCode());
		ByteBuffer buffer = message.serialize();
		int writes = NO_DATA;
		lockForConnecting.readLock().lock();
		try {
			writes = session.write(buffer);
		} catch (IOException e) {
			// TODO logs
			if (e instanceof ClosedChannelException ||
					e instanceof AsynchronousCloseException ||
					e instanceof ClosedByInterruptException ||
					writes < 1){
				throw new ClosedChannelException();
			}
			throw new JemException(e);
		} finally {
			lockForConnecting.readLock().unlock();
		}
	}

	@Override
    public void run() {
		try {
			selector = Selector.open();
			connect();
		} catch (Exception e) {
			// TODO logs
			e.printStackTrace();
			startUpFuture.setExcetpionAndNotify(new ExecutionException(e));
			return;
		}
		try {
			startUpFuture.setObjectAndNotify(client);
			listen();
		} catch (IOException e) {
			// TODO logs
			e.printStackTrace();
		} catch (JemException e) {
			// TODO logs
			e.printStackTrace();
		}
	}
	
	private void reconnect() throws JemException{
		lockForConnecting.writeLock().lock();
		try {
			connect();
		} finally {
			lockForConnecting.writeLock().lock();
		}
	}
	
	private void connect() throws JemException{
		for (InetSocketAddress address : client.getClientConfig().getStoredAddresses()){
			try {
				SocketChannel channel = SocketChannel.open();
				System.err.println("Try "+address);
//				channel.socket().connect(address, 5000);
				channel.socket().connect(address);
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_READ);

				session = new Session(channel);
				session.setId(client.getSessionInfo().getId());
				System.err.println("Created "+session);
				System.err.println(client.getSessionInfo());
		       	SessionCreatedMessage msg = new SessionCreatedMessage();
		       	msg.setObject(client.getSessionInfo());
		       	write(msg);
//		       	ByteBuffer bb = msg.serialize();
//		       	session.write(bb);
				return;
			} catch (ClosedChannelException e) {
				// nothing
			} catch (IOException e) {
				//;
			}
		}
		// TODO logs
		System.err.println("Closed "+session);
		throw new JemException("JEM not available!");
	}
	
	private void listen() throws IOException, JemException{
		while (session.getChannel().isConnected()) {
			try {
				if (selector.select() != 0) {
					handleSelector();
					//	                } else if (isShutdown){
					//	                	selector.close();
					//	                	return;
				}
			} catch (ClosedChannelException e) {
				System.err.println("Reconnect if can");
				reconnect();
			}
		}
	}
    
    private void handleSelector() throws ClosedChannelException, JemException {
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

    private void handleKey(SelectionKey key) throws JemException, ClosedChannelException {
     	// checks if is invalid
    	// after a cancel of key, the key will remaining invalid forever
    	if (!key.isValid()){
    		return;
    	}
    	// receives something from server
        if (key.isReadable()) {
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
				closeSession(key);
				if (e instanceof ClosedChannelException ||
						e instanceof AsynchronousCloseException ||
						e instanceof ClosedByInterruptException ||
						reads == NO_DATA){
					closeSession(key);
					throw new ClosedChannelException();
				}
				throw new JemException(e);
			}
			
			try {
				client.messageReceived(session, buffer, reads);
			} catch (Exception e) {
				closeSession(key);
				if (e instanceof JemException){
					throw (JemException)e;
				}
				throw new JemException(e);
			}
        }
    }
    
    private void closeSession(SelectionKey key){
    	key.cancel();
    	try {
    		session.getChannel().close();
    	} catch (IOException e) {
    		// TODO puts logs
    	}
    }
    

}
