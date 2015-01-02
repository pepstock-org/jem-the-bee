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

import java.io.Serializable;

import org.pepstock.jem.Jcl;

/**
 * This interface must be implemented for a specific job control language that
 * you want to implement. It creates jcl object
 * 
 * @see org.pepstock.jem.Jcl
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface JclFactory extends Serializable {

	/**
	 * Called to create a jcl object, by the string representing source code. It
	 * should validate the language of control job and throws an exception when
	 * the syntax of JCL is not correct
	 * 
	 * @see org.pepstock.jem.Jcl#getContent()
	 * @param job job to submit
	 * @param content the string representing source code
	 * @return jcl object
	 * @throws JclFactoryException if the syntax of source code is not correct,
	 *             new exception will throw.
	 */
	Jcl createJcl(String content) throws JclFactoryException;

}