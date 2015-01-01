/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourceHeader;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * An inspector that let the user to view, modify and save an existing {@link Resource}
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class ExistingResourceInspector extends AbstractResourceInspector {

//	/**
//	 * Builds a {@link ExistingResourceInspector}
//	 * @param resource the underlying {@link Resource}
//	 */
//	public ExistingResourceInspector(Resource resource) {
//		this(resource, null);
//	}
//	
	/**
	 * Builds a {@link ExistingResourceInspector}
	 * @param resource the underlying {@link Resource}
	 * @param descriptor Resource descriptor
	 */
	public ExistingResourceInspector(Resource resource, ResourceDescriptor descriptor) {
		super(resource);
		createResourcePanel(descriptor);	
	}
	
	private void createResourcePanel(ResourceDescriptor descriptor){
		ResourcesPropertiesPanel resourcePanel = renderResourcePanel(descriptor); 
		resourcePanel.loadProperties();
		showResourcePanel(resourcePanel);
	}

	public final void showResourcePanel(ResourcesPropertiesPanel activePanel) {
		int newAvailableWidth = getAvailableWidth();
		int newAvailableHeight = getAvailableHeight();
		mainContainer.add(resourcePanelContainer);
		resourcePanelContainer.setWidget(activePanel);
		resourcePanelContainer.setSize(Sizes.toString(newAvailableWidth), Sizes.toString(newAvailableHeight));
		activePanel.onResize(newAvailableWidth, newAvailableHeight);
	}

	@Override
	public void save() {
		if (checkMandatoryAttributes() && validate()) {
			Services.COMMON_RESOURCES_MANAGER.updateCommonResource(getResource(), new ServiceAsyncCallback<Boolean>() {
	
				@Override
				public void onJemSuccess(Boolean result) {
					// do nothing
				}
	
				@Override
				public void onJemFailure(Throwable caught) {
					new Toast(MessageLevel.ERROR, caught.getMessage(), "Update resource command error!").show();
				}
				
				@Override
                public void onJemExecuted() {
					// hide the popup
					hide(); 
                }
			});
		}
	}

	@Override
	public FlexTable getHeader() {
		return new ResourceHeader("[" + getResource().getType() + "] " + getResource().getName(), this);
	}
	
}
