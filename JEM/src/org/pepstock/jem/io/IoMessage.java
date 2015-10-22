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
package org.pepstock.jem.io;

import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM Input/Output. <br>
 * It is a list of <code>IoMessage</code>. <br> 
 * Each <code>IoMessage</code> in the list corresponds to a <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Alessandro Zambrini
 * @version 1.0	
 */
public enum IoMessage implements MessageInterface{
		
	/**
	 * "Negative initial size: {0}.", MessageLevel.ERROR
	 */
	JEMI001E("0001", "Negative initial size: {0}.", MessageLevel.ERROR),
	
	/**
	 * "Invalid parameters for write: start offset = {0}, number of bytes to write={1}, data length = {2}.", MessageLevel.ERROR
	 */
	JEMI002E("0002", "Invalid parameters for write: start offset = {0}, number of bytes to write={1}, data length = {2}.", MessageLevel.ERROR),
	
	/**
	 * "Encoding must not be null.", MessageLevel.ERROR
	 */
	@Deprecated
	JEMI003E("0003", "Encoding must not be null.", MessageLevel.ERROR),
	
	/**
	 * "Stream Closed.", MessageLevel.ERROR
	 */
	@Deprecated
	JEMI004E("0004", "Stream Closed.", MessageLevel.ERROR);

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
	private IoMessage(String code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.IO.getCode(), messageContent, level);
	}
	
	/**
	 * It returns the {@link Message} corresponding to an <code>IoMessage</code> instance.
	 * @return the {@link Message} corresponding to an <code>IoMessage</code> instance.
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}
