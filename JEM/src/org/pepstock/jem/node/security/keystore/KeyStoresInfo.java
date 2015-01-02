/** 
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone
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

/**
 * 
 * @author Simone
 * 
 */
public class KeyStoresInfo {

	private KeyStoreInfo clusterKeystoreInfo;

	private KeyStoreInfo userKeystoreInfo;

	/**
	 * @return the clusterKeystore which contains the symmetric key used during
	 *         login phase among nodes
	 */
	public KeyStoreInfo getClusterKeystoreInfo() {
		return clusterKeystoreInfo;
	}

	/**
	 * @param clusterKeystoreInfo the clusterKeystoreInfo to set which contains
	 *            the symmetric key used during login phase among nodes
	 */
	public void setClusterKeystoreInfo(KeyStoreInfo clusterKeystoreInfo) {
		this.clusterKeystoreInfo = clusterKeystoreInfo;
	}

	/**
	 * @return the userKeystore which contains the certificates used during
	 *         login phase between cluster and submit client
	 */
	public KeyStoreInfo getUserKeystoreInfo() {
		return userKeystoreInfo;
	}

	/**
	 * @param userKeystoreInfo the userKeystoreInfo to set which contains the
	 *            certificates used during login phase between cluster and
	 *            submit client
	 */
	public void setUserKeystoreInfo(KeyStoreInfo userKeystoreInfo) {
		this.userKeystoreInfo = userKeystoreInfo;
	}

}
