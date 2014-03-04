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
package org.pepstock.jem.node.executors.certificates;

import java.security.KeyException;

import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.ResourcesUtil;

/**
 * Returns object with crypted secret and hash to check.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class GetCryptedValueAndHash extends DefaultExecutor<CryptedValueAndHash> {

	private static final long serialVersionUID = 1L;

	private String secret = null;

	/**
	 * Constructs saving secret to encrypt
	 * @param secret what to encrypt
	 */
	public GetCryptedValueAndHash(String secret) {
		this.secret = secret;
	}

	/**
	 * Encrypts the secret.
	 * 
	 * @return encrypted value and hash
	 * @throws if I/O error occurs 
	 */
	@Override
	public CryptedValueAndHash execute() throws ExecutorException {
		try {
			return ResourcesUtil.getInstance().encrypt(secret);
		} catch (KeyException e) {
			throw new ExecutorException(NodeMessage.JEMC240E, e);
		}
	}
}