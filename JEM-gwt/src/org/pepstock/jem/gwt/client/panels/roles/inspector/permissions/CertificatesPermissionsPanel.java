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
public class CertificatesPermissionsPanel extends CheckBoxPermissionsPanel {

	// creates all check boxes
	private PermissionItem certificatesAll = new PermissionItem("All", "allows to have all actions on certificates management", Permissions.CERTIFICATES_STAR);
	private PermissionItem certificatesCreate = new PermissionItem("Read","allows to have READ permission, to see certificates attributes", Permissions.CERTIFICATES_READ);
	private PermissionItem certificatesDelete = new PermissionItem("Delete","allows to have DELETE permission, to remove certificates", Permissions.CERTIFICATES_DELETE);
	private PermissionItem certificatesRead = new PermissionItem("Create", "allows to have CREATE permission, to create new certificates", Permissions.CERTIFICATES_CREATE);
	/**
	 * @param role
	 * @param list
	 * 
	 */
	public CertificatesPermissionsPanel(Role role) {
		super(role);
		
		// initialize the check boxes using the role permissions
		for (String permission : role.getPermissions()){
			if (permission.startsWith(Permissions.CERTIFICATES) || permission.startsWith(Permissions.STAR)){
				if (permission.equalsIgnoreCase(Permissions.CERTIFICATES_STAR) || permission.startsWith(Permissions.STAR)){
					certificatesAll.setValue(true);
					for (int i=0; i<Permissions.CERTIFICATES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.CERTIFICATES_ALL[i]);
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
		certificatesAll.addClickHandler(new CertificatesAllClickHanlder());

		loadCheckBoxAction(certificatesRead);
		loadCheckBoxAction(certificatesDelete);
		loadCheckBoxAction(certificatesCreate);

		setItems(certificatesAll, certificatesRead, certificatesDelete, certificatesCreate);
	}

	private class CertificatesAllClickHanlder implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (certificatesAll.getValue()){
				if (!getRole().getPermissions().contains(Permissions.CERTIFICATES_STAR)) {
					for (Iterator<String> iter = getRole().getPermissions().iterator(); iter.hasNext();){
						String permission = iter.next();
						if (permission.startsWith(Permissions.CERTIFICATES)){
							iter.remove();
						}
					}
					for (int i=0; i<Permissions.CERTIFICATES_ALL.length; i++){
						PermissionItem checkBox = getPermissionItemByPermission(Permissions.CERTIFICATES_ALL[i]);
						if (checkBox != null){
							checkBox.setValue(false);
							checkBox.setEnabled(false);
						}
					}
					getRole().getPermissions().add(Permissions.CERTIFICATES_STAR);
				}
			} else {
				getRole().getPermissions().remove(Permissions.CERTIFICATES_STAR);
				for (int i=0; i<Permissions.CERTIFICATES_ALL.length; i++){
					PermissionItem checkBox = getPermissionItemByPermission(Permissions.CERTIFICATES_ALL[i]);
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
		if (permission.equalsIgnoreCase(Permissions.CERTIFICATES_CREATE)){
			return certificatesCreate;
		} else if (permission.equalsIgnoreCase(Permissions.CERTIFICATES_DELETE)){
			return certificatesDelete;
		} else if (permission.equalsIgnoreCase(Permissions.CERTIFICATES_READ)){
			return certificatesRead;
		} else if (permission.equalsIgnoreCase(Permissions.CERTIFICATES_STAR)){
			return certificatesAll;
		} 
		return null;
	}
	
}