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
package org.pepstock.jem.protocol;

/**
 * Enumeration with all status of session.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public enum SessionStatus {
	
	/**
	 * Status if the session is not completed
	 */
	HANDSHAKING(-1),
	/**
	 * Status if the session is not completed
	 */
	STARTING(0),
	
	/**
	 * Status if the session is ready to be used
	 */
	CONNECTED(1),
	
	/**
	 * Status if the session is closed
	 */
	DISCONNECTED(2);
	
	private int code = 0;

	/**
	 * @param code
	 */
	private SessionStatus(int code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

}
