/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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

/**
 * All JEM messages contains a char which identify the sub component of JEM.
 * <br>
 * In this enumeration we can have all characters used on messages, from different modules.
 *  
 * @author Marco "Fuzzo" Cuccato
 * @version 1.3
 */
public enum MessageCode {

	/**
	 * ANT module
	 */
	ANT("A"),
	/**
	 * ANT utilities module
	 */
	ANT_UTIL("Z"),
	/**
	 * GDG management module
	 */
	GDG("D"),
	/**
	 * IO utilities module
	 */
	IO("I"),
	/**
	 * JPPF module
	 */
	JPPF("J"),
	/**
	 * GWT and user interface module
	 */
	TCP_SERVER("E"),
	/**
	 * Core JEM module
	 */	
	NODE("C"),
	/**
	 * Notification engine module
	 */	
	NOTIFY("N"),
	/**
	 * Common resources module
	 */
	RESOURCE("R"),
	/**
	 * Spring batch module
	 */
	SPRING_BATCH("S"),
	/**
	 * SUBMIT engines module
	 */
	SUBMIT("W"),
	/**
	 * SWARM module
	 */
	SWARM_NODE("O"),
	/**
	 * GWT and user interface module
	 */
	USER_INTERFACE("G"),
	/**
	 * Common utilities module
	 */
	UTIL("B");
	
	private String code;
	
	/**
	 * Creates the message code using the char as argument
	 * @param code char of sub component which will use thos message codes
	 */
	private MessageCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the associated Id
	 */
	public String getCode() {
		return code;
	}
}
