package org.pepstock.jem.gwt.client.panels.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.resources.inspector.NewResourceHeader;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.custom.ResourceDescriptor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * An inspector that let the user to define (and save) a brand-new {@link Resource}
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class NewResourceInspector extends AbstractResourceInspector {

	 // the "select" one, plus concrete ootb types
	private static int FIRST_CUSTOM_RESOURCE_INDEX = 7;
	private static final int LIST_HEIGHT = 40;

	private HorizontalPanel typePanel = new HorizontalPanel(); 
	private ListBox typeCombo = new ListBox();
	private Collection<String> customResourceNames = null;
	
	/**
	 * Builds a {@link NewResourceInspector}
	 */
	public NewResourceInspector() {
		super(new Resource());
		loadCustomResourceNames();
	}
	
	private void buildTypeCombo() {
		typePanel.setHeight(Sizes.toString(LIST_HEIGHT));

		// add ootb names
		typeCombo.addStyleName(Styles.INSTANCE.common().bold());
		typeCombo.addItem("Select a Resource Type");
		typeCombo.addItem(JDBC);
		typeCombo.addItem(FTP);
		typeCombo.addItem(JMS);
		typeCombo.addItem(HTTP);
		typeCombo.addItem(JPPF);
		typeCombo.addItem(JEM);

		// add custom names, if any
		if (customResourceNames != null) {
			List<String >customResourceNameList = new ArrayList<String>(customResourceNames);
			Collections.sort(customResourceNameList);
			for (String type : customResourceNameList) {
				typeCombo.addItem(type);
			}
		}

		// add combo type to main panel
		typePanel.add(typeCombo);
		typePanel.setCellVerticalAlignment(typeCombo, HasVerticalAlignment.ALIGN_MIDDLE);
		typePanel.setSpacing(Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT);
		mainContainer.add(typePanel);

		// when i select a resource type, i need to know if it's an ootb resource or if i need to load a custom resource descriptor 
		typeCombo.addChangeHandler(new TypeComboChangeHandler());
	}

	private class TypeComboChangeHandler implements ChangeHandler {
		@Override
		public void onChange(ChangeEvent event) {
			int selectedIndex = typeCombo.getSelectedIndex();
			if(selectedIndex > 0) {
				// a resource name is selected
				typeCombo.setEnabled(false);
				String selectedResourceType = typeCombo.getItemText(selectedIndex); 
				
				if (selectedIndex < FIRST_CUSTOM_RESOURCE_INDEX) {
					// a ootb resource is selected
					ResourcesPropertiesPanel ootbPanel = renderOOTBResourcePanel(selectedResourceType);
					showResourcePanel(ootbPanel);
				} else {
					// a custom resource index is selected, i need to load appropriate descriptor and render panel
					Services.CUSTOM_RESOURCE_DEFINITIONS_MANAGER.getDescriptorOf(selectedResourceType, new ServiceAsyncCallback<ResourceDescriptor>() {

						@Override
						public void onJemFailure(Throwable caught) {
							new Toast(MessageLevel.ERROR, "Unable to load cutom resource definition: " + caught.getMessage(), "Custom Resource Definitions Error!").show();
							// re-enable the selection combo
							typeCombo.setEnabled(true);
						}

						@Override
						public void onJemSuccess(ResourceDescriptor descriptor) {
							ResourcesPropertiesPanel customPanel = renderCustomResourcePanel(descriptor);
							showResourcePanel(customPanel);
						}
						
						@Override
	                    public void onJemExecuted() {
							// do nothing
	                    }
					});
				}
			}
		}
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

	private final void loadCustomResourceNames() {
		Services.CUSTOM_RESOURCE_DEFINITIONS_MANAGER.getAllResourceNames(new ServiceAsyncCallback<Collection<String>>() {
			@Override
			public void onJemFailure(Throwable caught) {
				new Toast(MessageLevel.ERROR, "Unable to load cutom resource definitions: " + caught.getMessage(), "Custom Resource Definitions Error!").show();
				// always build the type combo, at least for ootb types only
				onSuccess(null);
			}

			@Override
			public void onJemSuccess(Collection<String> result) {
				customResourceNames = result;
				buildTypeCombo();
			}
			
			@Override
            public void onJemExecuted() {
				// do nothing
            }
		});
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

}
