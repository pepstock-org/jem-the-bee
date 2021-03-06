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
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.ant.tasks.utilities.CommonResourcesTask;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourcesList;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.util.Numbers;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class GetList extends Command {

	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "GETLIST ";

	private static final String GET_FORMAT = "GETLIST {0}";

	private static final String GET_NOENCRYPT_FORMAT = "GETLIST {0} NOENCRYPTION";

	private static final String GET_AND_FILE_FORMAT = "GETLIST {0} FILE {1}";

	private static final String GET_AND_FILE_NOENCRYPT_FORMAT = "GETLIST {0} FILE {1} NOENCRYPTION";

	private static final MessageFormat FORMAT = new MessageFormat(GET_FORMAT);
	
	private static final MessageFormat FORMAT_NOENCRYPT = new MessageFormat(GET_NOENCRYPT_FORMAT);
	
	private static final MessageFormat FORMAT_FILE = new MessageFormat(GET_AND_FILE_FORMAT);
	
	private static final MessageFormat FORMAT_FILE_NOENCRYPT = new MessageFormat(GET_AND_FILE_NOENCRYPT_FORMAT);
	/**
	 * @param commandLine
	 * @throws ParseException
	 */
	public GetList(String commandLine) throws ParseException {
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

		if (object.length == Numbers.N_1) {
			setParameter(object[ELEMENT_1].toString());
		} else if (object.length == Numbers.N_2) {
			setParameter(object[ELEMENT_1].toString());
			setFile(object[ELEMENT_2].toString());
			
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage().getFormattedMessage(COMMAND_KEYWORD, commandLine), ELEMENT_1);
		}

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.resources.Command#execute()
	 */
	@Override
	public void execute() throws JemException {
		try {
			// new initial context to access by INPUT to COMMAND DataDescription
			InitialContext ic = ContextUtils.getContext();

			String dd = (getFile() != null) ? getFile() : CommonResourcesTask.OUTPUT_DATA_DESCRIPTION_NAME;
			// gets inputstream
			Object fileout = (Object) ic.lookup(dd);
			if (fileout instanceof OutputStream){
				Collection<Resource> allResources = getResourcer().values(JobId.VALUE, getParameter());
				ResourcesList resources = new ResourcesList();
				List<Resource> list = new ArrayList<Resource>();
				for (Resource resource : allResources){
					if (!isNoEncryption()){
						encrypt(resource);
					}
					list.add(resource);
				}
				resources.setResources(list);
			
				getxStream().toXML(resources, (OutputStream)fileout);
				LogAppl.getInstance().emit(AntUtilMessage.JEMZ012I, allResources.size());
			} else {
				throw new MessageException(AntUtilMessage.JEMZ010E, dd, fileout.getClass().getName());
			}
		} catch (RemoteException e) {
			throw new JemException(e);
		} catch (UnknownHostException e) {
			throw new JemException(e);
		} catch (NamingException e) {
			throw new JemException(e);
		}
	}

}