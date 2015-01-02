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

/**
 * JEM generic exception.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JemException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public JemException() {
	}

	/**
	 * Constructor with error message
	 * @param message error message
	 */
	public JemException(String message) {
		super(message);
	}

	/**
	 * Constructor with root cause exception
	 * @param cause root cause exception
	 */
	public JemException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor with error message and root cause exception
	 * 
	 * @param message error message
	 * @param cause root cause exception
	 */
	public JemException(String message, Throwable cause) {
		super(message, cause);
	}
}