/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Bean with all information about the KEY store to use inside the JEM.
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0	
 *
 */
public class KeyStoreInfo {

	/**
	 * The JCEKS keystore type necessary for keystore containing symmetric key
	 */
	public static final String JCEKS_KEYSTORE_TYPE = "JCEKS";
	/**
	 * The JKS keystore type standard for storing x509 certificate
	 */
	public static final String JKS_KEYSTORE_TYPE = "JKS";
	
	private File file;
	
	private File backupFile;
	
	private String password;

	private String symmetricKeyAlias;
	
	private String symmetricKeyPwd;
	
	private String type = null;
	
	private ByteArrayOutputStream bytes = null;

	/**
	 * Creates the object using the type of key store 
	 * @param type type of this keystore
	 */
	public KeyStoreInfo(String type) {
		// if type is null, EXCEPTION
		if (type == null){
			throw new IllegalArgumentException("Type is null");
		}
		// if is not a right value, EXCEPTION
		if (!type.equalsIgnoreCase(JCEKS_KEYSTORE_TYPE) && !type.equalsIgnoreCase(JKS_KEYSTORE_TYPE)){
			throw new IllegalArgumentException("Type '"+type+"'is invalid");
		}
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * @return the backupFile
	 */
	public File getBackupFile() {
		return backupFile;
	}

	/**
	 * @param backupFile the backupFile to set
	 */
	public void setBackupFile(File backupFile) {
		this.backupFile = backupFile;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the symmetricKeyAlias
	 */
	public String getSymmetricKeyAlias() {
		return symmetricKeyAlias;
	}

	/**
	 * @param symmetricKeyAlias the symmetricKeyAlias to set
	 */
	public void setSymmetricKeyAlias(String symmetricKeyAlias) {
		this.symmetricKeyAlias = symmetricKeyAlias;
	}

	/**
	 * @return the symmetricKeyPwd
	 */
	public String getSymmetricKeyPwd() {
		return symmetricKeyPwd;
	}

	/**
	 * @param symmetricKeyPwd the symmetricKeyPwd to set
	 */
	public void setSymmetricKeyPwd(String symmetricKeyPwd) {
		this.symmetricKeyPwd = symmetricKeyPwd;
	}

	/**
	 * @return the bytes
	 */
	public ByteArrayOutputStream getBytes() {
		return bytes;
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(ByteArrayOutputStream bytes) {
		this.bytes = bytes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KeyStoreInfo [file=" + file + ", backupFile=" + backupFile + ", type=" + type + "]";
	}
}