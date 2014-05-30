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
package org.pepstock.jem.gwt.client.panels;

import java.util.Collection;

import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.components.BasePanel;
import org.pepstock.jem.gwt.client.panels.components.CommandPanel;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.gwt.client.panels.roles.RoleInspector;
import org.pepstock.jem.gwt.client.panels.roles.RolesActions;
import org.pepstock.jem.gwt.client.panels.roles.RolesSearcher;
import org.pepstock.jem.gwt.client.panels.roles.RolesTable;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Main panel of roles manager. Shows the list of roles defined with the possibilities to act on them. 
 * Furthermore allows to inspect the role to change it.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Roles extends BasePanel<Role> implements SearchListener, InspectListener<Role> {
	
	/**
	 * Constructs all UI 
	 */
	public Roles() {
		super(new TableContainer<Role>(new RolesTable()),
			new CommandPanel<Role>(new RolesSearcher(), new RolesActions()));
		getTableContainer().getUnderlyingTable().setInspectListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see test.client.main.JobsSearchListener#search(java.lang.String)
	 */
	@Override
	public void search(final String filter) {
		if (ClientPermissions.isAuthorized(Permissions.ROLES, Permissions.ROLES_READ)) {
			getCommandPanel().getSearcher().setEnabled(false);
			Loading.startProcessing();
		    Scheduler scheduler = Scheduler.get();
		    scheduler.scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					// asks for roles
					Services.ROLES_MANAGER.getRoles(filter, new GetRolesAsyncCallback());
				}
		    });

		}
	}

	private class GetRolesAsyncCallback extends ServiceAsyncCallback<Collection<Role>> {
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Search error!").show();
			getCommandPanel().getSearcher().setFirstSearch(true);
		}

		@Override
		public void onJemSuccess(Collection<Role> result) {
			// sets data to table to show it
			getTableContainer().getUnderlyingTable().setRowData(result);
			getCommandPanel().getSearcher().setFirstSearch(false);
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			getCommandPanel().getSearcher().setEnabled(true);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.RoleInspectListener#inspect(org.pepstock.jem.gwt.client.security.Role)
	 */
    @Override
    public void inspect(Role role) {
    	if (ClientPermissions.isAuthorized(Permissions.ROLES, Permissions.ROLES_UPDATE)) {
    		// goes inspect in teh role
    		RoleInspector inspector = new RoleInspector(role);
    		inspector.setTitle(role.getName());
    		inspector.center();

    		// adds itself listener for closing and refreshing the data
    		inspector.addCloseHandler(new CloseHandler<PopupPanel>() {
    			@Override
    			public void onClose(CloseEvent<PopupPanel> arg0) {
    				getCommandPanel().getSearcher().refresh();
    			}
    		});
    	}
    }
}