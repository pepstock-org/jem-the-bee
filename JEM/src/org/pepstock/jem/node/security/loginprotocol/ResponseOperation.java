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

package org.pepstock.jem.node.security.loginprotocol;

/**
 * @author Simone Businaro
 * @version 1.0
 * 
 *          Is the model for Operation inside the request during the
 *          authentication phase.
 * 
 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
 * @see org.pepstock.jem.node.security.loginprotocol.LoginRequest
 * @see org.pepstock.jem.node.security.loginprotocol.Operation
 * 
 */
public class ResponseOperation extends RequestOperation {

	/**
	 * The operation result when login is accepted
	 */
	public static final String LOGIN_ACCEPTED = "accepted";

	/**
	 * The operation result when login is denied
	 */
	public static final String LOGIN_DENIED = "denied";

	private String result;

	/**
	 * 
	 * @return the result of the operation
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
	 */
	public String getResult() {
		return result;
	}

	/**
	 * 
	 * @param result of the operation
	 * @see org.pepstock.jem.node.security.loginprotocol.Operation
	 * @see org.pepstock.jem.node.security.loginprotocol.LoginProtocol
	 */
	public void setResult(String result) {
		this.result = result;
	}

}
