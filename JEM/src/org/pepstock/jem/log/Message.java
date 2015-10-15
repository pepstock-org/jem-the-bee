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
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

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
	
	/**
	 * Length to center the message
	 */
	public static final int ATTENTION_STRING_LEGTH = 40;
	
	/**
	 * Common attention message
	 */
	public static final String ATTENTION_STRING = StringUtils.center("ATTENTION", ATTENTION_STRING_LEGTH, "-");
	
	private MessageFormat format = null;

	private String code = null;

	private String content = null;

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
	public Message(String code, String id, String msg, MessageLevel level) {
		this.code = code;
		this.level = level;
		// creates prefix to message "JEMnnnn"
		this.content = PREFIX + id + code + " " + msg;

		// creates format object
		this.format = new MessageFormat(content);
	}

	/**
	 * Returns the identifier ID
	 * 
	 * @return identifier ID
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Returns the log message, original one, no filled with variables values
	 * 
	 * @return log message
	 */
	public String getContent() {
		return content;
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