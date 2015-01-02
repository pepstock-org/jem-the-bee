/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.resources.inspector.NewResourceHeader;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;

/**
 * An inspector that let the user to define (and save) a brand-new {@link Resource}
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class NewResourceInspector extends AbstractResourceInspector implements ResizeCapable {

	private static final int LIST_HEIGHT = 40;
	
	private HTML type = null; 
	
	private ResourceDescriptor descriptor = null;

	/**
	 * Builds a {@link NewResourceInspector}
	 * @param descriptor resorce descriptor
	 */
	public NewResourceInspector(ResourceDescriptor descriptor) {
		super(new Resource(descriptor.getType()));
		this.descriptor = descriptor;
		
		type = new HTML("<span style='padding: 0px 0px 0px 6px;'>Resource type: <b><span style='font-size: 1.5em;'>"+descriptor.getType()+"</span><b></span>");
		type.setHeight(Sizes.toString(LIST_HEIGHT));

		mainContainer.add(type);
		ResourcesPropertiesPanel customPanel = renderResourcePanel(this.descriptor);
		showResourcePanel(customPanel);
	}
	
	@Override
	public void showResourcePanel(ResourcesPropertiesPanel activePanel) {
		// calculate sizes
		int availableWidth = getAvailableWidth();
		int availableHeight = getAvailableHeight() - LIST_HEIGHT;
		// init resource
		activePanel.initializeResource();
		// add panel container top main panel
		mainContainer.add(resourcePanelContainer);
		// set the panel to panel container
		resourcePanelContainer.setWidget(activePanel);
		// resize the panel container
		resourcePanelContainer.setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
		// resize the panel itself
		activePanel.onResize(availableWidth, availableHeight);
	}

	@Override
	public void save() {
		if (checkMandatoryAttributes() && validate()) {
			// checks if the name is valid for new resource
			if (getResource().getName() != null && !getResource().getName().trim().isEmpty()) {
				Services.COMMON_RESOURCES_MANAGER.addCommonResource(getResource(), new ServiceAsyncCallback<Boolean>() {
					@Override
					public void onJemSuccess(Boolean result) {
						// do nothing
					}

					@Override
					public void onJemFailure(Throwable caught) {
						new Toast(MessageLevel.ERROR, caught.getMessage(), "Add resource command error!").show();
					}
					
					@Override
		            public void onJemExecuted() {
						// hide the popup
						hide(); 
		            }
				});
			} else {
				new Toast(MessageLevel.ERROR, "Please type a valid not-empty resource name", "Invalid resource name!").show();
			}
		}
	}

	@Override
	public FlexTable getHeader() {
		return new NewResourceHeader(getResource(), this);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	// do nothing
    }
}
