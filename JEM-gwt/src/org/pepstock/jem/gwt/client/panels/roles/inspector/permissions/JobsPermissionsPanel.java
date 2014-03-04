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
package org.pepstock.jem.gwt.client.panels.roles.inspector.permissions;

import java.util.Iterator;

import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.CheckBoxPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.PermissionItem;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * panel to manage all JOBS domain permissions. It-s managing all check boxes and add and remove of permissions
 * into role object
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobsPermissionsPanel extends CheckBoxPermissionsPanel {
	
	// creates all check boxes
	private PermissionItem jobsAll = new PermissionItem("All", "allows to have all actions on jobs", Permissions.JOBS_STAR);
	private PermissionItem jobsHold = new PermissionItem("Hold", "allows to have permission to HOLD jobs",Permissions.JOBS_HOLD);
	private PermissionItem jobsRelease = new PermissionItem("Release", "allows to have permission to RELEASE jobs, previously changed in HOLD",Permissions.JOBS_RELEASE);
	private PermissionItem jobsPurge = new PermissionItem("Purge", "allows to have permission to PURGE jobs from queues",Permissions.JOBS_PURGE);
	private PermissionItem jobsCancel = new PermissionItem("Cancel", "allows to have permission to CANCEL jobs in execution",Permissions.JOBS_CANCEL);
	private PermissionItem jobsKill = new PermissionItem("Kill", "allows to have permission to KILL jobs in execution",Permissions.JOBS_KILL);
	private PermissionItem jobsSubmit = new PermissionItem("Submit","allows to have permission to SUBMIT jobs", Permissions.JOBS_SUBMIT);
	private PermissionItem jobsUpdate = new PermissionItem("Update","allows to have permission to UPDATE fields of jobs", Permissions.JOBS_UPDATE);
	

	/**
	 * Counstructs UI panel, using role argument to set check boxes
	 * 
	 * @param role role instance to update
	 * 
	 */
	public JobsPermissionsPanel(Role role) {
		super(role);

		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			// if is a jobs permission
			if (permission.startsWith(Permissions.JOBS) || permission.startsWith(Permissions.STAR)){
				// is if set for all put unable all other checkbox
				if (permission.equalsIgnoreCase(Permissions.JOBS_STAR) || permission.startsWith(Permissions.STAR)){
					// sets ALL
					jobsAll.setValue(true);
					for (int i=0; i<Permissions.JOBS_ALL.length; i++){
						// gest checkbox to set false 
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.JOBS_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
				} else {
					// gets check box and sets it true
					PermissionItem checkBox = getPermissionItemByPermission(permission);
					if (checkBox != null){
						checkBox.setValue(true);
					}
				}
			}
		}

		// sets actions for ALL check box 
		jobsAll.addClickHandler(new JobsAllClickHandler());

		loadCheckBoxAction(jobsHold);
		loadCheckBoxAction(jobsRelease);
		loadCheckBoxAction(jobsPurge);
		loadCheckBoxAction(jobsCancel);
		loadCheckBoxAction(jobsKill);
		loadCheckBoxAction(jobsSubmit);
		loadCheckBoxAction(jobsUpdate);
		
		setItems(jobsAll, jobsHold, jobsRelease, jobsPurge, jobsCancel, jobsKill, jobsSubmit, jobsUpdate);
	}
	
	private class JobsAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// checks if all is checked
			if (jobsAll.getValue()){
				// if list doesn't contain the permission all
				if (!getRole().getPermissions().contains(Permissions.JOBS_STAR)) {
					// scans all permissions
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						// removes ALL permissions of JOBS
						if (permission.startsWith(Permissions.JOBS)){
							iter.remove();
						}
					}
					// scans all check boxes and sets FALSE
					for (int i=0; i<Permissions.JOBS_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.JOBS_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					// adds the ALL permission
					getRole().getPermissions().add(Permissions.JOBS_STAR);
				}
			} else {
				// remove ALL permission and set enable all other check boxes
				getRole().getPermissions().remove(Permissions.JOBS_STAR);
				for (int i=0; i<Permissions.JOBS_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.JOBS_ALL[i]);
					if (checkBox != null){
						checkBox.setEnabled(true);
					}
				}
			}
		}
	}
	
	/**
	 * look for the right check box starting from permission name
	 * 
	 * @param permission permssion to check
	 * @return check box 
	 */
	private PermissionItem getPermissionItemByPermission(String permission){
		if (permission.equalsIgnoreCase(Permissions.JOBS_HOLD)){
			return jobsHold;
		} else if (permission.equalsIgnoreCase(Permissions.JOBS_RELEASE)){
			return jobsRelease;
		} else if (permission.equalsIgnoreCase(Permissions.JOBS_PURGE)){
			return jobsPurge;
		} else if (permission.equalsIgnoreCase(Permissions.JOBS_CANCEL)){
			return jobsCancel;
		} else if (permission.equalsIgnoreCase(Permissions.JOBS_KILL)){
			return jobsKill;
		} else if (permission.equalsIgnoreCase(Permissions.JOBS_SUBMIT)){
			return jobsSubmit;
		} else if (permission.equalsIgnoreCase(Permissions.JOBS_UPDATE)){
			return jobsUpdate;
		} else if (permission.equalsIgnoreCase(Permissions.JOBS_STAR)){
			return jobsAll;
		} 
		return null;
	}
	
}