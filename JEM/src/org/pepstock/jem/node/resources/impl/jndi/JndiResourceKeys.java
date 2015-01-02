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
package org.pepstock.jem.node.resources.impl.jndi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.naming.Context;

/**
 * Contains all information necessary to create a JNDI by JNDI.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@SuppressWarnings("javadoc")
public class JndiResourceKeys {
	
	public static final String INITIAL_CONTEXT_FACTORY = Context.INITIAL_CONTEXT_FACTORY;
	
	/**
	 * If the initial context can be use to bind, unbind and rebind objects
	 */
	public static final String READONLY = "readOnly";
	
	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(INITIAL_CONTEXT_FACTORY, READONLY));

	/**
	 * To avoid any instantiation
	 */
	private JndiResourceKeys() {
	}
}