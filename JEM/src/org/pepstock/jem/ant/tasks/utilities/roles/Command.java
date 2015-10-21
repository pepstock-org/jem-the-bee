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

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.ant.tasks.utilities.SubCommand;
import org.pepstock.jem.node.security.Permissions;

/**
 * Utility class to use to save command line during the syntax checking and execute the command
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public abstract class Command implements SubCommand{
	
	private static final String ENTITIES_SEPARATOR = ",";
	
	private String commandLine = null;
	
	private String permissions = null;
			
	private String users = null;
	
	private String roles = null;
	
	/**
	 * Stores command line
	 * 
	 * @param commandLine command line 
	 * @throws ParseException  if command line has a syntax error
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
	 * @param commandLine the commandLine to set
	 */
	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * @return the permissions
	 */
	public String getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the users
	 */
	public String getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(String users) {
		this.users = users;
	}

	/**
	 * @return the roles
	 */
	public String getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(String roles) {
		this.roles = roles;
	}

	/**
	 * Splits a string using the entities separator (comma)
	 * @param entity strign to split
	 * @return array of elements
	 * @throws BuildException 
	 */
	public String[] split(String entity){
		String newEntity = StringUtils.remove(entity, " ");
		return StringUtils.split(newEntity, ENTITIES_SEPARATOR);
	}
	
	/**
	 * Splits a string using the entities separator (comma)
	 * @param entity strign to split
	 * @return array of elements
	 * @throws BuildException 
	 */
	public String[] splitPermission() throws BuildException{
		String entity = StringUtils.remove(permissions, " ");
		String[] perms = StringUtils.split(entity, ENTITIES_SEPARATOR);
		for (int i=0; i<perms.length; i++){
			if (!Permissions.checkPermissionSyntax(perms[i])){
				throw new BuildException(AntUtilMessage.JEMZ037E.toMessage().getFormattedMessage(perms[i]));
			}
		}
		return perms;
	}
}