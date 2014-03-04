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
package org.pepstock.jem.node.security;


/**
 * Container of predefined roles for JEM web app.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public final class Roles {
	
	/**
	 * default ROOT user id. 
	 */
	public static final String DEFAULT_GRANTOR_ID = "grantor";
	
	/**
	 * Administrator role tag. Usually an administrator should do everything 
	 */
	public static final String ADMINISTRATOR = "administrator";

	/**
	 * Operator role tag. An operator should do only operations (no administration)
	 */
	public static final String OPERATOR = "operator";
	
	/**
	 * Developer role tag. It could see own jobs and executions. 
	 */
	public static final String DEVELOPER = "developer";
	
	/**
	 * Grantor role tag. It could be able to manage roles and permissions.
	 */
	public static final String GRANTOR = "grantor";
	
	/**
	 * Administrator role object. Usually an administrator should do everything 
	 */
	public static final Role ADMINISTRATOR_ROLE = new Role(ADMINISTRATOR, false);
	
	/**
	 * Operator role object. An operator should do only operations (no administration)
	 */
	public static final Role OPERATOR_ROLE = new Role(OPERATOR,  false);
	
	/**
	 * Developer role object. It could see own jobs and executions. 
	 */
	public static final Role DEVELOPER_ROLE = new Role(DEVELOPER, false);
	
	/**
	 * Grantor role object. It could be able to manage roles and permissions.
	 */
	public static final Role GRANTOR_ROLE = new Role(GRANTOR,  false);

	/**
	 * To avoid any instantiation
	 */
	private Roles() {
	}
	
}