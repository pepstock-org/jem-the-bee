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
package org.pepstock.jem.node.resources;

import java.security.Key;
import java.security.KeyException;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.NodeMessageException;
import org.pepstock.jem.node.security.Crypto;

/**
 * Common resources utility for keys management, 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ResourcesUtil {
	
	private static ResourcesUtil INSTANCE = new ResourcesUtil();
	
	private Key key = null;
	
	/**
	 * Creates a empty object
	 */
	private ResourcesUtil() {
		
	}
	
	/**
	 * @return instance of utility to use everywhere
	 */
	public static ResourcesUtil getInstance(){
		return INSTANCE;
	}

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * Encryts a secret 
	 * 
	 * @param value to encryt
	 * @return encrypted value
	 * @throws KeyException 
	 * @throws Exception 
	 */
	public CryptedValueAndHash encrypt(String value) throws KeyException {
		byte[] whatBytes = CodecSupport.toBytes(value);
		byte[] cryptBytes = Crypto.crypt(whatBytes, key);
		
		CryptedValueAndHash result = new CryptedValueAndHash();
		result.setCryptedValue(Base64.encodeBase64String(cryptBytes));
		
		Sha256Hash hasher = new Sha256Hash(result.getCryptedValue());
		String valueHashed = hasher.toBase64();
		
		result.setHash(valueHashed);
		return result;
	}
	
	/**
	 * Decrypts a secret
	 * @param object oject with encrypted value and ha sh to check is correcct
	 * @return secret in clear
	 * @throws NodeMessageException 
	 * @throws Exception if error occurs
	 */
	public String decrypt(CryptedValueAndHash object) throws NodeMessageException {
		return decrypt(object.getCryptedValue(), object.getHash());
	}
	
	/**
	 * Decrypts a secret
	 * 
	 * @param cryptedValue encrypted value
	 * @param hash hash string to check if secret is correct
	 * @return secret in clear
	 * @throws Base64DecodingException 
	 * @throws KeyException 
	 * @throws NodeMessageException 
	 * @throws Exception if error occurs
	 */
	public String decrypt(String cryptedValue, String hash) throws NodeMessageException {
		try {
			Sha256Hash hasher = new Sha256Hash(cryptedValue);
			String valueHashed = hasher.toBase64();
			if (!valueHashed.equalsIgnoreCase(hash)){
				throw new NodeMessageException(NodeMessage.JEMC118E);
			}
			
			byte[] decryptBytes = Crypto.decrypt(Base64.decodeBase64(cryptedValue), key);
			return CodecSupport.toString(decryptBytes);
		} catch (KeyException e) {
			throw new NodeMessageException(NodeMessage.JEMC118E, e);
		} 
	}
}