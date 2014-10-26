/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.util;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM utility. <br>
 * It is a list of <code>UtilMessage</code>. <br> 
 * Each <code>UtilMessage</code> in the list corresponds to a <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Alessandro Zambrini
 * @version 1.0	
 */
public enum UtilMessage implements MessageInterface{

	/**
	 * "Registry Container is not instantiated.", MessageLevel.ERROR
	 */
	JEMB001E(1, "Registry Container is not instantiated.", MessageLevel.ERROR),

	/**
	 * "Instance CLIENT is null! Call 'createInstance' method before to get an instance.", MessageLevel.ERROR
	 */
	@Deprecated
	JEMB002E(2, "Instance CLIENT is null! Call 'createInstance' method before to get an instance.", MessageLevel.ERROR),

	/**
	 * "{0}.", MessageLevel.ERROR
	 */
	JEMB003E(3, "RMI {0} error: {1}", MessageLevel.ERROR),

	/**
	 * "Error building MapVariableDereferencer for: [{0}].", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there are problems building the MapVariableDereferencer with the properties of the Job end of the NodeInfo. It is a system error.<br>An exception is thrown.<br>Check in the log if other errors are displayed, they could be the real cause.")
	JEMB004E(4, "Error building MapVariableDereferencer for: [{0}].", MessageLevel.ERROR),
	

	/**
	 * "Addedd variabile: key [{0}] - value [{1}].", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs building the MapVariableDereferencer with the properties of the Job end of the NodeInfo.<br>In particular it is displayed when a new variable name (a Job or NodeInfo property name, specified in the log: {0}) and value(the respective Job or NodeInfo property value, specified in the log: {1}) is added in the MapVariableDereferencer.<br>No exception is thrown, but this information is logged.")
	JEMB005I(5, "Added variabile: key [{0}] - value [{1}].", MessageLevel.INFO),
	
	/**
	 * "Unable to unbind {0} object", MessageLevel.INFO
	 */
	JEMB006I(6, "Unable to unbind {0} object", MessageLevel.INFO),
	
	/**
	 * "Unable to unbind {0} object", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when someone is trying to access to RMI listener.<br>RMI listener can be used ONLY by job in execution on the same machine.")
	JEMB007E(7, "Access to RMI listener from other machine is not allowed: {0}", MessageLevel.ERROR),

	/**
	 * "Unable to unbind {0} object", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the client receives an execption during the SSL initialization.<br>Please contact your JEM administrators.")
	JEMB008E(8, "Unable to activate SSL protocol for REST client", MessageLevel.ERROR),
	
	/**
	 * "Unable to unbind {0} object", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the URLs list necessary for custom classloader is empty.<br>Please hve a look to JEM configuration.")
	JEMB009E(9, "No URLS have been loaded! ", MessageLevel.ERROR);
	
	/**
	 * The {@link Message} created in the constructor corresponding to an instance of <code>UtilMessage</code>. 
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
	private UtilMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.UTIL.getCode(), messageContent, level);
	}
	
	/**
	 * It returns the {@link Message} corresponding to an <code>UtilMessage</code> instance.
	 * @return the {@link Message} corresponding to an <code>UtilMessage</code> instance.
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}
}