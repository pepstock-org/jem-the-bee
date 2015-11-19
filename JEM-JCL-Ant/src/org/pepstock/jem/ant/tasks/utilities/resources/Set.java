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

import java.io.InputStream;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.text.ParseException;

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

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 *
 */
public class Set extends Command {

	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "SET";

	private static final String SET_FORMAT = "SET";

	private static final String SET_NOENCRYPT_FORMAT = "SET NOENCRYPTION";

	private static final String SET_AND_FILE_FORMAT = "SET SOURCE {0}";

	private static final String SET_AND_FILE_NOENCRYPT_FORMAT = "SET SOURCE {0} NOENCRYPTION";

	private static final MessageFormat FORMAT = new MessageFormat(SET_FORMAT);
	
	private static final MessageFormat FORMAT_NOENCRYPT = new MessageFormat(SET_NOENCRYPT_FORMAT);
	
	private static final MessageFormat FORMAT_FILE = new MessageFormat(SET_AND_FILE_FORMAT);
	
	private static final MessageFormat FORMAT_FILE_NOENCRYPT = new MessageFormat(SET_AND_FILE_NOENCRYPT_FORMAT);
	/**
	 * @param commandLine
	 * @throws ParseException
	 */
	public Set(String commandLine) throws ParseException {
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
			setFile(object[0].toString());
		} else {
			setFile(CommonResourcesTask.INPUT_DATA_DESCRIPTION_NAME);
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
			// gets inputstream
			Object filein = (Object) ic.lookup(getFile());
			if (filein instanceof InputStream){
				Object data = getxStream().fromXML((InputStream)filein);
				if (data instanceof Resource){
					Resource resource = (Resource) data;
					addResource(resource);
				} else if (data instanceof ResourcesList){
					ResourcesList resources = (ResourcesList)data;
					for (Resource resource : resources.getResources()){
						addResource(resource);
					}
				} else {
					throw new MessageException(AntUtilMessage.JEMZ017E, data.getClass().getName());
				}
			} else {
				throw new MessageException(AntUtilMessage.JEMZ011E, getFile(), filein.getClass().getName());
			}
		} catch (RemoteException e) {
			throw new JemException(e);
		} catch (UnknownHostException e) {
			throw new JemException(e);
		} catch (NamingException e) {
			throw new JemException(e);
		}
	}
	
	/**
	 * 
	 * @param resource
	 * @throws UnknownHostException 
	 * @throws RemoteException 
	 */
	private void addResource(Resource resource) throws RemoteException, UnknownHostException {
		if (!isNoEncryption()){
			decrypt(resource);					
		}
		boolean isAdded = getResourcer().put(JobId.VALUE, resource);
		if (isAdded){
			LogAppl.getInstance().emit(AntUtilMessage.JEMZ015I, resource);
		} else {
			LogAppl.getInstance().emit(AntUtilMessage.JEMZ016I, resource);
		}
	}

}