/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.util;

import org.pepstock.jem.log.JemException;


/**
 * A <code>VariablesFactoryException</code> is thrown by the {@link VariablesFactory} class
 * when some error occur inside its methods. <br>
 * @see VariablesFactory#createVariables(org.pepstock.jem.Job)
 * @see VariablesFactory#createVariables(org.pepstock.jem.NodeInfoBean)
 * @see VariablesFactory#createVariables(org.pepstock.jem.Job, org.pepstock.jem.NodeInfoBean)
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 */
public class VariablesFactoryException extends JemException{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor based on the description of the error related to the exception.
	 * 
	 * @param message the description of the error related to this exception.
	 */
	public VariablesFactoryException(String message){
		super(message);
	}
	
	/**
	 * Constructor based on another exception that is the cause of this exception.
	 * 
	 * @param exception the cause of this exception.
	 */
	public VariablesFactoryException(Exception exception){
		super(exception);
	}
	
	/**
	 * Constructor based on the description of the error related and on another exception 
	 * that is the cause of this exception.
	 * 
	 * @param message the description of the error related to this exception.
	 * @param exception the cause of this exception.
	 */
	public VariablesFactoryException(String message, Exception exception){
		super(message, exception);
	}
}