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
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.SubmitPreJob;
import org.pepstock.jem.protocol.message.GetPrintOutputMessage;
import org.pepstock.jem.protocol.message.JobIdMessage;
import org.pepstock.jem.protocol.message.MembersMessage;
import org.pepstock.jem.protocol.message.PrintOutputMessage;
import org.pepstock.jem.protocol.message.SessionCreatedMessage;
import org.pepstock.jem.protocol.message.SubmitJobMessage;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class ServerMessageHandler implements Runnable {
	
	private final SelectionKey key;
	
	private final ByteBuffer buffer;
	
	private final Session session;
	
	private Message<?> message = null;
	
	private Exception exception = null;

	/**
	 * @param key
	 * @param buffer
	 * @param length
	 */
	public ServerMessageHandler(SelectionKey key, ByteBuffer buffer) {
		this(key, buffer, null);
	}

	public ServerMessageHandler(SelectionKey key, ByteBuffer buffer, Message<?> message) {
		this.key = key;
		this.buffer = buffer;
		this.session = (Session)key.attachment();
		this.message = message;
	}

	/**
	 * @return the key
	 */
	public SelectionKey getKey() {
		return key;
	}

	/**
	 * @return the buffer
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @return the message
	 */
	public Message<?> getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (session.getOpen().get()){
			try {
				execute();
			} catch (Exception e) {
				// TODO logs
				e.printStackTrace();
				this.exception = e;
			}
		}
	}
	
	private void execute() throws JemException{
		// TODO logs
		System.err.println("Message from "+buffer);
		buffer.position(0);
		int code = buffer.getInt();
		switch(code){
			case MessageCodes.SESSION_CREATED:
				sessionCreated();
				break;
			case MessageCodes.SUBMIT_JOB:
				submitJob();
				break;
			case MessageCodes.JOBID:
				jobId();
				break;
			case MessageCodes.ENDED_JOB:
				writeBuffer(buffer);
				break;
			case MessageCodes.MEMBERS:
				writeBuffer(buffer);
				break;
				
			case MessageCodes.PRINT_OUTPUT:
				printOutput();
				break;
				
			default:
				// TODO fare nuovo messaggio
				throw new JemException("Invalid protocol : "+code);
//				break;
		}
	}
	
	private void sessionCreated() throws JemException {
		SessionCreatedMessage msg = new SessionCreatedMessage();
		message = msg;
		msg.deserialize(buffer);
		session.setUser(msg.getObject().getUser());
		session.setId(msg.getObject().getId());
		
		System.err.println(session);
		// TODO logs
		// writes Members
		MembersMessage mMsg = ServerMessageFactory.createMembersMessage();
		writeBuffer(mMsg.serialize());
	}
	
	private void submitJob() throws JemException{
		SubmitJobMessage msg = new SubmitJobMessage();
		message = msg;
		msg.deserialize(buffer);
		PreJob preJob = msg.getObject();
		SubmitPreJob.submit(Main.getHazelcast(), preJob);
	}
	
	private void jobId() throws JemException{
		JobIdMessage jobIdMsg = new JobIdMessage();
		message = jobIdMsg;
		jobIdMsg.deserialize(buffer);
		JobIdMessage newJobIdMsg = ServerMessageFactory.createJobIdMessage(); 
		newJobIdMsg.setId(jobIdMsg.getId());
		writeBuffer(newJobIdMsg.serialize());
	}
	
	private void printOutput() throws JemException{
		GetPrintOutputMessage msg = new GetPrintOutputMessage();
		message = msg;
		msg.deserialize(buffer);

		PrintOutputMessage newMsg = ServerMessageFactory.createPrintOutputMessage(msg.getObject());
		newMsg.setId(msg.getId());
		writeBuffer(newMsg.serialize());
	}
	
	
	private void writeBuffer(ByteBuffer newBuffer) throws JemException{
		try {
			newBuffer.position(0);
			session.write(newBuffer);
		} catch (UnknownHostException e) {
			throw new JemException(e);
		} catch (IOException e) {
			throw new JemException(e);
		}
	}
}
