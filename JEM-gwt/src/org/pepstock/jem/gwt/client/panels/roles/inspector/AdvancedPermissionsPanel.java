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
package org.pepstock.jem.gwt.client.panels.roles.inspector;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.CheckBoxPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.PermissionItem;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.user.client.ui.ScrollPanel;


/**
 * Component to manage the advanced permissions. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class AdvancedPermissionsPanel extends ScrollPanel implements ResizeCapable {

	/**
	 * Constructs all UI using role instance information
	 * 
	 * @param role
	 * @param mainPermission
	 * 
	 */
	public AdvancedPermissionsPanel(Role role) {
		PermissionItem item = new PermissionItem("Internal Service", "Internal services necessary for Extended ANT utilities", Permissions.INTERNAL_SERVICES);
		
		PermissionItem item2 = new PermissionItem("Local File System Access", "Accessibility to local file system of node's machine", Permissions.LOCAL_FILE_SYSTEM_ACCESS);
	
		CheckBoxPermissionsPanel panel = new CheckBoxPermissionsPanel(role);
		panel.setItems(item, item2);
		panel.loadCheckBoxAction(item);
		panel.loadCheckBoxAction(item2);
		
		add(panel);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
    }
}