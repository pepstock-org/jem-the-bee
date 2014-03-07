/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
public class ResourcesPermissionsPanel extends CheckBoxPermissionsPanel {
	
	// creates all check boxes
	private PermissionItem resourcesAll = new PermissionItem("All", "allows to have all actions on resources management", Permissions.RESOURCES_STAR);
	private PermissionItem resourcesRead = new PermissionItem("Read","allows to have READ permission, to see resources attributes", Permissions.RESOURCES_READ);
	private PermissionItem resourcesDelete = new PermissionItem("Delete","allows to have DELETE permission, to remove resources", Permissions.RESOURCES_DELETE);
	private PermissionItem resourcesCreate = new PermissionItem("Create","allows to have CREATE permission, to create new resources", Permissions.RESOURCES_CREATE);
	private PermissionItem resourcesUpdate = new PermissionItem("Update", "allows to have UPDATE permission, to change resources", Permissions.RESOURCES_UPDATE);

	/**
	 * Counstructs UI panel, using role argument to set check boxes
	 * 
	 * @param role role instance to update
	 * 
	 */
	public ResourcesPermissionsPanel(Role role) {
		super(role);
		
		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			// if is a resources permission
			if (permission.startsWith(Permissions.RESOURCES) || permission.startsWith(Permissions.STAR)){
				// is if set for all put unable all other checkbox
				if (permission.equalsIgnoreCase(Permissions.RESOURCES_STAR) || permission.startsWith(Permissions.STAR)){
					// sets ALL
					resourcesAll.setValue(true);
					for (int i=0; i<Permissions.RESOURCES_ALL.length; i++){
						// gest checkbox to set false 
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.RESOURCES_ALL[i]);
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
		resourcesAll.addClickHandler(new ResourcesAllClickHandler());

		loadCheckBoxAction(resourcesRead);
		loadCheckBoxAction(resourcesDelete);
		loadCheckBoxAction(resourcesCreate);
		loadCheckBoxAction(resourcesUpdate);

		setItems(resourcesAll, resourcesRead, resourcesDelete, resourcesCreate, resourcesUpdate);
	}

	private class ResourcesAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// checks if all is checked
			if (resourcesAll.getValue()){
				// if list doesn't contain the permission all
				if (!getRole().getPermissions().contains(Permissions.RESOURCES_STAR)) {
					// scans all permissions
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						// removes ALL permissions of JOBS
						if (permission.startsWith(Permissions.RESOURCES)){
							iter.remove();
						}
					}
					// scans all check boxes and sets FALSE
					for (int i=0; i<Permissions.RESOURCES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.RESOURCES_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					// adds the ALL permission
					getRole().getPermissions().add(Permissions.RESOURCES_STAR);
				}
			} else {
				// remove ALL permission and set enable all other check boxes
				getRole().getPermissions().remove(Permissions.RESOURCES_STAR);
				for (int i=0; i<Permissions.RESOURCES_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.RESOURCES_ALL[i]);
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
		if (permission.equalsIgnoreCase(Permissions.RESOURCES_READ)){
			return resourcesRead;
		} else if (permission.equalsIgnoreCase(Permissions.RESOURCES_DELETE)){
			return resourcesDelete;
		} else if (permission.equalsIgnoreCase(Permissions.RESOURCES_CREATE)){
			return resourcesCreate;
		} else if (permission.equalsIgnoreCase(Permissions.RESOURCES_UPDATE)){
			return resourcesUpdate;
		} else if (permission.equalsIgnoreCase(Permissions.RESOURCES_STAR)){
			return resourcesAll;
		} 
		return null;
	}
	
}