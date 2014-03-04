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
package org.pepstock.jem.gwt.client.panels.roles.inspector;

import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.ListPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.PermissionCell;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Component to manage the  search permissions. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class FilePermissionsPanel extends ListPermissionsPanel {
	
	private final RadioButton read = new RadioButton("domain", "Read permit");
	
	private final RadioButton write = new RadioButton("domain", "Write permit");
	
	private final RadioButton execute = new RadioButton("domain", "Execute permit");
	
	private final RadioButton all = new RadioButton("domain", "All permit");

	/**
	 * Constructs all UI using role instance information
	 * 
	 * @param role
	 * 
	 */
	public FilePermissionsPanel(Role role) {
		super(role, Permissions.FILES, "Files name pattern",  new PermissionCell(Images.INSTANCE.permission()));
		all.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getInputPanel().setEnabled(false);
			}
		});
		
		getInputPanel().add(read);
		read.setValue(true);
		
		getInputPanel().add(write);
		getInputPanel().add(execute);
		getInputPanel().add(all);

	}

	/**
	 * Adds the permission from permissions list
	 * 
	 * @param selectedPermission selected permission
	 */
	@Override
	public void add(String selectedPermissionParm) {
		String selectedPermission = selectedPermissionParm;
		// check if the user inserted "*". That's not a RegExp
		// so we changed in ".*"
		if ("*".equalsIgnoreCase(selectedPermission)){
			selectedPermission = ".*";
		}
		// creates the complete permission, adding the prefix based on 
		// the radio button checked
		String permission = null;
		if (all.getValue()){
			permission = Permissions.FILES_STAR;
		} else if (read.getValue()){
			permission = Permissions.FILES_READ;
			permission = permission + selectedPermission;
		} else if (write.getValue()){
			permission = Permissions.FILES_WRITE;
			permission = permission + selectedPermission;
		} else {
			permission = Permissions.FILES_EXECUTE;
			permission = permission + selectedPermission;
		}
		// creates the complete permission, adding the prefix based on 
		// the radiobutton checked
		super.add(permission);
	}
}