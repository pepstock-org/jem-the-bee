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

package org.pepstock.jem.node.security.loginprotocol;

/**
 * 
 * @author Simone Businaro
 * @version 1.0
 * 
 *          is the enumeration of the possible type of operation during the
 *          Login communication between the members of a cluster
 */
public enum Operation {

	/**
	 * the name of the operation between client and server during authentication
	 * phase, when a node try to join the cluster and asks for a password
	 */
	GETPASSWORD,
	/**
	 * the name of the operation between client and server during authentication
	 * phase, when a node try to join the cluster and asks to login 
	 */
	LOGIN;
}