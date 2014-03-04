/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Simone "Busy" Businaro
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
 * @author Simone "Busy" Businaro
 * 
 */
public class SwarmPermissionsPanel extends CheckBoxPermissionsPanel {

	// creates all check boxes
	private PermissionItem nodesAll = new PermissionItem("All", "allows to have all actions on swarm nodes", Permissions.SWARM_NODES_STAR);
	private PermissionItem nodesViewConfig = new PermissionItem("View config", "allows to VIEW swarm cluster configuration",Permissions.SWARM_NODES_VIEW_CONFIG);
	private PermissionItem nodesEditConfig = new PermissionItem("Edit config", "allows to EDIT swarm cluster configurations",Permissions.SWARM_NODES_EDIT_CONFIG);
	private PermissionItem nodesStart = new PermissionItem("Start", "allows to have START command to perform on swarm nodes",Permissions.SWARM_NODES_START);
	private PermissionItem nodesDrain = new PermissionItem("Drain", "allows to have STOP command to perform on swarm nodes",Permissions.SWARM_NODES_DRAIN);
	/**
	 * @param role
	 * @param list
	 * 
	 */
	public SwarmPermissionsPanel(Role role) {
		super(role);
		
		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			if (permission.startsWith(Permissions.SWARM) || permission.startsWith(Permissions.STAR)){
				if (permission.equalsIgnoreCase(Permissions.SWARM_NODES_STAR) || permission.startsWith(Permissions.STAR)){
					nodesAll.setValue(true);
					for (int i=0; i<Permissions.NODES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.SWARM_NODES_ALL[i]);
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
		loadCheckBoxAction(nodesViewConfig);
		loadCheckBoxAction(nodesEditConfig);

		setItems(nodesAll, nodesStart, nodesDrain, nodesViewConfig, nodesEditConfig);
	}
	
	private class NodesAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (nodesAll.getValue()){
				if (!getRole().getPermissions().contains(Permissions.SWARM_NODES_STAR)) {
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						if (permission.startsWith(Permissions.SWARM)){
							iter.remove();
						}
					}
					for (int i=0; i<Permissions.SWARM_NODES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.SWARM_NODES_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					getRole().getPermissions().add(Permissions.SWARM_NODES_STAR);
				}
			} else {
				getRole().getPermissions().remove(Permissions.SWARM_NODES_STAR);
				for (int i=0; i<Permissions.SWARM_NODES_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.SWARM_NODES_ALL[i]);
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
		if (permission.equalsIgnoreCase(Permissions.SWARM_NODES_START)){
			return nodesStart;
		} else if (permission.equalsIgnoreCase(Permissions.SWARM_NODES_DRAIN)){
			return nodesDrain;
		} else if (permission.equalsIgnoreCase(Permissions.SWARM_NODES_VIEW_CONFIG)){
			return nodesViewConfig;
		} else if (permission.equalsIgnoreCase(Permissions.SWARM_NODES_EDIT_CONFIG)){
			return nodesEditConfig;
		} else if (permission.equalsIgnoreCase(Permissions.SWARM_NODES_STAR)){
			return nodesAll;
		} 
		return null;
	}
	
}