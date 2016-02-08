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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Abstract class which is executed inside of thread pool to manage messages
 * read from the client socket or to send data to the client.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public abstract class ResponseWorker extends Worker {

	/**
	 * Created the object storing the client session and the message which has
	 * been read.
	 * 
	 * @param session client session
	 * @param message message must be managed
	 */
	protected ResponseWorker(Session session, Message request) {
		super(session, request);
	}
	
	/**
	 * Applies the logic using the message which has been received
	 * 
	 * @param session client session
	 * @throws JemException if any error occurs
	 */
	public abstract void execute(Session session) throws JemException;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	/* (non-Javadoc)
	 * @see org.pepstock.jem.protocol.Worker#execute()
	 */
	@Override
	public void execute() {
		try {
			// real task execution
			execute(getSession());
			// sets ended OK
			setCorrectlyCompleted(true); 
		} catch (JemException e) {
			LogAppl.getInstance().emit(ProtocolMessage.JEME015E, e);
			// sets ended NOT OK
			setCorrectlyCompleted(false); 
			// stores the exception stack trace into a string
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			// sets to the response the exception with the message the
			// complete stack trace
			// store the same message ID of request
			// to the response
			Message message = ObjectFactory.createMessage(getMessage(), new JemException(sw.toString()), JemException.class);
			// adds to be written
			add(message);
		}
	}
}
