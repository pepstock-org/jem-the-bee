package org.pepstock.jem.protocol;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.protocol.message.EndedJobMessage;
import org.pepstock.jem.protocol.message.ExceptionMessage;
import org.pepstock.jem.protocol.message.GetPrintOutputMessage;
import org.pepstock.jem.protocol.message.JobIdMessage;
import org.pepstock.jem.protocol.message.MembersMessage;
import org.pepstock.jem.protocol.message.PrintOutputMessage;
import org.pepstock.jem.protocol.message.SubmitJobMessage;

public class Client {

	private final Connector connector;

	private final ClientConfig clientConfig;
   
    private final SessionInfo sessionInfo = new SessionInfo();
    
    private AtomicInteger messageCounter = new AtomicInteger();
    
    private final Map<Integer, DefaultFuture<?>> futures = new ConcurrentHashMap<Integer, DefaultFuture<?>>();
    
    /**
	 * @param clientConfig
     * @throws JemException 
	 */
	Client(ClientConfig clientConfig){
		this.clientConfig = clientConfig;
    	sessionInfo.setGroup(clientConfig.getGroup());
    	sessionInfo.setPassword(clientConfig.getPassword());
    	sessionInfo.setUser(clientConfig.getUser());
    	connector = new Connector(this);
	}
	
	public static Future<Client> open(ClientConfig clientConfig){
		Client client = new Client(clientConfig);
		client.connector.start();
		return client.connector.getStartUpFuture();
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
	
	public Future<String> getJobId(){
		JobIdMessage jmsg = new JobIdMessage();
		jmsg.setId(messageCounter.incrementAndGet());
		jmsg.setObject(String.valueOf(jmsg.getId()));
		DefaultFuture<String> future = new DefaultFuture<String>();
		try {
			connector.write(jmsg);
			futures.put(jmsg.getId(), future);
		} catch (ClosedChannelException e) {
			future.setExcetpionAndNotify(new ExecutionException(e));
		} catch (JemException e) {
			future.setExcetpionAndNotify(new ExecutionException(e));
		}
		return future;
	}

	public Future<Job> submit(PreJob prejob){
		SubmitJobMessage jmsg = new SubmitJobMessage();
		jmsg.setId(messageCounter.incrementAndGet());
		jmsg.setObject(prejob);
		
		Job job = prejob.getJob();
		job.setClientFutureId(jmsg.getId());
		job.setClientSessionId(sessionInfo.getId());
		
		DefaultFuture<Job> future = new DefaultFuture<Job>();
		try {
			connector.write(jmsg);
			futures.put(jmsg.getId(), future);
		} catch (ClosedChannelException e) {
			future.setExcetpionAndNotify(new ExecutionException(e));
		} catch (JemException e) {
			future.setExcetpionAndNotify(new ExecutionException(e));
		}
		return future;
	}

	public Future<String> getOutput(Job job){
		GetPrintOutputMessage jmsg = new GetPrintOutputMessage();
		jmsg.setId(messageCounter.incrementAndGet());
		jmsg.setObject(job);
		
		job.setClientFutureId(jmsg.getId());
		job.setClientSessionId(sessionInfo.getId());
		
		DefaultFuture<String> future = new DefaultFuture<String>();
		try {
			connector.write(jmsg);
			futures.put(jmsg.getId(), future);
		} catch (ClosedChannelException e) {
			future.setExcetpionAndNotify(new ExecutionException(e));
		} catch (JemException e) {
			future.setExcetpionAndNotify(new ExecutionException(e));
		}
		return future;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.protocol.test.IoHandler#messageReceived(org.pepstock.jem.protocol.test.Session, java.nio.ByteBuffer, int)
	 */
	void messageReceived(Session session, ByteBuffer buffer, int length) throws JemException {
		buffer.position(0);
		int code = buffer.getInt();
		switch(code){
			case MessageCodes.MEMBERS:
				MembersMessage msg = new MembersMessage();
				msg.deserialize(buffer);
				clientConfig.clearAndAddAddresses(msg.getObject());
				System.err.println(msg.getObject());
				break;
			case MessageCodes.JOBID:
				JobIdMessage jmsg = new JobIdMessage();
				jmsg.deserialize(buffer);
				
				System.err.println(jmsg);
				
				@SuppressWarnings("unchecked")
				DefaultFuture<String> futureJobId = (DefaultFuture<String>)futures.remove(jmsg.getId());
				if (futureJobId != null){
					futureJobId.setObjectAndNotify(jmsg.getObject());
				} else {
					System.err.println(" Future("+jmsg.getId()+") is missing!");
				}
				break;
			case MessageCodes.ENDED_JOB:
				EndedJobMessage ejmsg = new EndedJobMessage();
				ejmsg.deserialize(buffer);
				@SuppressWarnings("unchecked")
				DefaultFuture<Job> futureEndedJob = (DefaultFuture<Job>)futures.remove(ejmsg.getId());
				if (futureEndedJob != null){
					futureEndedJob.setObjectAndNotify(ejmsg.getObject());
				} else {
					System.err.println(" Future("+ejmsg.getId()+") is missing!");
				}
				break;
			case MessageCodes.PRINT_OUTPUT:
				PrintOutputMessage pomsg = new PrintOutputMessage();
				pomsg.deserialize(buffer);
				@SuppressWarnings("unchecked")
				DefaultFuture<String> futurePrintOuptut = (DefaultFuture<String>)futures.remove(pomsg.getId());
				if (futurePrintOuptut != null){
					futurePrintOuptut.setObjectAndNotify(pomsg.getObject());
				} else {
					System.err.println(" Future("+pomsg.getId()+") is missing!");
				}
				break;

			
			case MessageCodes.EXCEPTION:
				ExceptionMessage emsg = new ExceptionMessage();
				emsg.deserialize(buffer);
				JemException exception = new JemException(emsg.getObject());
				if (emsg.getId() != Message.NO_ID){
					DefaultFuture<?> futureForException = futures.remove(emsg.getId());
					if (futureForException != null){
						futureForException.setExcetpionAndNotify(new ExecutionException(exception));
						return;
					} 
				}
				throw exception;
			default:
				System.err.println("Invalid protocol : "+code);
				break;
		}
	}
 }
