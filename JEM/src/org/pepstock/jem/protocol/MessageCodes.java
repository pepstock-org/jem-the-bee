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
 * List of codes of messages used into the communication protocol to identify which
 * kind of message is coming.<br>
 * <pre>
 * Client                                 Server
 *   | SESSION_CREATED                      |
 *   |+------------------------------------>|             
 *   |                      SESSION_CREATED |
 *   |<------------------------------------+|
 *   |                            ENDED_JOB |
 *   |<------------------------------------+|
 *   |                          GET_MEMBERS | 
 *   |<------------------------------------+|
 *   | GET_JOBID                            |
 *   |+------------------------------------>|             
 *   |                            GET_JOBID |
 *   |<------------------------------------+|
 *   | SUBMIT_JOB                           |
 *   |+------------------------------------>|             
 *   |                            ENDED_JOB |
 *   |<------------------------------------+|
 *   | GET_PRINT_OUTPUT                     |
 *   |+------------------------------------>|             
 *   |                     GET_PRINT_OUTPUT |
 *   |<------------------------------------+|
 *
 *   Heartbeat:   
 *   
 *   | HEARTBEAT                            |
 *   |+------------------------------------>|             
 *   |                            HEARTBEAT |
 *   |<------------------------------------+|
 *   
 * </pre>  
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class MessageCodes {
	
	/**
	 * From client to server
	 */
	public static final int SESSION_CREATED = 1;
	
	/**
	 * From server to client
	 */	
	public static final int GET_JOBID = 2;

	/**
	 * From client to server
	 */
	public static final int SUBMIT_JOB = 3;

	/**
	 * From server to client
	 */	
	public static final int ENDED_JOB = 4;

	/**
	 * From server to client
	 */	
	public static final int GET_PRINT_OUTPUT = 5;

	/**
	 * From server to client
	 */	
	public static final int GET_MEMBERS = 100;

	/**
	 * From server to client and viceversa
	 */	
	public static final int HEARTBEAT = 101;

	/**
	 * From server to client
	 */	
	public static final int INVALID_PROTOCOL = 102;

	/**
	 * From server to server
	 */	
	public static final int FORCE_CLOSE_SESSION = 103;

	
	/**
	 * To avoid any instantiation
	 */
	private MessageCodes() {
	}
}
