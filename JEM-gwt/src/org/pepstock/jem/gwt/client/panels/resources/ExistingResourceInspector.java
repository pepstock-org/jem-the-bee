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

	/**
	 * Builds a {@link ExistingResourceInspector}
	 * @param resource the underlying {@link Resource}
	 */
	public ExistingResourceInspector(Resource resource) {
		super(resource);
		buildExistingPanel();
	}

	private final void buildExistingPanel() {
		// obtain the ootb specific panel, if the resource is a custom type, it will be null 
		// it's a custom resource, need to load the descriptor and render the corrisponding panel
		Services.RESOURCE_DEFINITIONS_MANAGER.getDescriptorOf(getResource().getType(), new ServiceAsyncCallback<ResourceDescriptor>() {
			@Override
			public void onJemFailure(Throwable caught) {
				new Toast(MessageLevel.ERROR, "Unable to load cutom resource definition: " + caught.getMessage(), "Custom Resource Definitions Error!").show();
			}

			@Override
			public void onJemSuccess(ResourceDescriptor descriptor) {
				ResourcesPropertiesPanel resourcePanel = renderResourcePanel(descriptor); 
				resourcePanel.loadProperties();
				showResourcePanel(resourcePanel);
			}

			@Override
			public void onJemExecuted() {
				// do nothing
			}
		});
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
