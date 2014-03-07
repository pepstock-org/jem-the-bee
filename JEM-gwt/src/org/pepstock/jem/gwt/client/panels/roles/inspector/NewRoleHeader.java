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
import org.pepstock.jem.gwt.client.panels.components.NewObjectHeader;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Header component for role adding. A text field is used to add Role name
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class NewRoleHeader extends NewObjectHeader {

	private Role role = null;
	
	/**
	 * Creates the header with text field to assign the role name to role empty object passed by argument
	 * @param role new role to fill with all necessary data
	 * @param parent 
	 */
	public NewRoleHeader(Role role, PopupPanel parent) {
		super(Images.INSTANCE.roles(), "Type here the new Role name...", parent);
		this.role = role;
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public void onNameTyped(String name) {
		role.setName(name);
	}
	
}