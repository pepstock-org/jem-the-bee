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
package org.pepstock.jem.gwt.client.panels.roles;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.roles.inspector.Actions;
import org.pepstock.jem.gwt.client.panels.roles.inspector.AdvancedPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.DatasourcePermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.FilePermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.NewRoleHeader;
import org.pepstock.jem.gwt.client.panels.roles.inspector.PermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.RoleHeader;
import org.pepstock.jem.gwt.client.panels.roles.inspector.SearchPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.SurrogatePermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.UsersPanel;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Componet which allows to update a role or insert a new one. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public final class RoleInspector extends AbstractTabPanelInspector {
	
	@SuppressWarnings("javadoc")
    public static final int NEW = 0, UPDATE = 1, CLONE = 2;
	
	private int action = NEW;
	
	private Role role = null;
	
	private TabPanel mainPanel = new TabPanel();
	
	private Actions actions = null;
	
	
	/**
	 * Constructs a new component to add a new role
	 */
	public RoleInspector(){
		this(new Role(), NEW);
		getRole().setRemovable(true);
	}
	/**
	 * Constructs a new component to update the role, passed as argument
	 * 
	 * @param role role to update
	 */
	public RoleInspector(Role role){
		this(role, UPDATE);
	}

	/**
	 * Constructs privately a new component to manage the role
	 * 
	 * @param role role to manage
	 * @param action 
	 */
	public RoleInspector(Role role, int action) {
		super(true);
		// saves arguments
		this.role = role;
		this.action = action;
		
		if (role.isRemovable()){
			mainPanel.add(new PermissionsPanel(role), "Views and Actions");
		}
		// checks is not admin
		if (!role.getPermissions().contains(Permissions.STAR)){
			mainPanel.add(new SearchPermissionsPanel(role), "Searches");
			mainPanel.add(new DatasourcePermissionsPanel(role), "Datasources");
			mainPanel.add(new FilePermissionsPanel(role), "Files");
			mainPanel.add(new SurrogatePermissionsPanel(role), "Surrogate");
			mainPanel.add(new AdvancedPermissionsPanel(role), "Advanced");
		}
		
		UsersPanel usersPanel = new UsersPanel(role);
		mainPanel.add(usersPanel, "Users");
		
		mainPanel.setWidth(Sizes.HUNDRED_PERCENT);
		
		actions = new Actions(role);
		actions.setInspector(this);

		mainPanel.selectTab(0);
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
	 * @return the action
	 */
	public int getAction() {
		return action;
	}
	
	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}
	
	/**
	 * Cancel if pressed, so hide the popup
	 */
	public void cancel() {
		hide();
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getHeader()
	 */
    @Override
    public FlexTable getHeader() {
		// if is for a new role, create a different header (with text field)
		if (getAction() == UPDATE) {
			return new RoleHeader(getRole().getName(), this);
		} else {
			return new NewRoleHeader(getRole(), this);
		}
    }
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getCenter()
	 */
    @Override
    public TabPanel getTabPanel() {
	    return mainPanel;
    }
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getActions()
	 */
    @Override
    public CellPanel getActions() {
	    return actions;
    }

	/**
	 * save the updated or new role
	 */
	public void save() {
		if (role.getName() != null) {
			if (!role.getName().trim().isEmpty()) {
				
				Loading.startProcessing();
				
			    Scheduler scheduler = Scheduler.get();
			    scheduler.scheduleDeferred(new ScheduledCommand() {
					
					@Override
					public void execute() {
						// checks if the name is valid for new role
						if (getAction() != UPDATE) {
							Services.ROLES_MANAGER.addRole(role, new AddRoleAsyncCallback());

						} else {
							Services.ROLES_MANAGER.updateRole(role, new UpdateRoleAsyncCallback());
						}
					}
			    });

			} else {
				new Toast(MessageLevel.ERROR, "Role name is empty. Please type a Role name", "Role name empty!").show();
			}
		} else {
			new Toast(MessageLevel.ERROR, "Please type a valid Role name", "Invalid role name!").show();
		}
	}

	private class AddRoleAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			// do nothing
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Add role command error!").show();
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			// hide the popup
			hide();
        }
	}

	private class UpdateRoleAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			// do nothing
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Update role command error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			// hide the popup
			hide();
        }
	}

}