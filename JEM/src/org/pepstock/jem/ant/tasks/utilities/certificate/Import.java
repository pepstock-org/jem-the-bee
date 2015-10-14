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

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.rmi.InternalUtilities;
import org.pepstock.jem.node.rmi.UtilsInitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.util.CharSet;

/**
 * @author Simone "Stock" Businarp
 * @version 2.3
 * 
 *          Command used in ant jcl to import the x509 user certificate into the
 *          JEM keystore
 */
public class Import extends Command {

	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "IMPORT";

	private static final String IMPORT_FILE_FORMAT = "IMPORT CERTIFICATE {0} ALIAS {1}";

	private static final MessageFormat FORMAT_FILE = new MessageFormat(IMPORT_FILE_FORMAT);

	/**
	 * Is the delete command for an alias inside the JEM user keystore.
	 * <p>
	 * Example:
	 * <p>
	 * IMPORT CERTIFICATE [DATA_DESCRIPTOR_NAME] ALIAS PIPPO
	 * <p>
	 * insert in the JEM user keystore the certificate contained in the
	 * DATA_DESCRIPTOR
	 * 
	 * @param commandLine
	 * @throws ParseException
	 */
	public Import(String commandLine) throws ParseException {
		super(commandLine);

		Object[] object = null;
		object = FORMAT_FILE.parse(commandLine);

		if (object.length == 2) {
			setFile(object[0].toString());
			setAlias(object[1].toString());
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage()
					.getFormattedMessage(COMMAND_KEYWORD, commandLine), 0);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.ant.tasks.utilities.resources.Command#execute()
	 */
	@Override
	public void execute() throws JemException {

		try {
			// new initial context to access by INPUT to COMMAND DataDescription
			InitialContext ic = ContextUtils.getContext();

			// read certificate to a string
			Object filein = (Object) ic.lookup(getFile());
			if (filein instanceof InputStream) {
				// read the certificate
				InputStream in = (InputStream) filein;
			
				StringBuilder sb = new StringBuilder();
				List<String> rows = IOUtils.readLines(in, CharSet.DEFAULT);
				for (String row: rows){
					String trimRow = row.trim();
					if (!"".equals(trimRow)){
						sb.append(trimRow).append(System.getProperty("line.separator"));
					}
				}
				in.close();
				String certificate = sb.toString();
				System.out.println(certificate);
				InternalUtilities util = UtilsInitiatorManager.getInternalUtilities();
				util.importCertificate(JobId.VALUE, certificate.getBytes(CharSet.DEFAULT),	getAlias());
			} else {
				throw new MessageException(AntUtilMessage.JEMZ011E,	getFile(), filein.getClass().getName());
			}
		} catch (RemoteException e) {
			throw new JemException(e);
		} catch (UnknownHostException e) {
			throw new JemException(e);
		} catch (NamingException e) {
			throw new JemException(e);
		} catch (IOException e) {
			throw new JemException(e);
		}
	}
}