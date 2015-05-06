/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.security.Crypto;

import com.hazelcast.config.SocketInterceptorConfig;

/**
 * Utility to manage the keystores and keys, managed inside the JEM node.<br>
 * 
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
	 * Returns the keystores with symmetric keys of used for socket interceptor.
	 * 
	 * @return return keystore info
	 */
	public static synchronized KeyStoresInfo getKeyStoresInfo() {
		if (INFO == null) {
			// gets key store properteis from HC configuration
			SocketInterceptorConfig config = Main.getHazelcast().getConfig().getNetworkConfig().getSocketInterceptorConfig();
			// creates keystore
			INFO = Factory.createKeyStoresInfo(config.getProperties());
		}
		return INFO;
	}
	
	/**
	 * Returns a SSL socket factory creating asymmetric keys at runtime.
	 * 
	 * @return a SSL socket factory for HTTPS listener 
	 * @throws KeyStoreException if any errors occurs to get keys
	 */
	public static SSLServerSocketFactory getSSLServerSocketFactory() throws KeyStoreException {
		try {
			// gets a key stores created at runtime
			ByteArrayInputStream baos = SelfSignedCertificate.getCertificate();
			KeyStore keystore  = KeyStore.getInstance("jks");
			// loads the keystore
			keystore.load(baos, SelfSignedCertificate.CERTIFICATE_PASSWORD.toCharArray());
			KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
					KeyManagerFactory.getDefaultAlgorithm());
			
			// initialiazes the key manager
			kmfactory.init(keystore, SelfSignedCertificate.CERTIFICATE_PASSWORD.toCharArray());
			KeyManager[] keymanagers = kmfactory.getKeyManagers();
			// creates SSL socket factory
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(keymanagers, null, null);
			return sslcontext.getServerSocketFactory();
		} catch (UnrecoverableKeyException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (KeyManagementException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (SecurityException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (IOException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (OperatorCreationException e) {
			throw new KeyStoreException(e.getMessage(), e);
		}
	}
	
	/**
	 * Gets a key store using a entity with the information where it has to read and 
	 * load into the keystore
	 * 
	 * @param keystoreInfo entity with information about keystore
	 * @return a new keystore
	 * @throws KeyStoreException if any error occurs during the keystore creation
	 */
	static KeyStore getKeystore(KeyStoreInfo keystoreInfo) throws KeyStoreException {
		// gets keystore
		KeyStore keystore = KeyStore.getInstance(keystoreInfo.getType());
		InputStream is = null;
		try {
			// if the entity must read the keystore from memory
			// used the bytes of the entity and
			if (keystoreInfo.getBytes() != null){
				is = new ByteArrayInputStream(keystoreInfo.getBytes().toByteArray());
			} else {
				// otherwise it reads the keystore from the file system 
				is = new FileInputStream(keystoreInfo.getFile());
			}
			// loads the key store
			keystore.load(is, keystoreInfo.getPassword().toCharArray());
		} catch (FileNotFoundException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (IOException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} finally {
			// if inputstream is not null
			// it closes
			if (is != null){
				try {
					is.close();
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
	 * @param keystoreInfo entity with information about keystore
	 * @throws KeyStoreException if any exception occurs during key store creation
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
				// creates simmetricKey
				Key secretKey = Crypto.generateSymmetricKey();
				// adds the key
				keystore.setKeyEntry(keystoreInfo.getSymmetricKeyAlias(), secretKey, keystoreInfo.getSymmetricKeyPwd().toCharArray(), null);
				// saves the keystore
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
	 * Saves the kwystore on the file
	 * @param keystore keystore to be saved
	 * @param info Key store info with all necessary info to save it 
	 * @throws KeyStoreException if any error occurs saving the key store
	 */
	static void save(KeyStore keystore, KeyStoreInfo info) throws KeyStoreException {
		OutputStream os = null;
		try {
			// creates the file stream
			os = new FileOutputStream(info.getFile());
			// stores the file 
			keystore.store(os, info.getPassword().toCharArray());
			// checks if it must be backuped
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
			// always it closes the outut stream
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