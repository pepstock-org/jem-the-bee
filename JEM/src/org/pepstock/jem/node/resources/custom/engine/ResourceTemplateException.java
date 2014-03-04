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
package org.pepstock.jem.node.resources.custom.engine;

import org.pepstock.jem.node.resources.custom.ResourceDefinitionException;

/**
 * A <code>ResourceTemplateException</code> is thrown to indicate a 
 * problem related to the Custom Resource Templates. <br>
 * In particular it may be thrown in the configuration loading phase.
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 */
public class ResourceTemplateException extends ResourceDefinitionException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor based on the description of the error related to the exception.
	 * 
	 * @param message the description of the error related to this exception.
	 */
	public ResourceTemplateException(String message){
		super(message);
	}
	
	/**
	 * Constructor based on another exception that is the cause of this exception.
	 * 
	 * @param exception the cause of this exception.
	 */
	public ResourceTemplateException(Exception exception){
		super(exception);
	}
	
	/**
	 * Constructor based on the description of the error related and on another exception 
	 * that is the cause of this exception.
	 * 
	 * @param message the description of the error related to this exception.
	 * @param exception the cause of this exception.
	 */
	public ResourceTemplateException(String message, Exception exception){
		super(message, exception);
	}
}
