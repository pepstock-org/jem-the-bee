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

import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.roles.RoleInspector;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ActionsButtonPanel that user can do after a role updating o a new role adding. At the moment we have 2 actions:<br>
 * <br>
 * <ul>
 * <li><b>Save<b>: to consolidate the updates</li>
 * <li><b>Cancel<b>: to discard the updates</li>
 * </ul>
 * 
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public final class Actions extends HorizontalPanel {
	
	private static final int SPACING_DEFAULT = 4;

	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private Role role = null;
	
	private RoleInspector inspector = null;

	/**
	 * Creates the UI using the Role argument
	 * 
	 * @param role role to add or update
	 * 
	 */
	public Actions(Role role) {
		setSpacing(SPACING_DEFAULT);
		this.role = role;
		
		Button saveButton = new Button("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// do!
				getInspector().save();
			}
		});
		saveButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
		add(saveButton);

		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// do!
				getInspector().cancel();
			}
		});
		add(cancelButton);
		
		setCellVerticalAlignment(saveButton, ALIGN_MIDDLE);
		setCellVerticalAlignment(cancelButton, ALIGN_MIDDLE);
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

	/**
	 * @return the inspector
	 */
	public RoleInspector getInspector() {
		return inspector;
	}

	/**
	 * @param inspector the inspector to set
	 */
	public void setInspector(RoleInspector inspector) {
		this.inspector = inspector;
	}
	
	
}