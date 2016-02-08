/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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

import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM TCP server. <br>
 * It is a list of <code>ProtocolMessage</code>. <br> 
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Andrea Stock Stocchero
 * @version 3.0
 */
public enum ProtocolMessage implements MessageInterface{
		
	/**
	 * "TCP server startup on {0}", MessageLevel.INFO
	 */
	JEME001I("0001", "TCP server startup on {0}", MessageLevel.INFO),
	
	/**
	 * "Unable to start TCP server more than once", MessageLevel.ERROR
	 */
	JEME002E("0002", "Unable to start TCP server more than once", MessageLevel.ERROR),
	
	/**
	 * "Unable to process the event.", MessageLevel.WARNING
	 */
	JEME003W("0003", "Unable to process the event.", MessageLevel.WARNING),

	/**
	 * "Client session created {0}", MessageLevel.INFO
	 */
	JEME004I("0004", "Client session created {0}", MessageLevel.INFO),

	/**
	 * "Unable to accept client socket", MessageLevel.ERROR
	 */
	JEME005E("0005", "Unable to accept client socket", MessageLevel.ERROR),
	
	/**
	 * "Unable to read from client socket", MessageLevel.ERROR
	 */
	JEME006E("0006", "Unable to read client socket", MessageLevel.ERROR),
	
	/**
	 * "Invalid protocol (unknown code {1}) used by the client {0}", MessageLevel.ERROR
	 */
	JEME007E("0007", "Invalid protocol (unknown code {1}) used by the client {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to write to client socket", MessageLevel.ERROR
	 */
	JEME008E("0008", "Unable to write to client socket", MessageLevel.ERROR),
	
	/**
	 * "Unable to close the server socket correctly", MessageLevel.ERROR
	 */
	JEME009E("0009", "Unable to close the server socket correctly", MessageLevel.ERROR),
	
	/**
	 * "Client session closed {0}", MessageLevel.INFO
	 */
	JEME010I("0010", "Client session closed {0}", MessageLevel.INFO),

	/**
	 * "Invalid worker class: {0}", MessageLevel.ERROR
	 */
	JEME011E("0011", "Invalid worker class: {0}", MessageLevel.ERROR),

	/**
	 * "Unable to notify the end of the job to the session {0}", MessageLevel.ERROR
	 */
	JEME012E("0012", "Unable to notify the end of the job to the session {0}", MessageLevel.ERROR),

	/**
	 * "Unable to create a message with the members of cluster for all clients", MessageLevel.ERROR
	 */
	JEME013E("0013", "Unable to create a message with the members of cluster for all clients", MessageLevel.ERROR),

	/**
	 * "Unable to send a message with the members of cluster to the client {0}", MessageLevel.WARNING
	 */
	@Deprecated
	JEME014W("0014", "Unable to send a message with the members of cluster to the client {0}", MessageLevel.WARNING),

	/**
	 * "Unable to handle a message read from client socket", MessageLevel.ERROR
	 */
	JEME015E("0015", "Unable to handle a message read from client socket", MessageLevel.ERROR),
	
	/**
	 * "Client session opened {0}", MessageLevel.INFO
	 */
	JEME016I("0016", "Client session opened {0}", MessageLevel.INFO);


	/**
	 * The {@link Message} created in the constructor corresponding to an instance of <code>IoMessage</code>. 
	 * @see Message
	 */
	private Message message;
	
	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code> constructor
	 * and the specific ID: {@link #MESSAGE_ID}.
	 * 
	 * @param code identifier ID
	 * @param msg string to display. Could contain variables, resolved at runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private ProtocolMessage(String code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.TCP_SERVER.getCode(), messageContent, level);
	}
	
	/**
	 * It returns the {@link Message} corresponding to an <code>ProtocolMessage</code> instance.
	 * @return the {@link Message} corresponding to an <code>ProtocolMessage</code> instance.
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}
