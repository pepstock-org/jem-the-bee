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
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class MessageCodes {
	
	public static final int SESSION_CREATED = 0;
	
	public static final int SESSION_CLOSE = 1;

	public static final int MEMBERS = 2;

	public static final int EXCEPTION = 3;
	
	public static final int JOBID = 4;
	
	public static final int HEARTBEAT = 5;
	
	public static final int SUBMIT_JOB = 6;
	
	public static final int ENDED_JOB = 7;
	
	public static final int PRINT_OUTPUT = 8;
	/**
	 * To avoid any instantiation
	 */
	private MessageCodes() {
	}
}
