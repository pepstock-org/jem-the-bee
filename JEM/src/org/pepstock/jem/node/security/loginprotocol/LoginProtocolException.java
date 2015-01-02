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
package org.pepstock.jem.node.security.loginprotocol;

import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.log.MessageInterface;

/**
 * Specific Exception for login protocol
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class LoginProtocolException extends MessageException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs the object using the message interface
	 * @param messageInterface message to show
	 */
	public LoginProtocolException(MessageInterface messageInterface) {
		super(messageInterface);
	}

	/**
	 * Constructs the object using the message interface and objects to fill message 
	 * @param messageInterface message to show
	 * @param objects data to fill message
	 */
	public LoginProtocolException(MessageInterface messageInterface, Object... objects) {
		super(messageInterface, objects);
	}

	/**
	 * Constructs the object using the message interface, objects to fill message and root cause  
	 * @param messageInterface message to show
	 * @param cause exception genrated to show
	 * @param objects data to fill message
	 */
	public LoginProtocolException(MessageInterface messageInterface, Throwable cause, Object... objects) {
		super(messageInterface, cause, objects);
	}

}
