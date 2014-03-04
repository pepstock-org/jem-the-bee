/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.jem.node.resources;

import javax.naming.Context;

/**
 * Contains all information necessary to create a <code>JMS</code> source by JNDI.<br>
 * Here are the list of attributes to use to create a <code>JMS</code> source:<br>
 * <br>
 * 
 * <pre>
 * PROVIDER_URL = &quot;{@link Context.PROVIDER_URL}&quot;;
 * INITIAL_CONTEXT_FACTORY = &quot;{@link Context.INITIAL_CONTEXT_FACTORY}&quot;;
 * CONNECTION_FACTORY_NAME = &quot;connectionFactoryName&quot;;
 * USERID = &quot;username&quot;;
 * PASSWORD = &quot;password&quot;;
 * </pre>
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public class JmsResource extends Resource {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant property containing the <code>JMS URL</code> provider 
	 * to create the connection to use <code>JMS</code> objects.
	 */
	public static final String PROVIDER_URL = Context.PROVIDER_URL;
	
	/**
	 * Constant property containing the <code>INITIAL CONTEXT FACTORY</code>   
	 * to create the connection to use <code>JMS</code> objects.
	 */
	public static final String INITIAL_CONTEXT_FACTORY = Context.INITIAL_CONTEXT_FACTORY;
	
	/**
	 * Constant property containing the <code>CONNECTION FACTORY NAME</code>   
	 * to create the connection to use <code>JMS</code> objects.
	 */
	public static final String CONNECTION_FACTORY_NAME = "connectionFactoryName";
	
	/**
	 * <code>String Array</code> containing all the properties of <code>JmsResource</code>.
	 */
	public static final String[] PROPERTIES_ALL = new String[] { 
		USERID, 
		PASSWORD, 
		PROVIDER_URL,
		INITIAL_CONTEXT_FACTORY,
		CONNECTION_FACTORY_NAME
	};
	
	/**
	 * Type for <code>JMS</code> sources
	 */
	public static final String TYPE = "jms";

	/**
	 * Constructs the object adding user name, password, and the specific unchangeable final static
	 * properties.
	 */
	public JmsResource() {
		setType(TYPE);
	}
}