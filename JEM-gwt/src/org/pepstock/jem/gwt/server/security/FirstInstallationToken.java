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
package org.pepstock.jem.gwt.server.security;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * This token is used ONLY during the first installation phase.<br>
 * It contains userid and password of first installation user.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class FirstInstallationToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

	/**
	 * Creates a object for first installation using a default grantor id
	 * 
	 * @param userid user id
	 * @param password password of user
	 */
	public FirstInstallationToken(String userid, String password) {
		super(userid, password);
	}
	
}