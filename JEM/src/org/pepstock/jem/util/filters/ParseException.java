/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco "Cuc" Cuccato
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
package org.pepstock.jem.util.filters;

import org.pepstock.jem.log.JemException;

/**
 * Exception throwed by {@link Filter} and {@link FilterToken} when a parse fails
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 */
public class ParseException extends JemException {

	private static final long serialVersionUID = 8002367713613656195L;

	/**
	 * Empty constructor
	 */
	public ParseException() {
	}

	/**
	 * Message contructor
	 * @param arg0 the message
	 */
	public ParseException(String arg0) {
		super(arg0);
	}

	/**
	 * {@link Throwable} contructor
	 * @param arg0 the root cause
	 */
	public ParseException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Both message and {@link Throwable} contructor
	 * @param arg0 the message
	 * @param arg1 the root cause
	 */
	public ParseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}