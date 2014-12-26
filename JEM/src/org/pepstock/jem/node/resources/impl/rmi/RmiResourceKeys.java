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
package org.pepstock.jem.node.resources.impl.rmi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains all information necessary to create a RMI client for JEM by JNDI.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class RmiResourceKeys {
	
	/**
	 * RMI registry host name
	 */
	public static final String HOSTNAME = "hostname";
	/**
	 * RMI registry port
	 */
	public static final String PORT = "port";
	/**
	 * Ih teh connection must be SSL or not
	 */
	public static final String SSL = "ssl";
	
	@SuppressWarnings("javadoc")
	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(HOSTNAME, PORT, SSL));
	
	@SuppressWarnings("javadoc")
	public static final List<String> PROPERTIES_MANDATORY = Collections.unmodifiableList(Arrays.asList(HOSTNAME, PORT));

	/**
	 * To avoid any instantiation
	 */
	private RmiResourceKeys() {
	}
}