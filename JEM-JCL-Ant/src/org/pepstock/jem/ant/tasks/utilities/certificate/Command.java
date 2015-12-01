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
package org.pepstock.jem.ant.tasks.utilities.certificate;

import java.text.ParseException;

import org.pepstock.jem.ant.tasks.utilities.SubCommand;

/**
 * Utility class to use to save command line during the syntax checking and
 * execute the command
 * 
 * @author Simone "Busy" Businaro
 * @version 2.3
 * 
 */
public abstract class Command implements SubCommand{
	
	static final int ELEMENT_1 = 0;

	static final int ELEMENT_2 = 1;

	private String commandLine = null;

	private String file = null;
	
	private String x509CertificateB64 = null;

	private String alias = null;

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
	 * @return the x509Certificate base64 encoded
	 */
	public String getCertificate() {
		return x509CertificateB64;
	}

	/**
	 * @param certificate
	 *            the X509 certificate encoded in base64
	 */
	public void setCertificate(String certificate) {
		this.x509CertificateB64 = certificate;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
}