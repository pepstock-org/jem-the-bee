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
package org.pepstock.jem.commands.docker;

import org.pepstock.jem.commands.Parameter;

/**
 * Container of constant parameters to use in command line to create node.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class CreateNodeParameters {
	
	/**
	 * Key for the url for databse 
	 */
	public static final Parameter DB_TYPE = new Parameter("t", "use given type of database for persistence. Could be 'mongodb' or 'mysql'");
	
	/**
	 * Key for the url for databse 
	 */
	public static final Parameter DB_USER = new Parameter("u", "use given user of database for persistence");

	/**
	 * Key for the environment of jem
	 */
	public static final Parameter DB_PASSWORD = new Parameter( "p", "use given password to access to database for persistence");

	/**
	 * To avoid any instantiation
	 */
	private CreateNodeParameters() {
		
	}
	
	/**
	 * Creates a empty argument using the parameter 
	 * @param parameter parameter related to argument
	 * @return new empty argument 
	 */
	public static CreateNodeArgument createArgument(Parameter parameter){
		return createArgument(parameter, false);
	}

	/**
	 * Creates a empty argument using the parameter defining if is mandatory
	 * @param parameter parameter related to argument
	 * @param required <code>true</code> if the parameter is mandatory
	 * @return new empty argument
	 */
	public static CreateNodeArgument createArgument(Parameter parameter, boolean required){
		return new CreateNodeArgument(parameter, required);
	}

}
