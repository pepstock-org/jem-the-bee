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
package org.pepstock.jem.ant.tasks.utilities.roles;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.text.ParseException;

import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.rmi.InternalUtilities;
import org.pepstock.jem.node.rmi.UtilsInitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.util.Numbers;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class Delete extends Command {

	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "DELETE ";
	
	// format of GDG command
	private static final String DELETE_FORMAT = "DELETE {0} FROM {1}";

	private static final MessageFormat FORMAT = new MessageFormat(DELETE_FORMAT);

	/**
	 * @param commandLine
	 * @throws ParseException
	 */
	public Delete(String commandLine) throws ParseException {
		super(commandLine);
		// parse command
		Object[] object = FORMAT.parse(commandLine);
		
		if (object.length == Numbers.N_2){
			setUsers(object[ELEMENT_1].toString());
			setRoles(object[ELEMENT_2].toString());
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage().getFormattedMessage(COMMAND_KEYWORD, commandLine), ELEMENT_1);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.nodes.Command#execute()
	 */
	@Override
	public void execute() throws JemException {
		if ((getRoles() != null) && (getUsers() != null)){
			try {
				String[] users = split(getUsers());
				String[] roles = split(getRoles());

				InternalUtilities util = UtilsInitiatorManager.getInternalUtilities();
				util.delete(JobId.VALUE, users, roles);
				
				for (int k=0; k<roles.length; k++){
					for (int i=0; i<users.length; i++){
						LogAppl.getInstance().emit(AntUtilMessage.JEMZ020I, users[i], roles[k]);
					}
				}
			} catch (RemoteException e) {
				throw new JemException(e);
			} catch (UnknownHostException e) {
				throw new JemException(e);
			}
		}
	}
}