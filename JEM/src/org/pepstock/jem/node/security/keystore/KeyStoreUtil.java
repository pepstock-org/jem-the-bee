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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.security.Crypto;

import com.hazelcast.config.SocketInterceptorConfig;

/**
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class KeyStoreUtil {

	private static KeyStoresInfo INFO = null;
	
	/**
	 * Private constructor to avoid any instantiation 
	 */
	private KeyStoreUtil() {
		
	}

	/**
	 * @return return keystore info
	 */
	public static synchronized KeyStoresInfo getKeyStoresInfo() {
		if (INFO == null) {
			SocketInterceptorConfig config = Main.getHazelcast().getConfig().getNetworkConfig().getSocketInterceptorConfig();
			INFO = Factory.createKeyStoresInfo(config.getProperties());
		}
		return INFO;
	}
	
	/**
	 * 
	 * @param keystoreInfo
	 * @return
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws Exception
	 */
	static KeyStore getKeystore(KeyStoreInfo keystoreInfo) throws KeyStoreException {
		KeyStore keystore = KeyStore.getInstance(keystoreInfo.getType());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(keystoreInfo.getFile());
			keystore.load(fis, keystoreInfo.getPassword().toCharArray());
		} catch (FileNotFoundException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (IOException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} finally {
			if (fis != null){
				try {
					fis.close();
				} catch (Exception e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);					
				}
			}
		}
		return keystore;
	}
	/**
	 * Generate an empty key store where will be store the X509 certificate of
	 * the user
	 * <p>
	 * This key store will be used when the client will used a private key to
	 * connect to the cluster and the cluster will used the relative public key
	 * present in the x509 certificate to verify the identity of the client.
	 * @param keystoreInfo 
	 * @throws Exception 
	 * @throws UnrecoverableKeyException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @throws KeyException if any exception occurs during key store creation
	 * 
	 */
	public static void generate(KeyStoreInfo keystoreInfo) throws KeyStoreException {
		try {
			// if the keystore exist load it else create a new one
			KeyStore keystore = null;
			if (keystoreInfo.getFile().exists()) {
				keystore = getKeystore(keystoreInfo);
			} else {
				keystore = KeyStore.getInstance(keystoreInfo.getType());
				keystore.load(null, null);
				save(keystore, keystoreInfo);
			}
			// if the keystore does not contain the given alias, create a new key
			// with that alias otherwise does nothing
			if (keystoreInfo.getSymmetricKeyAlias() != null && keystoreInfo.getSymmetricKeyPwd() != null && 
					keystore.getKey(keystoreInfo.getSymmetricKeyAlias(), keystoreInfo.getSymmetricKeyPwd().toCharArray()) == null) {
				Key secretKey = Crypto.generateSymmetricKey();
				keystore.setKeyEntry(keystoreInfo.getSymmetricKeyAlias(), secretKey, keystoreInfo.getSymmetricKeyPwd().toCharArray(), null);
				save(keystore, keystoreInfo);
			}
		} catch (UnrecoverableKeyException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (IOException e) {
			throw new KeyStoreException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param keystore
	 * @param keystoreFile
	 * @param keystoreBackupFile
	 * @param keystorePasswd
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @throws Exception
	 */
	static void save(KeyStore keystore, KeyStoreInfo info) throws KeyStoreException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(info.getFile());
			keystore.store(os, info.getPassword().toCharArray());
			os.close();
			if (info.getBackupFile() != null){
				// read keystore to check if is consistent
				getKeystore(info);
				FileUtils.copyFile(info.getFile(), info.getBackupFile());
			}
		} catch (FileNotFoundException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (IOException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} finally {
			if (os != null){
				try {
					os.close();
				} catch (Exception e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);					
				}
			}
		}
	}
}
