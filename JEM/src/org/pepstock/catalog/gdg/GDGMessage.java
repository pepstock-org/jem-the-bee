/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.catalog.gdg;

import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM GDG. <br>
 * It is a list of <code>GDGMessage</code>. <br> 
 * Each <code>GDGMessage</code> in the list corresponds to a <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public enum GDGMessage implements MessageInterface{
	
	/**
	 * "dd={0}, dsn={1} doesn't exist.", MessageLevel.ERROR
	 */
	JEMD001E(1, "dd={0}, dsn={1} doesn't exist.", MessageLevel.ERROR),

	/**
	 * "dd={0}, dsn={1} Offset greater than 0 and not allowed in {2}", MessageLevel.ERROR
	 */
	JEMD002E(2, "dd={0}, dsn={1} Offset greater than 0 and not allowed in {2}", MessageLevel.ERROR),

	/**
	 * "dd={0}, dsn={1} Offset equals or less than 0 and not allowed in NEW.", MessageLevel.ERROR
	 */
	JEMD003E(3, "dd={0}, dsn={1} Offset equals or less than 0 and not allowed in NEW.", MessageLevel.ERROR),

	/**
	 * "dd={0}, dsn={1} exists.", MessageLevel.ERROR
	 */
	JEMD004E(4, "dd={0}, dsn={1} exists.", MessageLevel.ERROR),
	
	/**
	 * "Relative position {0} is too high.", MessageLevel.ERROR
	 */
	JEMD005E(5, "Relative position {0} is too high.", MessageLevel.ERROR),
	
	/**
	 * "File is null.", MessageLevel.ERROR
	 */
	JEMD006E(6, "File is null.", MessageLevel.ERROR),
	
	/**
	 * "{0} already exists.", MessageLevel.ERROR
	 */
	JEMD007E(7, "{0} already exists.", MessageLevel.ERROR),
	
	/**
	 * "{0} unable to create generation 0.", MessageLevel.ERROR
	 */
	JEMD008E(8, "{0} unable to create generation 0.", MessageLevel.ERROR),	
	
	/**
	 * "{0} unable to create directories.", MessageLevel.ERROR
	 */
	JEMD009E(9, "{0} unable to create directories.", MessageLevel.ERROR),	
	
	/**
	 * "{0}.", MessageLevel.ERROR
	 */
	JEMD010E(10, "{0}.", MessageLevel.ERROR),
	
	/**
	 * "{0} is not a directory.", MessageLevel.ERROR
	 */
	JEMD011E(11, "{0} is not a directory.", MessageLevel.ERROR),
	
	/**
	 * "Last Generation is not an Integer: {0}.", MessageLevel.ERROR
	 */
	JEMD012E(12, "Last Generation is not an Integer: {0}.", MessageLevel.ERROR),
	
	/**
	 * "Last Generation is not setted.", MessageLevel.ERROR
	 */
	JEMD013E(13, "Last Generation is not setted.", MessageLevel.ERROR);
	
	/**
	 * The {@link Message} created in the constructor corresponding to an instance of <code>GDGMessage</code>. 
	 * @see Message
	 */
	private Message message;
	
	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code> constructor
	 * and the specific ID: {@link #MESSAGE_ID}.
	 * 
	 * @param code identifier ID
	 * @param msg string do display. Could contain variables, resolved at runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private GDGMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.GDG.getCode(), messageContent, level);
	}
	
	/**
	 * It returns the {@link Message} corresponding to an <code>GDGMessage</code> instance.
	 * @return the {@link Message} corresponding to an <code>GDGMessage</code> instance.
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}

}