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
package org.pepstock.jem.node.tasks.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Creates the initial context JNDI isnide the main program to execute in JEM. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ContextUtils {

	/**
	 * To avoid any instantiation
	 */
	private ContextUtils() {
	}

	/**
	 * Uses the System properties for JNDI intial context
	 * 
	 * @return JNDI context with all data desxriptions
	 * @throws NamingException if an error occurs
	 */
	public static final synchronized InitialContext getContext() throws NamingException{
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.pepstock.jem.node.tasks.jndi.JemContextFactory");
		return new InitialContext(env);
	}

}