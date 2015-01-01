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

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ChooserActions;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourceDescriptorsList;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourceHeader;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Componet which allows to update a resource or insert a new one. 
 * 
 * @author Andrea "Stock" Stocchero
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class ResourceTypeChooser extends AbstractInspector implements InspectListener<ResourceDescriptor> {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private ChooserActions actionsPanel = new ChooserActions();
	
	protected VerticalPanel mainContainer = new VerticalPanel();	
	protected SimplePanel resourcePanelContainer = new SimplePanel();
	
	private ResourceDescriptorsList list = new ResourceDescriptorsList();
	
	private ResourceDescriptor descriptor = null;
	
	/**
	 * Constructs privately a new component to manage the resource
	 * 
	 * @param resource resource to manage
	 * @param action 
	 */
	public ResourceTypeChooser() {
		super(true);
		buildExistingPanel();
	}
	
	private final void buildExistingPanel() {
		// obtain the ootb specific panel, if the resource is a type, it will be null 
		// it's a resource, need to load the descriptor and render the corrisponding panel
		Services.RESOURCE_DEFINITIONS_MANAGER.getAllResourceDescriptors(new ServiceAsyncCallback<Collection<ResourceDescriptor>>() {
			@Override
			public void onJemFailure(Throwable caught) {
				new Toast(MessageLevel.ERROR, "Unable to load cutom resource definition: " + caught.getMessage(), "Custom Resource Definitions Error!").show();
			}

			@Override
			public void onJemSuccess(Collection<ResourceDescriptor> descriptors) {
				list.setRowData(descriptors); 
				showResourcePanel(list);
			}

			@Override
			public void onJemExecuted() {
				// do nothing
			}

		});
	}
	
	/**
	 * Showes resource panel
	 * @param list list of resoyrce types to show
	 */
	public final void showResourcePanel(ResourceDescriptorsList list) {
		int newAvailableWidth = getAvailableWidth();
		int newAvailableHeight = getAvailableHeight();
		mainContainer.add(resourcePanelContainer);
		resourcePanelContainer.setWidget(list);
		resourcePanelContainer.setSize(Sizes.toString(newAvailableWidth), Sizes.toString(newAvailableHeight));
		list.onResize(newAvailableWidth, newAvailableHeight);
		
		list.setListener(this);
		actionsPanel.setInspector(this);
	}


    @Override
    public FlexTable getHeader(){
    	return new ResourceHeader("Choose resource type", this);
    }
	
    @Override
    public Panel getContent() {
	    return mainContainer;
    }

    @Override
    public Panel getActions() {
	    return actionsPanel;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(ResourceDescriptor object) {
    	this.descriptor = object;
    }
    
    /**
     * Called by the action panel
     */
    public void ok(){
		if (descriptor == null) {
			new Toast(MessageLevel.WARNING, "No resoure type is selected.", "No resource type!").show();
			return;
		}
    	
    	hide();
    	AbstractResourceInspector inspector = new NewResourceInspector(descriptor);
    	inspector.center();
    }

    /**
     * Called by the action panel
     */
    public void cancel(){
    	hide();
    }
    
}