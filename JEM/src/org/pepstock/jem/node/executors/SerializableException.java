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
package org.pepstock.jem.node.executors;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.pepstock.jem.log.JemException;

/**
 * Exception used to serialize the complete stack trace. Tjis is used for Executors because teh exceptions must be serializable
 * in Executors otherwise Hazelcast exception occurs.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class SerializableException extends JemException {

	private static final long serialVersionUID = 1L;

	private final String causePrintStackTrace;
	
	/**
	 * Constructs the exception using the message
	 * @param message message exception
	 * @param causePrintStackTrace stack trace of exception cause
	 */
	public SerializableException(String message, String causePrintStackTrace) {
		super(message);
		this.causePrintStackTrace = causePrintStackTrace;
	}

	/**
	 * @return the causePrintStackTrace
	 */
	public String getCausePrintStackTrace() {
		return causePrintStackTrace;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	@Override
	public void printStackTrace(PrintStream s) {
		if (causePrintStackTrace != null){
			s.print(causePrintStackTrace);
		} else { 
			super.printStackTrace(s);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	@Override
	public void printStackTrace(PrintWriter s) {
		if (causePrintStackTrace != null){
			s.print(causePrintStackTrace);
		} else { 
			super.printStackTrace(s);
		}
	}
}
