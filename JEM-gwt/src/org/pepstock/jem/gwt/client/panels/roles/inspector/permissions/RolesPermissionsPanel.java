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
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RolesPermissionsPanel extends CheckBoxPermissionsPanel {

	// creates all check boxes
	private PermissionItem rolesAll = new PermissionItem("All", "allows to have all actions on roles management", Permissions.ROLES_STAR);
	private PermissionItem rolesCreate = new PermissionItem("Read","allows to have READ permission, to see roles attributes", Permissions.ROLES_READ);
	private PermissionItem rolesDelete = new PermissionItem("Delete","allows to have DELETE permission, to remove roles", Permissions.ROLES_DELETE);
	private PermissionItem rolesRead = new PermissionItem("Create", "allows to have CREATE permission, to create new roles", Permissions.ROLES_CREATE);
	private PermissionItem rolesUpdate = new PermissionItem("Update", "allows to have UPDATE permission, to change roles", Permissions.ROLES_UPDATE);
	/**
	 * @param role
	 * @param list
	 * 
	 */
	public RolesPermissionsPanel(Role role) {
		super(role);
		
		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			if (permission.startsWith(Permissions.ROLES) || permission.startsWith(Permissions.STAR)){
				if (permission.equalsIgnoreCase(Permissions.ROLES_STAR) || permission.startsWith(Permissions.STAR)){
					rolesAll.setValue(true);
					for (int i=0; i<Permissions.ROLES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.ROLES_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
				} else {
					PermissionItem checkBox = getPermissionItemByPermission(permission);
					if (checkBox != null){
						checkBox.setValue(true);
					}

				}
			}
		}

		// sets all actions 
		rolesAll.addClickHandler(new RolesAllClickHandler());

		loadCheckBoxAction(rolesRead);
		loadCheckBoxAction(rolesDelete);
		loadCheckBoxAction(rolesCreate);
		loadCheckBoxAction(rolesUpdate);

		setItems(rolesAll, rolesRead, rolesDelete, rolesCreate, rolesUpdate);
	}

	private class RolesAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (rolesAll.getValue()){
				if (!getRole().getPermissions().contains(Permissions.ROLES_STAR)) {
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						if (permission.startsWith(Permissions.ROLES)){
							iter.remove();
						}
					}
					for (int i=0; i<Permissions.ROLES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.ROLES_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					getRole().getPermissions().add(Permissions.ROLES_STAR);
				}
			} else {
				getRole().getPermissions().remove(Permissions.ROLES_STAR);
				for (int i=0; i<Permissions.ROLES_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.ROLES_ALL[i]);
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
		if (permission.equalsIgnoreCase(Permissions.ROLES_CREATE)){
			return rolesCreate;
		} else if (permission.equalsIgnoreCase(Permissions.ROLES_DELETE)){
			return rolesDelete;
		} else if (permission.equalsIgnoreCase(Permissions.ROLES_UPDATE)){
			return rolesUpdate;
		} else if (permission.equalsIgnoreCase(Permissions.ROLES_READ)){
			return rolesRead;
		} else if (permission.equalsIgnoreCase(Permissions.ROLES_STAR)){
			return rolesAll;
		} 
		return null;
	}
	
}