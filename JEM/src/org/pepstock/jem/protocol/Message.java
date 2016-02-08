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
 * Implementation of a message, used to communicate between the client and the server.<br>
 * The ID of message is used to related the future created into client (which waits for answer) and
 * the message of server. The ID format is the same used for JOB ID (long-long).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class Message {
	/**
	 * Default id to be ignored when is set -1. 
	 * It uses for synch communication 
	 */
	public static final long NO_ID = -1L;
	
	private int length = 0;
	
	private int code = Integer.MIN_VALUE;
	
	private String id = null;
	
	private String value = null;
	

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}


	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
		if (value != null){
			length = value.length();
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [code=" + getCode() + ", id=" + id + ", length=" + length + ", data=" + value + "]";
	}
}
