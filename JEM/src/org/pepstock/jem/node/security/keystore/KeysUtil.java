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
package org.pepstock.jem.node.security.keystore;

import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.NodeMessage;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class KeysUtil {
	
	/**
	 * To avoid any instantiation
	 */
	private KeysUtil() {
		
	}

	/**
	 * 
	 * @param keystoreInfo
	 * @return return key
	 * @throws KeyException if any error occurs 
	 */
	public static Key getSymmetricKey() throws KeyException {
		return getSymmetricKey(KeyStoreUtil.getKeyStoresInfo().getClusterKeystoreInfo());
	}

	/**
	 * 
	 * @param keystoreInfo a bean containing keystore info
	 * 
	 * @return the symmetric key used in jem to make encrypt/decrypt operation
	 *         during the login phase of Jem Nodes (either web or not). The
	 *         method will try to load the symmetric key from key store
	 * @throws KeyException if any exception occurs during the extraction of the
	 *             key
	 */
	public static Key getSymmetricKey(KeyStoreInfo keystoreInfo) throws KeyException {
		try {
			KeyStore keystore = KeyStoreUtil.getKeystore(keystoreInfo);
			return keystore.getKey(keystoreInfo.getSymmetricKeyAlias(), keystoreInfo.getSymmetricKeyPwd().toCharArray());
		} catch (UnrecoverableKeyException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyException(e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new KeyException(e.getMessage(), e);
		}
	}

	/**
	 * The method will return the public key of the X.509 certificate inside the
	 * jem key store and relative to the alias passed as parameter. Before to
	 * extract the public key from the certificate, the method will check if the
	 * certificate is expired and if is an X.509 certificate.

	 * @param keystoreInfo 
	 * @param certAlias the alias of the X.509 certificate
	 * @return the public key relative to the certificate
	 * @throws MessageException if any error occurs
	 * @throws KeyException if any error occurs 
	 */
	public static PublicKey getPublicKeyByAlias(KeyStoreInfo keystoreInfo, String certAlias) throws MessageException, KeyException  {
		try {
			KeyStore keystore = KeyStoreUtil.getKeystore(keystoreInfo);
			Certificate certificate = keystore.getCertificate(certAlias);
			// check if is a X.509 certificate
			if (certificate == null || !certificate.getType().equals(CertificatesUtil.X509_CERTIFICATE_TYPE)) {
				throw new MessageException(NodeMessage.JEMC200E, certAlias);
			}
			// verify expiration date
			Date expirationDate = ((X509Certificate) certificate).getNotAfter();
			if (expirationDate.before(new Date())) {
				throw new MessageException(NodeMessage.JEMC201E, certAlias, expirationDate);
			}
			return certificate.getPublicKey();
		} catch (KeyStoreException e) {
			throw new KeyException(e.getMessage(), e);
		}
	}

}
