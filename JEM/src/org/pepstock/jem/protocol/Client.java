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

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.shiro.codec.Hex;
import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * JEM client with custom protocol to submit jobs.<br>
 * It's based on Java NIO non-blocking (Asynch) and SSL.<br>
 * All actions, being asynch, returns always a future
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class Client {

	private final Connector connector;

	private final ClientConfig clientConfig;

	private final SessionInfo sessionInfo = new SessionInfo();

	private final DefaultFuture<Client> futureForStartup = new DefaultFuture<Client>();

	private final Map<String, DefaultFuture<?>> futures = new ConcurrentHashMap<String, DefaultFuture<?>>();

	/**
	 * Creates a client instance using the configuration of the client
	 * 
	 * @param clientConfig client configuration
	 */
	Client(ClientConfig clientConfig) {
		// saves configuration
		this.clientConfig = clientConfig;
		// moves the data into session info
		sessionInfo.setGroup(clientConfig.getGroup());
		sessionInfo.setPassword(clientConfig.getPassword());
		sessionInfo.setUser(clientConfig.getUser());
		// creates the TCP connector
		connector = new Connector(this);
	}

	/**
	 * Creates a client instance using the configuration of the client
	 * 
	 * @param clientConfig client configuration
	 * @return future object to understand when the cline tis really connected
	 */
	public static Future<Client> open(ClientConfig clientConfig) {
		// creates client
		Client client = new Client(clientConfig);
		// start the thread of TCP connector
		client.connector.start();
		// return the future
		return client.futureForStartup;
	}

	/**
	 * @return the clientConfig
	 */
	ClientConfig getClientConfig() {
		return clientConfig;
	}

	/**
	 * @return the sessionInfo
	 */
	SessionInfo getSessionInfo() {
		return sessionInfo;
	}

	/**
	 * @return the futureForStartup
	 */
	DefaultFuture<Client> getFutureForStartup() {
		return futureForStartup;
	}

	/**
	 * @return the connector
	 */
	Connector getConnector() {
		return connector;
	}

	/**
	 * @return the futures
	 */
	Map<String, DefaultFuture<?>> getFutures() {
		return futures;
	}

	/**
	 * Asks a new JOBID to the JEM cluster
	 * 
	 * @return return a future to get the data when available
	 */
	public Future<String> getJobId() {
		// creates the protocol message
		Message msg = ObjectFactory.createMessage(MessageCodes.GET_JOBID);
		// sends the message and return the future
		return writeMsgAndAddFuture(msg, String.class);
	}

	/**
	 * Submits a job into JEM
	 * 
	 * @param prejob job with JCL, JOBID and type
	 * @return a future to get the data when available
	 */
	public Future<Job> submit(PreJob prejob) {
		// gets job
		Job job = prejob.getJob();
		// sets the session ID of the client
		job.setClientSessionId(sessionInfo.getId());
		// adds to a list of job ids
		// necessary to check if the job is of this client
		sessionInfo.getJobIds().add(prejob.getId());
		// creates message
		Message msg = ObjectFactory.createMessage(prejob.getId(), MessageCodes.SUBMIT_JOB, prejob, PreJob.class);
		// sends the message and return the future
		return writeMsgAndAddFuture(msg, Job.class);
	}

	/**
	 * Gets the standard output of job execution
	 * 
	 * @param job job to get output
	 * @return a future to get the data when available
	 */
	public Future<String> getOutput(Job job) {
		// sets the session id to the job
		job.setClientSessionId(sessionInfo.getId());
		// creates message
		Message msg = ObjectFactory.createMessage(job.getId(), MessageCodes.GET_PRINT_OUTPUT, job, Job.class);
		// sends the message and return the future
		return writeMsgAndAddFuture(msg, String.class);
	}

	/**
	 * Closes the client
	 */
	public void close() {
		// scans all pending futures
		// setting an exception
		// because closed before ending
		for (DefaultFuture<?> future : futures.values()) {
			// if future is not done, exception
			if (!future.isDone()) {
				future.setExcetpionAndNotify(new ExecutionException(new ClosedChannelException()));
			}
		}
		// close the connector
		connector.close();
	}

	/**
	 * Sends the message to the server, creates the future put into a map and
	 * return the future
	 * 
	 * @param message message to send to JEM
	 * @param clazz type of future
	 * @return future to use to get data
	 */
	private <T> Future<T> writeMsgAndAddFuture(Message message, Class<T> clazz) {
		// creates future
		DefaultFuture<T> future = new DefaultFuture<T>();
		// puts into map
		futures.put(message.getId(), future);
		// sends message
		getConnector().send(message);
		return future;
	}

	/**
	 * Handles the message that the client has received
	 * @param message received message
	 * @param clazz type of the future 
	 * @return the message ID
	 */
	private <T> String handleMessage(Message message, Class<T> clazz) {
		// gets the future from the map, using teh message id
		@SuppressWarnings("unchecked")
		DefaultFuture<T> future = (DefaultFuture<T>) futures.remove(message.getId());
		try {
			// gets the object from message
			T object = ObjectFactory.fromMessage(message, clazz);
			// if future exists
			// sets object and notify the future
			if (future != null) {
				future.setObjectAndNotify(object);
			}
		} catch (JemException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// sets the exception to the future 
			if (future != null) {
				future.setExcetpionAndNotify(new ExecutionException(e));
			}
		}
		// returns the message id
		return message.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.protocol.test.IoHandler#messageReceived(org.pepstock
	 * .jem.protocol.test.Session, java.nio.ByteBuffer, int)
	 */
	void messageReceived(Session session, ByteBuffer buffer) throws JemException {
		// locates the buffer to the first byte
		buffer.position(0);
		// creates the message
		List<Message> messages = ObjectFactory.deserialize(buffer);
		for (Message message : messages){
			System.err.println(message);
			switch (message.getCode()) {
				case MessageCodes.SESSION_CREATED:
					// if here, received the session created message
					try {
						// gets the value from message
						String value = ObjectFactory.fromMessage(message, String.class);
						// debug
						LogAppl.getInstance().debug(value);
						// sets session is connected
						session.setSessionStatus(SessionStatus.CONNECTED);
						// sets the instance of this client to future
						futureForStartup.setObjectAndNotify(this);
					} catch (JemException e) {
						// ignore
						LogAppl.getInstance().ignore(e.getMessage(), e);
						// sets exception to the future
						// is not able to connect
						futureForStartup.setExcetpionAndNotify(new ExecutionException(e));
						// closes the session 
						connector.close();
					}
					break;
				case MessageCodes.HEARTBEAT:
					// resets heartbeat counter
					connector.resetHeartbeat();
					break;
				case MessageCodes.GET_JOBID:
					// handles message getting job id
					LogAppl.getInstance().debug(handleMessage(message, String.class));
					break;
				case MessageCodes.ENDED_JOB:
					// handles message getting the ending of the job
					sessionInfo.getJobIds().remove(handleMessage(message, Job.class));
					break;
				case MessageCodes.GET_PRINT_OUTPUT:
					// handles message getting the output of the job
					LogAppl.getInstance().debug(handleMessage(message, String.class));
					break;
				case MessageCodes.GET_MEMBERS:
					// handles message getting all members of JEM cluster to reconnect
					// in case of connection termination from server
					String value = ObjectFactory.fromMessage(message, String.class);
					clientConfig.clearAndAddAddresses(value);
					break;
				default:
					// invalid protocol
					// FiXME
					System.err.println("Invalid protocol : " + message.getCode());
					System.err.println(buffer);
					System.err.println(Hex.encodeToString(buffer.array()));
					break;
			}
		}
	}
}
