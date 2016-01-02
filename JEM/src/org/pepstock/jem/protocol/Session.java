package org.pepstock.jem.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class Session {
	
	private static final String NOT_AVAILABLE = "n/a";

    private String id = NOT_AVAILABLE;
    
    private String user = NOT_AVAILABLE;

    private SocketChannel channel = null;
    
    private final AtomicBoolean open = new AtomicBoolean(false);
    
    private final AtomicInteger pendingTasksCount = new AtomicInteger(0);

    public Session(SocketChannel socketChannel) {
        this.channel = socketChannel;
    }
    
    /**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the channel
	 */
	public SocketChannel getChannel() {
		return channel;
	}
	
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the open
	 */
	public AtomicBoolean getOpen() {
		return open;
	}


	/**
	 * @return the pendingTasksCount
	 */
	public AtomicInteger getPendingTasksCount() {
		return pendingTasksCount;
	}

	/**
	 * 
	 * @param byteBuffer
	 * @return
	 * @throws IOException
	 */
	public synchronized int read(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.position(0);
        int bytesRead = this.channel.read(byteBuffer);
        int totalBytesRead = bytesRead;
        while(bytesRead > 0){
            bytesRead = this.channel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        byteBuffer.position(0);
        return totalBytesRead;
    }

	/**
	 * 
	 * @param byteBuffer
	 * @return
	 * @throws IOException
	 */
	public synchronized int write(ByteBuffer byteBuffer) throws IOException{
		byteBuffer.position(0);
        int bytesWritten = this.channel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while(bytesWritten > 0 && byteBuffer.hasRemaining()){
            bytesWritten = this.channel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }
        byteBuffer.position(0);
        return totalBytesWritten;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Session [id=" + id + ", user=" + user + ", channel=" + channel + "]";
	}
}
