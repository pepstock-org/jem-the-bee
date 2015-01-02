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
package org.pepstock.jem.commands;

import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM submit. <br>
 * It is a list of <code>SubmitMessage</code>. <br> 
 * Each <code>SubmitMessage</code> in the list corresponds to a <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 */
public enum SubmitMessage implements MessageInterface{
		
	/**
	 * "Unable to get the members of JEM cluter", MessageLevel.ERROR
	 */
	JEMW001E(1, "Unable to get the members of JEM cluter", MessageLevel.ERROR),
	
	/**
	 * "Unable to get the name of JEM cluter", MessageLevel.ERROR
	 */
	JEMW002E(2, "Unable to get the name of JEM cluter", MessageLevel.ERROR),
	
	/**
	 * "Unable to submit into JEM", MessageLevel.ERROR
	 */
	JEMW003E(3, "Unable to submit into JEM", MessageLevel.ERROR),
	
	/**
	 * "Unable to get job from output queue", MessageLevel.ERROR
	 */
	JEMW004E(4, "Unable to get job from output queue", MessageLevel.ERROR),
	
	/**
	 * "Unable to create a Hazelcast client", MessageLevel.ERROR
	 */
	JEMW005E(5, "Unable to create a Hazelcast client", MessageLevel.ERROR),
	
	/**
	 * "Unable to read JCL because URL {0} is malformed", MessageLevel.ERROR
	 */
	JEMW006E(6, "Unable to read JCL because URL {0} is malformed", MessageLevel.ERROR),
	
	/**
	 * "Unable to create a JOB from JCL {0}", MessageLevel.ERROR
	 */
	JEMW007E(7, "Unable to create a JOB from JCL {0}", MessageLevel.ERROR),
	
	/**
	 * "Unable to get the necessary password by console", MessageLevel.ERROR
	 */
	JEMW008E(8, "Unable to get the necessary password by console", MessageLevel.ERROR),
	
	/**
	 * "Unable to get output of job {0}", MessageLevel.ERROR
	 */
	JEMW009E(9, "Unable to get output of job {0}", MessageLevel.ERROR);
	
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
	private SubmitMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.SUBMIT.getCode(), messageContent, level);
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