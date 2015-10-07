/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea 'Stock" Stocchero
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
package org.pepstock.jem.grs;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages related to the ANT utilities. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Andrea 'Stock" Stocchero
 * @version 2.3
 */
public enum GrsMessage implements MessageInterface{

	/**
	 * "You can't override GrsRequestLock instance.", Message.INFO
	 */
	@Deprecated
	@Description(explanation = "It occurs when programmatically you call <code>setRequest</code> method.")
	JEMV001I(1, "You can't override GrsRequestLock instance.", MessageLevel.INFO);
	
	/**
	 * The {@link Message} created in the constructor corresponding to instances of ANT utilities. 
	 * @see Message
	 */
	private Message message;
	
	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code> constructor.
	 * 
	 * @param code identifier ID
	 * @param msg string to display. Could contain variables, resolved at runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private GrsMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.GRS.getCode(), messageContent, level);
	}
	
	/**
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}