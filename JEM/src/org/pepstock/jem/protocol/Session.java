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
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.pepstock.jem.log.LogAppl;

/**
 * Contains all information about the connection from and to server/client.<br>
 * It takes care also the SSL part, like handshaking and wrap/unwrap of buffer
 * received and sent.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class Session {

	private static final String NOT_AVAILABLE = "n/a";

	private String id = NOT_AVAILABLE;

	private String user = NOT_AVAILABLE;

	private final Queue<Message> messagesToWrite = new ConcurrentLinkedQueue<Message>();

	private final SSLContext context;
	
	private final boolean isClient;
	
	private SSLSession session;

	private SSLEngine engine;

	private SocketChannel socketChannel = null;
	
	/**
	 * Application data decrypted from the data received from the peer. This
	 * buffer must have enough space for a full unwrap operation, so we can't
	 * use the buffer provided by the application, since we have no control over
	 * its size.
	 */
	private ByteBuffer peerAppData;
	/** Network data received from the peer. Encrypted. */
	private ByteBuffer peerNetData;
	/** Network data to be sent to the peer. Encrypted. */
	private ByteBuffer netData;
	/** Used during handshake, for the operations that don't consume any data */
	private ByteBuffer dummy;

	/**
	 * Set to true during the initial handshake. The initial handshake is
	 * special since no application data can flow during it. Subsequent
	 * handshake are dealt with in a somewhat different way.
	 */
	private SessionStatus sessionStatus = SessionStatus.HANDSHAKING;

	private SSLEngineResult.HandshakeStatus handShakeStatus;
	/**
	 * Stores the result from the last operation performed by the SSLEngine
	 */
	private SSLEngineResult.Status sslStatus = null;

	private HandshakeListener listener = null;

	/**
	 * Creates the object preparing the SSL part.
	 * 
	 * @param sslcontext SSL context to use
	 * @param client true if is the session into client otherwise false.
	 * @throws IOException if any error occurs during SSL initialization
	 */
	Session(SSLContext sslcontext, boolean client) throws IOException {
		this.context = sslcontext;
		this.isClient = client;
	}

	/**
	 * @return the socketChannel
	 */
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	/**
	 * @param socketChannel the socketChannel to set
	 */
	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
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
	 * @return the sessionStatus
	 */
	public SessionStatus getSessionStatus() {
		return sessionStatus;
	}

	/**
	 * @param sessionStatus the sessionStatus to set
	 */
	public void setSessionStatus(SessionStatus sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	/**
	 * @return the messagesToWrite
	 */
	Queue<Message> getMessagesToWrite() {
		return messagesToWrite;
	}

	/**
	 * Reads from socket channel the byte array on the stream
	 * 
	 * @param byteBuffer buffer to be fill
	 * @return amount of bytes read
	 * @throws IOException if any error occurs
	 */
	public int read(ByteBuffer byteBuffer) throws IOException {
		// position 0 of buffer
		byteBuffer.position(0);
		// loads buffer
		int bytesRead = internalRead(byteBuffer);
		// saves the amount of data
		int totalBytesRead = bytesRead;
		// if it reads more than 1 bytes
		// tries to complete to read the buffer
		while (bytesRead > 0) {
			// reads and adds to buffer
			bytesRead = internalRead(byteBuffer);
			// increments the counter
			totalBytesRead += bytesRead;
		}
		// here has terminated to read
		// and position of buffer is set to the beginning again
		byteBuffer.position(0);
		// returns the bytes read
		return totalBytesRead;
	}

	/**
	 * Writes a buffer to the socket channel stream
	 * 
	 * @param byteBuffer
	 * @return amount of written bytes
	 * @throws IOException
	 */
	public int write(ByteBuffer byteBuffer) throws IOException {
		// position 0 of buffer
		byteBuffer.position(0);
		// writes the buffer
		int bytesWritten = internalWrite(byteBuffer);
		// saves the amount of data
		int totalBytesWritten = bytesWritten;
		// if it writes more than 0 bytes
		// tries to complete to write the buffer
		while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
			// writes the rest of buffer
			bytesWritten = internalWrite(byteBuffer);
			// increments the counter
			totalBytesWritten += bytesWritten;
		}
		// here has terminated to write
		// and position of buffer is set to the beginning again		
		byteBuffer.position(0);
		// returns the bytes written
		return totalBytesWritten;
	}

	/**
	 * Sets the handshake listener
	 * @param listener the listener to set
	 */
	void setListener(HandshakeListener listener) {
		this.listener = listener;
	}

	/**
	 * Starts the handshake phase
	 * @throws IOException if any error occurs
	 */
	void startHandshake() throws IOException {
		// gets SSL engine
		this.engine = context.createSSLEngine();
		// sets engine attributes
		engine.setUseClientMode(isClient);
		engine.setNeedClientAuth(false);
		// gets session
		session = engine.getSession();
		// initializes the buffer based on session buffer size
		peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
		peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
		netData = ByteBuffer.allocate(session.getPacketBufferSize());
		// Change the position of the buffers so that a
		// call to hasRemaining() returns false. A buffer is considered
		// empty when the position is set to its limit, that is when
		// hasRemaining() returns false.
		peerAppData.position(peerAppData.limit());
		netData.position(netData.limit());
		// creates a dummy buffer
		dummy = ByteBuffer.allocate(0);
		
		// if channel is not set, exception
		if (socketChannel == null) {
			throw new IOException("SocketChannel is null");
		}
		// begin handshake
		engine.beginHandshake();
		// gets status
		handShakeStatus = engine.getHandshakeStatus();
		// sets custom status
		sessionStatus = SessionStatus.HANDSHAKING;
		// DO IT!
		doHandshake();
	}

	/**
	 * Checks if the session is closed
	 * @throws IOException if the session is not connected
	 */
	private void checkChannelStillValid() throws IOException {
		if (sessionStatus.equals(SessionStatus.DISCONNECTED)) {
			throw new ClosedChannelException();
		}
	}

	/**
	 * Reads from channel, wrap/unwrap the data with SSL
	 * @param destination buffer to fill
	 * @return amount of byte read
	 * @throws IOException if any error occurs
	 */
	private int internalRead(ByteBuffer destination) throws IOException {
		// checks if the status is correct
		checkChannelStillValid();
		if (sessionStatus.equals(SessionStatus.HANDSHAKING)) {
			return 0;
		}
		// Check if the stream is closed.
		if (engine.isInboundDone()) {
			// We reached EOF.
			return -1;
		}

		// First check if there is decrypted data waiting in the buffers
		if (!peerAppData.hasRemaining()) {
			// reads and unwrap
			int appBytesProduced = readAndUnwrap();
			if (appBytesProduced == -1 || appBytesProduced == 0) {
				return appBytesProduced;
			}
		}
		// It's not certain that we will have some data decrypted ready to
		// be sent to the application. Anyway, copy as much data as possible
		int limit = Math.min(peerAppData.remaining(), destination.remaining());
		for (int i = 0; i < limit; i++) {
			destination.put(peerAppData.get());
		}
		return limit;
	}

	/**
	 * Reads a buffer from channel 
	 * @return amount of bytes read
	 * @throws IOException if any error occurs
	 */
	private int readAndUnwrap() throws IOException {
		// No decrypted data left on the buffers.
		// Try to read from the socket. There may be some data
		// on the peerNetData buffer, but it might not be sufficient.
		int bytesRead = socketChannel.read(peerNetData);
		if (bytesRead == -1) {
			// We will not receive any more data. Closing the engine
			// is a signal that the end of stream was reached.
			engine.closeInbound();
			// EOF. But do we still have some useful data available?
//			if (peerNetData.position() == 0 || sslStatus == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
//				// Yup. Either the buffer is empty or it's in underflow,
//				// meaning that there is not enough data to reassemble a
//				// TLS packet. So we can return EOF.
				return -1;
//			}
//			// Although we reach EOF, we still have some data left to
//			// be decrypted. We must process it
		}
		// Prepare the application buffer to receive decrypted data
		peerAppData.clear();
		// Prepare the net data for reading.
		peerNetData.flip();
		SSLEngineResult res;
		do {
			res = engine.unwrap(peerNetData, peerAppData);
			// During an handshake renegotiation we might need to perform
			// several unwraps to consume the handshake data.
		} while (res.getStatus() == SSLEngineResult.Status.OK && res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP && res.bytesProduced() == 0);

		// If the initial handshake finish after an unwrap
		if (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
			finishInitialHandshake();
		}
		// If no data was produced, and the status is still ok, try to read once more
		if (peerAppData.position() == 0 && res.getStatus() == SSLEngineResult.Status.OK && peerNetData.hasRemaining()) {
			res = engine.unwrap(peerNetData, peerAppData);
		}

		/*
		 * The status may be: 
		 * OK - Normal operation 
		 * OVERFLOW - Should never happen since the application buffer is sized to hold the maximum packet size. 
		 * UNDERFLOW - Need to read more data from the socket. It's normal. 
		 * CLOSED - The other peer closed the socket. Also normal.
		 */
		sslStatus = res.getStatus();
		handShakeStatus = res.getHandshakeStatus();
		// The handshake status here can be different than NOT_HANDSHAKING
		// if the other peer closed the connection. So only check for it
		// after testing for closure.
		if (sslStatus == SSLEngineResult.Status.CLOSED) {
			sessionStatus = SessionStatus.DISCONNECTED;
			doShutdown();
			return -1;
		}

		// Prepare the buffer to be written again.
		peerNetData.compact();
		// And the app buffer to be read.
		peerAppData.flip();

		if (handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK || handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP || handShakeStatus == SSLEngineResult.HandshakeStatus.FINISHED) {
			doHandshake();
		}
		return peerAppData.remaining();
	}

	/**
	 * Writes the buffer 
	 * @param source buffer to write
	 * @return amount of bytes written
	 * @throws IOException if any error occurs
	 */
	private int internalWrite(ByteBuffer source) throws IOException {
		// checks if the status is correct
		checkChannelStillValid();
		if (sessionStatus.equals(SessionStatus.HANDSHAKING)) {
			// Not ready to write
			return 0;
		}
		// First, check if we still have some data waiting to be sent.
		if (netData.hasRemaining()) {
			// There is. Don't try to send it.
			return 0;
		}
		// There is no data left to be sent. Clear the buffer and get
		// ready to encrypt more data.
		netData.clear();
		SSLEngineResult res = engine.wrap(source, netData);
		// Prepare the buffer for reading
		netData.flip();
		flushData();
		// Return the number of bytes read
		// from the source buffer
		return res.bytesConsumed();
	}

	/**
	 * Shutdown the channel and SSL context
	 * @throws IOException if any error occurs
	 */
	private void doShutdown() throws IOException {
		// Either shutdown was initiated now or we are on the middle
		// of shutting down and this method was called after emptying
		// the out buffer
		// If the engine has nothing else to do, close the socket. If
		// this socket is dead because of an exception, close it
		// immediately
		if (engine.isOutboundDone()) {
			try {
				// If no data was produced by the call to wrap, shutdown is
				// complete
				socketChannel.close();
			} catch (IOException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			return;
		}
		// The engine has more things to send
		netData.clear();
		try {
			SSLEngineResult res = engine.wrap(dummy, netData);
			LogAppl.getInstance().debug(res.toString());
		} catch (SSLException e1) {
			// Problems with the engine. Probably it is dead. So close
			// the socket and forget about it.
			try {
				socketChannel.close();
			} catch (IOException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			return;
		}
		netData.flip();
		flushData();
	}

	/**
	 * Closes the sessions
	 * @throws IOException if any error occurs
	 */
	void close() throws IOException {
		// if already closed, return
		if (sessionStatus.equals(SessionStatus.DISCONNECTED)) {
			return;
		}
		// sets the status
		sessionStatus = SessionStatus.DISCONNECTED;
		// Initiate the shutdown process
		engine.closeOutbound();
		if (netData.hasRemaining()) {
			// If this method is called after an exception, we should
			// close the socket regardless having some data to send.
			// We are waiting to send the data
			return;
		} else {
			doShutdown();
		}
	}

	/**
	 * Handshake is ended
	 */
	private void finishInitialHandshake() {
		// sets that the handshaking is ended
		sessionStatus = SessionStatus.STARTING;
		// if there is a listener, it will inform it
		if (listener != null) {
			listener.handshakeEnded(this);
		}
	}

	/**
	 * Performs the handshake
	 * @throws IOException if any error occurs
	 */
	void doHandshake() throws IOException {
		// do forever
		// returns when there is a specific
		// status of handshake
		while (true) {
			SSLEngineResult res;
			switch (handShakeStatus) {
				case FINISHED:
					// if finished, sets that finished
					if (sessionStatus.equals(SessionStatus.HANDSHAKING)) {
						finishInitialHandshake();
					}
					return;
				case NEED_TASK:
					// handshake needs a task
					doTasks();
					break;
				case NEED_UNWRAP:
					// needs to read and unwrap
					readAndUnwrap();
					return;
				case NEED_WRAP:
					// First make sure that the out buffer is completely empty.
					// Since we
					// cannot call wrap with data left on the buffer
					if (netData.hasRemaining()) {
						return;
					}
					// Prepare to write
					netData.clear();
					res = engine.wrap(dummy, netData);
					handShakeStatus = res.getHandshakeStatus();
					netData.flip();
					// Now send the data and come back here only when
					// the data is all sent
					flushData();
					// All data was sent. Break from the switch but don't
					// exit this method. It will loop again, since there may be
					// more
					// operations that can be done without blocking.
					break;
				case NOT_HANDSHAKING:
					// do nothing
					return;
			}
		}
	}

	/**
	 * Writes the data on the netData buffer to the socket
	 * @throws IOException
	 */
	private void flushData() throws IOException {
		try {
			int written = socketChannel.write(netData);
			LogAppl.getInstance().debug(String.valueOf(written));
		} catch (IOException ioe) {
			// Clear the buffer. If write failed, the socket is dead. Clearing
			// the buffer indicates that no more write should be attempted.
			netData.position(netData.limit());
			throw ioe;
		}
	}

	/**
	 * Performs SSL task
	 */
	private void doTasks() {
		Runnable task;
		while ((task = engine.getDelegatedTask()) != null) {
			task.run();
		}
		handShakeStatus = engine.getHandshakeStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Session [id=" + id + ", user=" + user + ", socketChannel=" + socketChannel + ", sessionStatus=" + sessionStatus.name() + "]";
	}

}