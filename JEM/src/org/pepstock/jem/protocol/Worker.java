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


/**
 * Class which is executed inside of thread pool to manage messages
 * read from the client socket or to send data to the client.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class Worker implements Runnable {

	private final Session session;
	
	private final Message message;

	private boolean correctlyCompleted = true;

	/**
	 * Created the object storing the client session and the message which has
	 * been read.
	 * 
	 * @param session client session
	 * @param message message must be managed
	 */
	protected Worker(Session session, Message message) {
		this.message = message;
		this.session = session;
	}

	/**
	 * @param correctlyCompleted the correctlyCompleted to set
	 */
	protected void setCorrectlyCompleted(boolean correctlyCompleted) {
		this.correctlyCompleted = correctlyCompleted;
	}

	/**
	 * @return the session
	 */
	Session getSession() {
		return session;
	}

	/**
	 * @return the request
	 */
	protected Message getMessage() {
		return message;
	}

	/**
	 * @return the correctlyCompleted
	 */
	boolean isCorrectlyCompleted() {
		return correctlyCompleted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {
		// if session is not connected
		// does nothing
		if (!session.getSessionStatus().equals(SessionStatus.DISCONNECTED)) {
			execute();
		}
	}
	
	/**
	 * Sends back the same message 
	 */
	public void execute(){
		add(getMessage());
		// sets ended OK
		correctlyCompleted = true;
	}
	
	/**
	 * Adds a message into session message queue to send (by a write operation)
	 * to the client
	 * 
	 * @param message message instance to be written to the client
	 */
	protected void add(Message message) {
		session.getMessagesToWrite().add(message);
	}

}
