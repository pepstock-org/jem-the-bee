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
package org.pepstock.jem.ant.tasks.utilities.resources;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.ParseException;

import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.XmlUtil;
import org.pepstock.jem.node.rmi.InternalUtilities;
import org.pepstock.jem.node.rmi.UtilsInitiatorManager;
import org.pepstock.jem.node.tasks.JobId;

import com.thoughtworks.xstream.XStream;

/**
 * Utility class to use to save command line during the syntax checking and
 * execute the command
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public abstract class Command {

	private String commandLine = null;

	private String parameter = null;

	private String file = null;

	private XStream xStream = XmlUtil.getXStream();

	private InternalUtilities resourcer = null;

	private boolean noEncryption = false;

	/**
	 * Stores command line
	 * 
	 * @param commandLine
	 *            command line
	 * @throws ParseException
	 *             if command line has a syntax error
	 */
	public Command(String commandLine) throws ParseException {
		this.setCommandLine(commandLine);
	}

	/**
	 * @return the commandLine
	 */
	public String getCommandLine() {
		return commandLine;
	}

	/**
	 * @param commandLine
	 *            the commandLine to set
	 */
	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * @return the resourcer
	 * @throws UnknownHostException
	 * @throws RemoteException
	 */
	public InternalUtilities getResourcer() throws RemoteException, UnknownHostException {
		if (resourcer == null) {
			resourcer = UtilsInitiatorManager.getInternalUtilities();
		}
		return resourcer;
	}

	/**
	 * @param resourcer
	 *            the resourcer to set
	 */
	public void setResourcer(InternalUtilities resourcer) {
		this.resourcer = resourcer;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the xStream
	 */
	public XStream getxStream() {
		return xStream;
	}

	/**
	 * @param xStream
	 *            the xStream to set
	 */
	public void setxStream(XStream xStream) {
		this.xStream = xStream;
	}

	/**
	 * @return the noEncryption
	 */
	public boolean isNoEncryption() {
		return noEncryption;
	}

	/**
	 * @param noEncryption
	 *            the noEncryption to set
	 */
	public void setNoEncryption(boolean noEncryption) {
		this.noEncryption = noEncryption;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * 
	 * @param resource
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public void encrypt(Resource resource) throws RemoteException, UnknownHostException {
		for (ResourceProperty property : resource.getProperties().values()) {
			if (!property.isVisible()) {
				CryptedValueAndHash newValue = getResourcer().encrypt(JobId.VALUE, property.getValue());
				property.setValue(newValue.getCryptedValue());
				property.setHash(newValue.getHash());
			}
		}
	}

	/**
	 * 
	 * @param resource
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public void decrypt(Resource resource) throws RemoteException, UnknownHostException {
		for (ResourceProperty property : resource.getProperties().values()) {
			if (!property.isVisible()) {
				String hash = property.getHash();
				if (hash == null) {
					throw new RemoteException(AntUtilMessage.JEMZ035E.toMessage().getFormattedMessage(property.getName()));
				}
				String cryptedValue = property.getValue();
				if (cryptedValue == null) {
					throw new RemoteException(AntUtilMessage.JEMZ036E.toMessage().getFormattedMessage(property.getName()));
				}
				CryptedValueAndHash newValue = new CryptedValueAndHash();
				newValue.setCryptedValue(cryptedValue);
				newValue.setHash(hash);
				String secret = getResourcer().decrypt(JobId.VALUE, newValue);
				property.setValue(secret);
				property.setHash(null);
			}
		}
	}

	/**
	 * Execute the command
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	public abstract void execute() throws Exception;

}
