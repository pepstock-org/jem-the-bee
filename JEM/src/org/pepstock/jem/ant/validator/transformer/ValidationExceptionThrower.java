/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Andrea "Stock" Stocchero
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
package org.pepstock.jem.ant.validator.transformer;

import javax.xml.transform.TransformerException;

/**
 * The Class ValidationExceptionThrower.
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 */
public class ValidationExceptionThrower {

	/**
	 * To avoid any instantiation
	 */
	private ValidationExceptionThrower() {
	}

	/**
	 * Throw exception.
	 * 
	 * @param message the error message
	 * @param line the error line
	 * @param column the error column
	 * @throws TransformerException
	 * @throws Exception the exception
	 */
	public static void throwValidateException(String message, String line, String column) throws TransformerException {
		throw new TransformerException("jcl validation: " + message + "; Line#: " + line + "; Column#: " + column);
	}

}