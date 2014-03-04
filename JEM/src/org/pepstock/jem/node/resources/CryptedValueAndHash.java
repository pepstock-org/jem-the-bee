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
package org.pepstock.jem.node.resources;

import java.io.Serializable;



/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class CryptedValueAndHash implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String cryptedValue = null;
	
	private String hash = null;
	
	/**
	 * @return the cryptedValue
	 */
	public String getCryptedValue() {
		return cryptedValue;
	}



	/**
	 * @param cryptedValue the cryptedValue to set
	 */
	public void setCryptedValue(String cryptedValue) {
		this.cryptedValue = cryptedValue;
	}



	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}


	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CryptedValueAndHash [cryptedValue=" + cryptedValue + ", hash=" + hash + "]";
	}


}