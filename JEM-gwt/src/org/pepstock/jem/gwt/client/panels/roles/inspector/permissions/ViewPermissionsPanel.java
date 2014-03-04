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
public class ViewPermissionsPanel extends CheckBoxPermissionsPanel {

	// creates all check boxes
	private PermissionItem viewAll = new PermissionItem("All", "allows to see all views",Permissions.VIEW_STAR);
	private PermissionItem viewInput = new PermissionItem("Input","allows to have INPUT queue view", Permissions.VIEW_INPUT);
	private PermissionItem viewRunning = new PermissionItem("Running","allows to have RUNNING queue view", Permissions.VIEW_RUNNING);
	private PermissionItem viewOutput = new PermissionItem("Output", "allows to have OUTPUT queue view", Permissions.VIEW_OUTPUT);
	private PermissionItem viewRouting = new PermissionItem("Routing","allows to have ROUTING queue view", Permissions.VIEW_ROUTING);
	private PermissionItem viewAdmin = new PermissionItem( "Administration", "allows to have ADMINISTRATION view", Permissions.VIEW_ADMIN);
	private PermissionItem viewNodes = new PermissionItem("Nodes", "allows to have NODES list view, to manage them", Permissions.VIEW_NODES);
	private PermissionItem viewSwarm = new PermissionItem("Swarm", "allows to have SWARM node list view, to manage them", Permissions.VIEW_SWARM_NODES);
	private PermissionItem viewRoles = new PermissionItem("Roles","allows to have ROLES list view, to manage them", Permissions.VIEW_ROLES);
	private PermissionItem viewResources = new PermissionItem("Resources", "allows to have RESOURCES list view, to manage them", Permissions.VIEW_RESOURCES);
	private PermissionItem viewStatus = new PermissionItem("Status","allows to have STATUS queue view, to search a single job on queues", Permissions.VIEW_STATUS);
	private PermissionItem viewGfs = new PermissionItem("GFS explorer", "allows to have GFS explorer view, to navugate on GFS seeing existing files", Permissions.VIEW_GFS_EXPLORER);
	
	/**
	 * @param role
	 * @param list
	 * 
	 */
	public ViewPermissionsPanel(Role role) {
		super(role);

		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			if (permission.startsWith(Permissions.VIEW) || permission.startsWith(Permissions.STAR)){
				if (permission.equalsIgnoreCase(Permissions.VIEW_STAR) || permission.startsWith(Permissions.STAR)){
					viewAll.setValue(true);
					for (int i=0; i<Permissions.VIEW_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.VIEW_ALL[i]);
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
		viewAll.addClickHandler(new ViewAllClickHandler());
		
		loadCheckBoxAction(viewInput);
		loadCheckBoxAction(viewRunning);
		loadCheckBoxAction(viewOutput);
		loadCheckBoxAction(viewRouting);
		loadCheckBoxAction(viewStatus);
		loadCheckBoxAction(viewGfs);
		loadCheckBoxAction(viewAdmin);
		loadCheckBoxAction(viewNodes);
		loadCheckBoxAction(viewSwarm);
		loadCheckBoxAction(viewRoles);
		loadCheckBoxAction(viewResources);

		setItems(viewAll, viewInput, viewRunning, viewOutput, viewRouting, viewStatus, viewNodes, viewSwarm, viewRoles, viewResources, viewGfs, viewAdmin);
	}

	private class ViewAllClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (viewAll.getValue()){
				if (!getRole().getPermissions().contains(Permissions.VIEW_STAR)) {
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						if (permission.startsWith(Permissions.VIEW)){
							iter.remove();
						}
					}
					for (int i=0; i<Permissions.VIEW_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.VIEW_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					getRole().getPermissions().add(Permissions.VIEW_STAR);
				}
			} else {
				getRole().getPermissions().remove(Permissions.VIEW_STAR);
				for (int i=0; i<Permissions.VIEW_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.VIEW_ALL[i]);
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
		if (permission.equalsIgnoreCase(Permissions.VIEW_INPUT)){
			return viewInput;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_RUNNING)){
			return viewRunning;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_OUTPUT)){
			return viewOutput;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_ROUTING)){
			return viewRouting;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_STATUS)){
			return viewStatus;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_GFS_EXPLORER)){
			return viewGfs;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_ADMIN)){
			return viewAdmin;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_ROLES)){
			return viewRoles;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_RESOURCES)){
			return viewResources;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_NODES)){
			return viewNodes;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_SWARM_NODES)){
			return viewSwarm;
		} else if (permission.equalsIgnoreCase(Permissions.VIEW_STAR)){
			return viewAll;
		} 
		return null;
	}
	
}