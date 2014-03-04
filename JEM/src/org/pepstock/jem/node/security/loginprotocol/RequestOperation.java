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

package org.pepstock.jem.node.security.loginprotocol;

/**
 * @author Simone Businaro
 * 
 * @version 1.0
 * 
 *          Is the model for Operation inside the request during the
 *          authentication phase.
 * 
 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
 * @see org.pepstock.jem.node.security.loginprotocol.LoginRequest
 * @see org.pepstock.jem.node.security.loginprotocol.Operation
 * 
 * 
 */
public class RequestOperation {

	private String name;

	/**
	 * 
	 * @return the name of the operation
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name of the operation
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 */
	public void setName(String name) {
		this.name = name;
	}

}
