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
package org.pepstock.jem.node.security.keystore;

import java.io.File;
import java.util.Properties;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class Factory {
	

	/**
	 * is the property defined inside the hazelcast configuration that indicates
	 * the keystore path
	 */
	public static final String JEM_KEYSTORE_PATH_PROP = "jem.keystore.path";
	/**
	 * is the property defined inside the hazelcast configuration that indicates
	 * the keystore path
	 */
	public static final String USER_KEYSTORE_PATH_PROP = "jem.user.keystore.path";
	/**
	 * is the property defined inside the hazelcast configuration that indicates
	 * the keystore password
	 */
	public static final String JEM_KEYSTORE_PWD_PROP = "jem.keystore.pwd";
	/**
	 * is the property defined inside the hazelcast configuration that indicates
	 * the symmetric key password
	 */
	public static final String JEM_CRYPT_KEY_PWD_PROP = "jem.crypt.key.pwd";

	/**
	 * is the property defined inside the hazelcast configuration that indicates
	 * the symmetric key alias
	 */
	public static final String JEM_CRYPT_KEY_ALIAS_PROP = "jem.crypt.key.alias";

	/**
	 * the keystores folder
	 */
	public static final String KEYSTORES_FOLDER = "keystores";

	/**
	 * To avoid any instantiation
	 */
	private Factory() {
		
	}

	/**
	 * Returns a keystores info reading Socket interceptor information
	 * 
	 * @param configProperties socket interceptor properties information
	 * @return keystores info
	 */
	public static KeyStoresInfo createKeyStoresInfo(Properties configProperties){
		File clusterKeystoreFile = new File(configProperties.getProperty(JEM_KEYSTORE_PATH_PROP));
		File userKeystoreFile = new File(configProperties.getProperty(USER_KEYSTORE_PATH_PROP));
		String keystorePasswd = configProperties.getProperty(JEM_KEYSTORE_PWD_PROP);
		String keyPasswd = configProperties.getProperty(JEM_CRYPT_KEY_PWD_PROP);
		String keyAlias = configProperties.getProperty(JEM_CRYPT_KEY_ALIAS_PROP);

		// Info relative to the keystore containing the symmetric key
		KeyStoreInfo clusterKeystoreInfo = new KeyStoreInfo(KeyStoreInfo.JCEKS_KEYSTORE_TYPE);
		clusterKeystoreInfo.setFile(clusterKeystoreFile);
		clusterKeystoreInfo.setBackupFile(new File(clusterKeystoreFile.getAbsolutePath()+".backup"));
		clusterKeystoreInfo.setPassword(keystorePasswd);
		clusterKeystoreInfo.setSymmetricKeyAlias(keyAlias);
		clusterKeystoreInfo.setSymmetricKeyPwd(keyPasswd);

		// Info relative to the keystore containing the user certificate
		KeyStoreInfo userKeystoreInfo = new KeyStoreInfo(KeyStoreInfo.JKS_KEYSTORE_TYPE);
		userKeystoreInfo.setFile(userKeystoreFile);
		userKeystoreInfo.setBackupFile(new File(userKeystoreFile.getAbsolutePath()+".backup"));
		userKeystoreInfo.setPassword(keystorePasswd);

		// keystores container
		KeyStoresInfo keystoresInfo=new KeyStoresInfo();
		keystoresInfo.setClusterKeystoreInfo(clusterKeystoreInfo);
		keystoresInfo.setUserKeystoreInfo(userKeystoreInfo);
		return keystoresInfo;
	}
}
