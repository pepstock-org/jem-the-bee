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
public class NodesPermissionsPanel extends CheckBoxPermissionsPanel {

	// creates all check boxes
	private PermissionItem nodesAll = new PermissionItem("All", "allows to have all actions on nodes", Permissions.NODES_STAR);
	private PermissionItem nodesStart = new PermissionItem("Start", "allows to have START command to perform on nodes", Permissions.NODES_START);
	private PermissionItem nodesDrain = new PermissionItem("Drain", "allows to have DRAIN command to perform on nodes",Permissions.NODES_DRAIN);
	private PermissionItem nodesUpdate = new PermissionItem("Update", "allows to have UPDATE affinities and domain on nodes",Permissions.NODES_UPDATE);
	/**
	 * @param role
	 * @param list
	 * 
	 */
	public NodesPermissionsPanel(Role role) {
		super(role);
		
		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			if (permission.startsWith(Permissions.NODES) || permission.startsWith(Permissions.STAR)){
				if (permission.equalsIgnoreCase(Permissions.NODES_STAR) || permission.startsWith(Permissions.STAR)){
					nodesAll.setValue(true);
					for (int i=0; i<Permissions.NODES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.NODES_ALL[i]);
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
		nodesAll.addClickHandler(new NodesAllClickHandler());

		loadCheckBoxAction(nodesStart);
		loadCheckBoxAction(nodesDrain);
		loadCheckBoxAction(nodesUpdate);
		
		setItems(nodesAll, nodesStart, nodesDrain, nodesUpdate);
	}
	
	private class NodesAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (nodesAll.getValue()){
				if (!getRole().getPermissions().contains(Permissions.NODES_STAR)) {
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						if (permission.startsWith(Permissions.NODES)){
							iter.remove();
						}
					}
					for (int i=0; i<Permissions.NODES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.NODES_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					getRole().getPermissions().add(Permissions.NODES_STAR);
				}
			} else {
				getRole().getPermissions().remove(Permissions.NODES_STAR);
				for (int i=0; i<Permissions.NODES_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.NODES_ALL[i]);
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
		if (permission.equalsIgnoreCase(Permissions.NODES_START)){
			return nodesStart;
		} else if (permission.equalsIgnoreCase(Permissions.NODES_DRAIN)){
			return nodesDrain;
		} else if (permission.equalsIgnoreCase(Permissions.NODES_UPDATE)){
			return nodesUpdate;
		} else if (permission.equalsIgnoreCase(Permissions.NODES_STAR)){
			return nodesAll;
		} 
		return null;
	}
	
}