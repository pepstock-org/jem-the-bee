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
package org.pepstock.jem.gwt.client.panels.roles.inspector;

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.ListPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.PermissionCell;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;


/**
 * Component to manage the  search permissions. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DatasourcePermissionsPanel extends ListPermissionsPanel {
	
	/**
	 * @param role
	 * @param mainPermission
	 */
    public DatasourcePermissionsPanel(Role role) {
	    super(role, Permissions.DATASOURCES, "Datasource pattern",  new PermissionCell(Images.INSTANCE.permission()));
    }

	/**
	 * Adds the permission from permissions list
	 * 
	 * @param selectedPermissionParm selected permission
	 */
	@Override
	public void add(String selectedPermissionParm) {
		String selectedPermission = selectedPermissionParm;
		// check if the user inserted "*". That's not a RegExp
		// so we changed in ".*"
		if ("*".equalsIgnoreCase(selectedPermission)){
			selectedPermission = ".*";
		}
		// if not in the list, adds!
		super.add(selectedPermission);	
	}
}