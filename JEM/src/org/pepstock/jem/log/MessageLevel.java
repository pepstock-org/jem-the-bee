/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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


/**
 * Enumeration to be used as Message/Toast severity level
 * @author Marco "Fuzzo" Cuccato
 * @version 1.2
 * 
 */
public enum MessageLevel implements Serializable {

	/**
	 * Used for normal messages
	 */
	INFO(0), 
	/** 
	 * Used for messages that needs attention
	 */
	WARNING(1),
	/**
	 * Used for error messages
	 */
	ERROR(2);
	
	private int intLevel = -1;
	
	/**
	 * Constructs the message level, using the level as argument
	 * @param intLevel level of message
	 */
	private MessageLevel(int intLevel) {
		this.intLevel = intLevel;
	}
	
	/**
	 * @return the value of message level as int
	 */
	public int getIntLevel() {
		return intLevel;
	}
	
	/**
	 * Returns the message level object from the level, integer format
	 * @param intLevel level of message
	 * @return the MessageLevel starting from int level value. Returns INFO by default.
	 */
	public static MessageLevel fromIntLevel(int intLevel) {
		MessageLevel parsed = null;
		switch (intLevel) {
			case 1:
				parsed = WARNING;
				break;
			case 2:
				parsed = ERROR;
				break;
			default:
				parsed = INFO;
				break;
		}
		return parsed;
	}	
}
