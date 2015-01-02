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
package org.pepstock.jem.node.resources.impl.rest;

import javax.naming.Reference;

import org.pepstock.jem.rest.RestClient;

/**
 * Sets constants for REST for Datasource object. It uses JERSEY classes.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2	
 *
 */
public class RestReference extends Reference {

	private static final long serialVersionUID = 1L;

	/**
	 * REST JNDI factory
	 */
	public static final String JNDI_FACTORY = RestFactory.class.getName();
	
	/**
	 * Is WebResource (JERSEY) object created when requested
	 */
	public static final String JNDI_OBJECT = RestClient.class.getName();
	
	/**
	 * Creates a JNDI reference for REST purposes
	 */
	public RestReference() {
		super(JNDI_OBJECT, JNDI_FACTORY, null);
	}

}