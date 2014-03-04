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
public class GfsPermissionsPanel extends CheckBoxPermissionsPanel {

	// creates all check boxes
	private PermissionItem gfsAll = new PermissionItem("All", "allows to see all global file system folders",Permissions.GFS_STAR);
	private PermissionItem gfsData = new PermissionItem("Data","allows to see data folder", Permissions.GFS_DATA);
	private PermissionItem gfsSource = new PermissionItem("Sources", "allows to see sources folder",Permissions.GFS_SOURCES);
	private PermissionItem gfsLibrary = new PermissionItem("Library","allows to see library folder", Permissions.GFS_LIBRARY);
	private PermissionItem gfsClasspath = new PermissionItem("Classpath","allows to see classpath folder", Permissions.GFS_CLASS);
	private PermissionItem gfsBinary = new PermissionItem("Binary","allows to see binary folder", Permissions.GFS_BINARY);
	

	/**
	 * Counstructs UI panel, using role argument to set check boxes
	 * 
	 * @param role role instance to update
	 * 
	 */
	public GfsPermissionsPanel(Role role) {
		super(role);

		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			// if is a gfs permission
			if (permission.startsWith(Permissions.GFS) || permission.startsWith(Permissions.STAR)){
				// is if set for all put unable all other checkbox
				if (permission.equalsIgnoreCase(Permissions.GFS_STAR) || permission.startsWith(Permissions.STAR)){
					// sets ALL
					gfsAll.setValue(true);
					for (int i=0; i<Permissions.GFS_ALL.length; i++){
						// gest checkbox to set false 
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.GFS_ALL[i]);
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
		gfsAll.addClickHandler(new GfsAllClickHandler());
		
		loadCheckBoxAction(gfsData);
		loadCheckBoxAction(gfsSource);
		loadCheckBoxAction(gfsLibrary);
		loadCheckBoxAction(gfsClasspath);
		loadCheckBoxAction(gfsBinary);

		setItems(gfsAll, gfsData, gfsSource, gfsLibrary, gfsClasspath, gfsBinary);
	}
	
	private class GfsAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// checks if all is checked
			if (gfsAll.getValue()){
				// if list doesn't contain the permission all
				if (!getRole().getPermissions().contains(Permissions.GFS_STAR)) {
					// scans all permissions
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						// removes ALL permissions of GFS
						if (permission.startsWith(Permissions.GFS)){
							iter.remove();
						}
					}
					// scans all check boxes and sets FALSE
					for (int i=0; i<Permissions.GFS_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.GFS_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					// adds the ALL permission
					getRole().getPermissions().add(Permissions.GFS_STAR);
				}
			} else {
				// remove ALL permission and set enable all other check boxes
				getRole().getPermissions().remove(Permissions.GFS_STAR);
				for (int i=0; i<Permissions.GFS_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.GFS_ALL[i]);
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
		if (permission.equalsIgnoreCase(Permissions.GFS_DATA)){
			return gfsData;
		} else if (permission.equalsIgnoreCase(Permissions.GFS_SOURCES)){
			return gfsSource;
		} else if (permission.equalsIgnoreCase(Permissions.GFS_LIBRARY)){
			return gfsLibrary;
		} else if (permission.equalsIgnoreCase(Permissions.GFS_CLASS)){
			return gfsClasspath;
		} else if (permission.equalsIgnoreCase(Permissions.GFS_BINARY)){
			return gfsBinary;
		} else if (permission.equalsIgnoreCase(Permissions.GFS_STAR)){
			return gfsAll;
		} 
		return null;
	}
	
}