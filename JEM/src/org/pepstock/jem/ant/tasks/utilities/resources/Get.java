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

import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.naming.InitialContext;

import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.ant.tasks.utilities.CommonResourcesTask;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class Get extends Command {

	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "GET ";

	private static final String GET_FORMAT = "GET {0}";

	private static final String GET_NOENCRYPT_FORMAT = "GET {0} NOENCRYPTION";

	private static final String GET_AND_FILE_FORMAT = "GET {0} FILE {1}";

	private static final String GET_AND_FILE_NOENCRYPT_FORMAT = "GET {0} FILE {1} NOENCRYPTION";

	private static final MessageFormat FORMAT = new MessageFormat(GET_FORMAT);
	
	private static final MessageFormat FORMAT_NOENCRYPT = new MessageFormat(GET_NOENCRYPT_FORMAT);
	
	private static final MessageFormat FORMAT_FILE = new MessageFormat(GET_AND_FILE_FORMAT);
	
	private static final MessageFormat FORMAT_FILE_NOENCRYPT = new MessageFormat(GET_AND_FILE_NOENCRYPT_FORMAT);
	/**
	 * @param commandLine
	 * @throws ParseException
	 */
	public Get(String commandLine) throws ParseException {
		super(commandLine);

		Object[] object = null;
		
		try {
			object = FORMAT_FILE_NOENCRYPT.parse(commandLine);
			setNoEncryption(true);
		} catch (ParseException e) {
			try {
				object = FORMAT_FILE.parse(commandLine);
				setNoEncryption(false);
			} catch (ParseException e1){
				try {
					object = FORMAT_NOENCRYPT.parse(commandLine);
					setNoEncryption(true);
				} catch (ParseException e2){
						object = FORMAT.parse(commandLine);
						setNoEncryption(false);
				}
			}
		}

		if (object.length == 1) {
			setParameter(object[0].toString());
		} else if (object.length == 2) {

			setParameter(object[0].toString());
			setFile(object[1].toString());
			
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage().getFormattedMessage(COMMAND_KEYWORD, commandLine), 0);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.resources.Command#execute()
	 */
	@Override
	public void execute() throws Exception {
		// new initial context to access by INPUT to COMMAND DataDescription
		InitialContext ic = ContextUtils.getContext();

		String dd = (getFile() != null) ? getFile() : CommonResourcesTask.OUTPUT_DATA_DESCRIPTION_NAME;
		// gets inputstream
		Object fileout = (Object) ic.lookup(dd);
		if (fileout instanceof OutputStream){
			Resource resource = getResourcer().lookup(JobId.VALUE, getParameter());
			if (!isNoEncryption()){
				encrypt(resource);
			}
			getxStream().toXML(resource, (OutputStream)fileout);
			System.out.println(AntUtilMessage.JEMZ009I.toMessage().getFormattedMessage(resource));
		} else {
			throw new Exception(AntUtilMessage.JEMZ010E.toMessage().getFormattedMessage(dd, fileout.getClass().getName()));
		}
	}

}