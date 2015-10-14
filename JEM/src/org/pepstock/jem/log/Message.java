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
package org.pepstock.jem.log;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * Represents the informations that all JEM components are logging. This is the
 * unit of log, mandatory if you want to log.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Prefix of all messages
	 */
	public static final String PREFIX = "JEM";

	private static final DecimalFormat DF = new DecimalFormat("0000");

	private MessageFormat format = null;

	private int code = 0;

	private String message = null;

	private MessageLevel level = MessageLevel.INFO;

	/**
	 * Constructs a message, by code number, id, message string (could be formatted
	 * at runtime) and level
	 * 
	 * @param code identifier ID
	 * @param id unambiguous identifier of the message type (for example:
	 * <code>NodeMessage</code> type, or <code>NotifyMessage</code> type)
	 * @param msg string do display. Could contain variables, resolved at
	 *            runtime
	 * @param level severity of log message
	 */
	public Message(int code, String id, String msg, MessageLevel level) {
		this.code = code;
		this.level = level;
		// formats the number to 5 digits
		String codeString = DF.format(code);
		// creates prefix to message "JEMnnnnn"
		this.message = PREFIX + id + codeString + " " + msg;

		// creates format object
		this.format = new MessageFormat(message);
	}

	/**
	 * Returns the identifier ID
	 * 
	 * @return identifier ID
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns the log message, original one, no filled with variables values
	 * 
	 * @return log message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the severity of log record
	 * @return a {@link MessageLevel}
	 */
	public MessageLevel getLevel() {
		return level;
	}

	/**
	 * Returns the log string to pass to log engine. This string is filled by
	 * variables values, passed by parameters
	 * 
	 * @param objects runtime variables values
	 * @return formatted message
	 */
	public String getFormattedMessage(Object... objects) {
		return format.format(objects);
	}

}