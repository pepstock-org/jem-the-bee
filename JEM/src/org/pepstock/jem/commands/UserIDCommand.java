/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.commands;

import org.hyperic.sigar.ProcCredName;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.pepstock.jem.log.LogAppl;

/**
 * This class contains the userid for all command which nedss userid.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class UserIDCommand {
	
	private static final Sigar SIGAR = new Sigar();
	
	private String userID = null;

	private String groupID = null;
	
	/**
	 * Constructs object loading the user id from system
	 */
	public UserIDCommand() {
		loadUserID();
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	private void setUserID(String userID) {
		this.userID = userID;
	}
	
	
	/**
	 * @return the groupID
	 */
	public String getGroupID() {
		return groupID;
	}

	/**
	 * @param groupID the groupID to set
	 */
	private void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	/**
	 * Loads user id from system
	 */
	private void loadUserID(){
		try {
			ProcCredName cred = SIGAR.getProcCredName(SIGAR.getPid());
			setUserID(cred.getUser());
			setGroupID(cred.getGroup());
		} catch (SigarException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// if exception, use the system property. Be carefully that is NOT secure
			setUserID(System.getProperty("user.name"));
		}
	}
	
	

}