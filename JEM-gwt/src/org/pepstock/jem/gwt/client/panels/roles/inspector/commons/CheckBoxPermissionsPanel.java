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
package org.pepstock.jem.gwt.client.panels.roles.inspector.commons;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.util.ColumnIndex;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CheckBoxPermissionsPanel extends ScrollPanel {

	private VerticalPanel main = new VerticalPanel();
	
	private Role role = null;

	/**
	 * @param role
	 * @param list
	 * 
	 */
	public CheckBoxPermissionsPanel(Role role) {
		this.role = role;
		main.setSpacing(10);
		add(main);
	}
	
	/**
	 * 
	 * @param items
	 */
	public void setItems(PermissionItem... items){
		FlexTable viewTable = new FlexTable();
		viewTable.setWidth(Sizes.HUNDRED_PERCENT);
		
		for (int i=0; i<items.length; i++){
			viewTable.setWidget(i,ColumnIndex.COLUMN_1, items[i]);
		}
		main.add(viewTable);
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}
	
	
	/**
	 * 
	 * @param item
	 */
	public void loadCheckBoxAction(final PermissionItem item){
		// sets actions for check box
		item.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// if checked and not in the list, adds to list
				if (item.getValue()){
					if (!getRole().getPermissions().contains(item.getPermission())) {
						getRole().getPermissions().add(item.getPermission());
					}
				} else {
					// if not checked, remove it
					getRole().getPermissions().remove(item.getPermission());
				}
			}
		});
	}
}
