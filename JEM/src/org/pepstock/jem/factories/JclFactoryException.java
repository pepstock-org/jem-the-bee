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
package org.pepstock.jem.factories;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.log.JemException;

/**
 * Special exception to wrap a JCL, already parsed.
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class JclFactoryException extends JemException {
	
	private static final long serialVersionUID = 1L;
	
	private final Jcl jcl;

	/**
	 * Empty constructor
	 * @param jcl jcl instance
	 */
	public JclFactoryException(Jcl jcl) {
		this.jcl = jcl;
	}

	/**
	 * Constructor with error message
	 * @param message error message
	 */
	public JclFactoryException(String message) {
		super(message);
		this.jcl = null;
	}

	/**
	 * Constructor with root cause exception
	 * @param jcl jcl instance
	 * @param cause root cause exception
	 */
	public JclFactoryException(Jcl jcl, Throwable cause) {
		super(cause);
		this.jcl = jcl;
	}

	/**
	 * Constructor with error message and root cause exception
	 * @param message error message
	 * @param cause root cause exception
	 */
	public JclFactoryException(String message, Throwable cause) {
		super(message, cause);
		this.jcl = null;
	}

	/**
	 * @return the jcl
	 */
	public Jcl getJcl() {
		return jcl;
	}
}