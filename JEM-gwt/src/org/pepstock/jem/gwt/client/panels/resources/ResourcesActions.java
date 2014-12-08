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
package org.pepstock.jem.gwt.client.panels.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * Component with buttons to perform actions on selected nodes.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ResourcesActions extends AbstractActionsButtonPanel<Resource> {
	
	/**
	 * 
	 */
    public ResourcesActions() {
	    super();
	    init();
    }

	@Override
	protected void initButtons() {
		addCreateButton();
		addRemoveButton();
		addCloneButton();
	}

	private void addCreateButton() {
		// checks if user has the permission to CREATE resource 
		if (ClientPermissions.isAuthorized(Permissions.RESOURCES, Permissions.RESOURCES_CREATE)) {
			Button createButton = new Button("New", new CreateButtonClickHandler());
			add(createButton);
			createButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(createButton, "Add a new resource");
		}
	}
	
	private class CreateButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			MultiSelectionModel<Resource> selectionModel = (MultiSelectionModel<Resource>) getUnderlyingTable().getTable().getSelectionModel();
			selectionModel.clear();

			// shows a popup to create new role
			ResourceTypeChooser inspector = new ResourceTypeChooser();
			inspector.center();
		}
	}
	
	private void addRemoveButton() {
		// checks if user has the permission to REMOVE resource 
		if (ClientPermissions.isAuthorized(Permissions.RESOURCES, Permissions.RESOURCES_DELETE)) {
			Button removeButton = new Button("Remove", new RemoveButtonClickHandler());
			add(removeButton);
			removeButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(removeButton, "Delete resources");
		}
	}
	
	private class RemoveButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			@SuppressWarnings("unchecked")
			final MultiSelectionModel<Resource> selectionModel = (MultiSelectionModel<Resource>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No resource is selected and it's not possible to perform REMOVE command.", "No resource selected!").show();
				return;
			}
			
			ConfirmMessageBox cd = new ConfirmMessageBox("Confirm REMOVE", "Are you sure you want to remove the selected common resource(s)?");
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
		// checks if user has the permission to CLONE resource 
		if (ClientPermissions.isAuthorized(Permissions.RESOURCES, Permissions.RESOURCES_CREATE)) {
			Button cloneButton = new Button("Clone", new CloneButtonClickHandler());
			add(cloneButton);
			cloneButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(cloneButton, "Duplicates a resource");
		}
	}

	private class CloneButtonClickHandler implements ClickHandler {
		@SuppressWarnings("unchecked")
        @Override
		public void onClick(ClickEvent event) {
			// gets the selected jobs
			final MultiSelectionModel<Resource> selectionModel = (MultiSelectionModel<Resource>) getUnderlyingTable().getTable().getSelectionModel();
			if (selectionModel.getSelectedSet().isEmpty()) {
				new Toast(MessageLevel.WARNING, "No resource is selected and it's not possible to perform CLONE command.", "No resource selected!").show();
				return;
			} else if (selectionModel.getSelectedSet().size() > 1) {
				new Toast(MessageLevel.WARNING, "Only one resource can be selected to perform CLONE command.", "Too many resources selected!").show();
				return;
			}
			
			// clone the resource
			Resource resource = selectionModel.getSelectedSet().iterator().next();
			Resource clone = new Resource();
			clone.setType(resource.getType());
			// create a deep copy of resource properties
			Map<String, ResourceProperty> clonedProperties = new HashMap<String, ResourceProperty>();
			clonedProperties.putAll(resource.getProperties());
			clone.setProperties(clonedProperties);
			
			Map<String, String> clonedCustomProperties = new HashMap<String, String>();
			clonedCustomProperties.putAll(resource.getCustomProperties());
			clone.setCustomProperties(clonedCustomProperties);
			
			selectionModel.clear();
			// shows a popup to create new role
			AbstractResourceInspector inspector = new CloneResourceInspector(clone);
			inspector.center();
			// adds itself to listener to refresh the lsit of roles seeing the new one if added
			selectionModel.clear();
		}
	}

	/**
	 * @param resources collection of resources to remove
	 */
	public void remove(final Collection<Resource> resources) {
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.COMMON_RESOURCES_MANAGER.removeCommonResource(resources, new RemoveCommonResourceAsyncCallback());
			}
	    });
	}

	private class RemoveCommonResourceAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			// if has success, refresh the data, to do not see in table that they are removed
			if (getSearcher() != null){
				getSearcher().refresh();
			}
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Remove resources command error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
}
