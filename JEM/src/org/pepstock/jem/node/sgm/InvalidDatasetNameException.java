/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.sgm;

import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;

/**
 * Exception generated when the data set name, put in the JCL, doesn't match with 
 * any defined rule, on which file system to store the file 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class InvalidDatasetNameException extends MessageException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates the exception using only the variables to put on message
	 * becuase the message is always the same.
	 * 
	 * @see NodeMessage#JEMC251E
	 * @param objects variable to use inside the message
	 */
	public InvalidDatasetNameException(Object... objects) {
		super(NodeMessage.JEMC251E, objects);
	}
}
