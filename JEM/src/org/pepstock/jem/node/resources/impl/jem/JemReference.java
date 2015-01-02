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
package org.pepstock.jem.node.resources.impl.jem;

import javax.naming.Reference;

import org.pepstock.jem.rest.ResourceRestClient;

/**
 * Sets constants for JNDI for REST client object.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2	
 *
 */
public class JemReference extends Reference {

	private static final long serialVersionUID = 1L;

	/**
	 * Jem factory
	 */
	public static final String JEM_FACTORY = JemFactory.class.getName();
	
	/**
	 * REST client
	 */
	public static final String JEM_OBJECT = ResourceRestClient.class.getName();
	
	/**
	 * Creates a JNDI reference for JEM purposes
	 */
	public JemReference() {
		super(JEM_OBJECT, JEM_FACTORY, null);
	}

}