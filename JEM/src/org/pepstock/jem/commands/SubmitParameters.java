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
package org.pepstock.jem.commands;

/**
 * Container of constant parameters to use in command line to submit jobs.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class SubmitParameters {
	
	/**
	 * Key for the url for jcl
	 */
	public static final Parameter JCL = new Parameter("jcl", "use given jcl file");

	/**
	 * Key for the type of jcl
	 */
	public static final Parameter TYPE = new Parameter("type","use given jcl type");

	/**
	 * Key for the host of JEM web app
	 */
	public static final Parameter HOST = new Parameter("host", "use given host to extract members cluster and group name");

	/**
	 * Key for the password of the user
	 */
	public static final Parameter PASSWORD = new Parameter("password", "use given password to login into JEM");

	/**
	 * Key for the userid of the user
	 */
	public static final Parameter USER = new Parameter("user", "use given userid to login into JEM");

	/**
	 * Key for the no wait
	 */
	public static final Parameter WAIT = new Parameter("wait", "use given to wait for end of job");
	
	/**
	 * Key for the local environment to connect to
	 */
	public static final Parameter ENV = new Parameter( "env", "use given JEM environment to login");

	/**
	 * Key for the local environment to connect to
	 */
	public static final Parameter PORT = new Parameter("port", "use given the port of the JEM environment to connect to");

	/**
	 * Key for the private key to supply in case of socketInterceptor enable
	 */
	public static final Parameter PRIVATE_KEY = new Parameter("privateKey", "use given the private key path to supply in case of socket interceptor enabled");

	/**
	 * Key for the private key to supply in case of socketInterceptor enable
	 */
	public static final Parameter PRIVATE_KEY_PWD = new Parameter("privateKeyPwd", "the private key password");

	/**
	 * Key for the printOutput
	 */
	public static final Parameter PRINT_OUTPUT = new Parameter("printOutput", "use given to print std output and error of job");

	/**
	 * To avoid any instantiation
	 */
	private SubmitParameters() {
		
	}
	
	/**
	 * Creates a empty argument using the parameter 
	 * @param parameter parameter related to argument
	 * @return new empty argument 
	 */
	public static SubmitArgument createArgument(Parameter parameter){
		return createArgument(parameter, false);
	}

	/**
	 * Creates a empty argument using the parameter defining if is mandatory
	 * @param parameter parameter related to argument
	 * @param required <code>true</code> if the parameter is mandatory
	 * @return new empty argument
	 */
	public static SubmitArgument createArgument(Parameter parameter, boolean required){
		return new SubmitArgument(parameter, required);
	}

}
