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
package org.pepstock.jem.gwt.client.panels.roles;

import java.util.ArrayList;
import java.util.Collection;

import org.pepstock.jem.gwt.client.commons.ConfirmMessageBox;
import org.pepstock.jem.gwt.client.commons.HideHandler;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.PreferredButton;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.panels.components.AbstractActionsButtonPanel;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.Roles;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * Component with buttons to perform actions on selected nodes.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RolesActions extends AbstractActionsButtonPanel<Role> {

	
	
	/**
	 * 
	 */
    public RolesActions() {
	    super();
	    init();
    }

	@Override
	protected void initButtons() {
		addCreateButton();
		addDeleteButton();
		addCloneButton();
	}

	private void addCreateButton() {
		// checks if user has the permission to CREATE job 
		if (ClientPermissions.isAuthorized(Permissions.ROLES, Permissions.ROLES_CREATE)) {
			Button createButton = new Button("New", new CreateButtonClickHandler());
			add(createButton);
			createButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(createButton, "Creates a role");
		}
	}
	
	private class CreateButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			MultiSelectionModel<Role> selectionModel = (MultiSelectionModel<Role>) getUnderlyingTable().getTable().getSelectionModel();
			selectionModel.clear();

			// shows a popup to create new role
			RoleInspector inspector = new RoleInspector();
			inspector.center();
			// adds itself to listener to refresh the lsit of roles seeing teh new one if added
			inspector.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> arg0) {
					getSearcher().refresh();
				}
			});

		}
	}
	
	
	private void addDeleteButton() {
		// checks if user has the permission to REMOVE job 
		if (ClientPermissions.isAuthorized(Permissions.ROLES, Permissions.ROLES_DELETE)) {
			Button removeButton = new Button("Remove", new RemoveButtonClickHandler());
			add(removeButton);
			removeButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(removeButton, "Delete roles");
		}
	}

	private class RemoveButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			final MultiSelectionModel<Role> selectionModel = (MultiSelectionModel<Role>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No role is selected and it's not possible to perform REMOVE command.", "No role selected!").show();
				return;
			}
	
			
			ConfirmMessageBox cd = new ConfirmMessageBox("Confirm REMOVE", "Are you sure you want to remove the selected roles?");
	        cd.setHideHandler(new HideHandler() {
				@Override
				public void onHide(PreferredButton button) {
			        if (button.getAction() == PreferredButton.YES_ACTION){
						// do!
						remove(selectionModel.getSelectedSet());
						// clear selection
						selectionModel.clear();
			        }
				}
			});
			cd.open();
		}
	}
	
	private void addCloneButton() {
		// checks if user has the permission to CLONE job 
		if (ClientPermissions.isAuthorized(Permissions.ROLES, Permissions.ROLES_CREATE)) {
			Button cloneButton = new Button("Clone", new CloneButtonClickHandler());
			add(cloneButton);
			cloneButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(cloneButton, "Duplicates a role");
		}
	}

	private class CloneButtonClickHandler implements ClickHandler {
		@SuppressWarnings("unchecked")
        @Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			final MultiSelectionModel<Role> selectionModel = (MultiSelectionModel<Role>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No role is selected and it's not possible to perform CLONE command.", "No role selected!").show();
				return;
			} else if (selectionModel.getSelectedSet().size() > 1) {
				new Toast(MessageLevel.WARNING, "Only one role must be selected otherwise it's not possible to perform CLONE command.", "Too many roles selected!").show();
				return;
			}
			Role source = selectionModel.getSelectedSet().iterator().next();
			if (source.getName().equalsIgnoreCase(Roles.ADMINISTRATOR_ROLE.getName())){
				selectionModel.clear();
				new Toast(MessageLevel.ERROR, "'"+Roles.ADMINISTRATOR_ROLE.getName()+"' cannot be cloned. Please creates a new ones and clone that.", "'"+Roles.ADMINISTRATOR_ROLE.getName()+"' cannot be cloned!").show();
				return;
			}
			Role clone = new Role();
			clone.setPermissions(new ArrayList<String>(source.getPermissions()));
			clone.setRemovable(true);
			clone.setUsers(new ArrayList<String>(source.getUsers()));
			selectionModel.clear();
			
			// shows a popup to create new role
			RoleInspector inspector = new RoleInspector(clone, RoleInspector.CLONE);
			inspector.center();
			// adds itself to listener to refresh the lsit of roles seeing teh new one if added
			inspector.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> arg0) {
					getSearcher().refresh();
				}
			});
			selectionModel.clear();
		}
	}

	/**
	 * @param roles collection of roles to remove
	 */
	public void remove(final Collection<Role> roles) {
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.ROLES_MANAGER.removeRole(roles, new RemoveRoleAsyncCallback());
			}
	    });
	}
	
	private class RemoveRoleAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			// if has success, refresh the data, to do not see in table that they are removed
			if (getSearcher() != null){
				getSearcher().refresh();
			}
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Remove role command error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
}
