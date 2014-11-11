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
package org.pepstock.jem.node.resources.impl.jms;

import javax.naming.Context;

import org.pepstock.jem.node.resources.impl.CommonKeys;

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
public final class JmsResourceKeys {

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
	 * <code>String Array</code> containing all the properties of <code>JmsResource</code>.
	 */
	public static final String[] PROPERTIES_ALL = new String[] { 
		CommonKeys.USERID, 
		CommonKeys.PASSWORD, 
		PROVIDER_URL,
		INITIAL_CONTEXT_FACTORY,
	};

	/**
	 * To avoid any instantiation
	 */
	private JmsResourceKeys() {
	}
}