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
package org.pepstock.jem.quartz;

import java.util.List;

import org.pepstock.jem.commands.SubmitParameters;

/**
 * Is a JCL job which contains all information about the private key to use
 * to connect JEM cluster.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public abstract class PrivateKeyJclJob extends JclJob {

	private String privateKeyFile = null;

	private String privateKeyPwd = null;
	
	/**
	 * Empty constructor
	 */
	public PrivateKeyJclJob() {
	}

	/**
	 * @return the privateKeyFile
	 */
	public final String getPrivateKeyFile() {
		return privateKeyFile;
	}

	/**
	 * @param privateKeyFile the privateKeyFile to set
	 */
	public final void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

	/**
	 * @return the privateKeyPwd
	 */
	public final String getPrivateKeyPwd() {
		return privateKeyPwd;
	}

	/**
	 * @param privateKeyPwd the privateKeyPwd to set
	 */
	public final void setPrivateKeyPwd(String privateKeyPwd) {
		this.privateKeyPwd = privateKeyPwd;
	}

	/**
	 * Creates a list of argument to use to start Quartz job
	 * @return list of string with all arguments
	 */
	@Override
	public List<String> createArgs() {
		List<String> list = super.createArgs();
		// if private key is not null
		// it creates the argument for Submit
		// with Private KEY
		if (privateKeyFile != null) {
			list.add("-" + SubmitParameters.PRIVATE_KEY.getName());
			list.add(privateKeyFile);
		}
		// if private key passowrd is not null
		// it creates the argument for Submit
		// with Private KEY pwd
		if (privateKeyPwd != null) {
			list.add("-" + SubmitParameters.PRIVATE_KEY_PWD.getName());
			list.add(privateKeyPwd);
		}
		return list;
	}
}
