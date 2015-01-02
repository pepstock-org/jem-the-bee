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
package org.pepstock.jem.gwt.client.security;

import org.pepstock.jem.node.security.Roles;


/**
 * Container of all permissions used inside of web app to check and activate the several features of appliction.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class ClientPermissions {

	private ClientPermissions() {
    }

	/**
	 * Returns <code>true</code> if you are authorized to permission, by the domain.
	 * 
	 * @param domain container of subsets of permission, by category  
	 * @param permission simple permission
	 * @return <code>true</code> if authorized, otherwise <code>false</code>
	 */
	public static boolean isAuthorized(String domain, String permission){
		Boolean authorized = CurrentUser.getInstance().getUser().isAuthorized(permission);
		return authorized.booleanValue();
	}
	
	/**
	 * @return
	 */
	public static boolean isAdministrator(){
		Boolean authorized = CurrentUser.getInstance().getUser().isAuthorized(Roles.ADMINISTRATOR);
		return authorized.booleanValue();
	}
	
}