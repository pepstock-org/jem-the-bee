/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.springbatch.tasks;

import org.pepstock.jem.log.JemException;

/**
 * Generic exception which can be thrown by a Spring batch tasklet 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class TaskletException extends JemException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public TaskletException() {
	}

	/**
	 * Constructor with error message
	 * @param message error message
	 */
	public TaskletException(String message) {
		super(message);
	}

	/**
	 * Constructor with root cause exception
	 * @param cause root cause exception
	 */
	public TaskletException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor with error message and root cause exception
	 * 
	 * @param message error message
	 * @param cause root cause exception
	 */
	public TaskletException(String message, Throwable cause) {
		super(message, cause);
	}

}
