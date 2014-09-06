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


import java.util.HashMap;
import java.util.Map;

import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.resources.inspector.Actions;
import org.pepstock.jem.gwt.client.panels.resources.inspector.FTPAttributesPanel;
import org.pepstock.jem.gwt.client.panels.resources.inspector.HTTPAttributesPanel;
import org.pepstock.jem.gwt.client.panels.resources.inspector.JDBCAttributesPanel;
import org.pepstock.jem.gwt.client.panels.resources.inspector.JEMAttributesPanel;
import org.pepstock.jem.gwt.client.panels.resources.inspector.JMSAttributesPanel;
import org.pepstock.jem.gwt.client.panels.resources.inspector.JPPFAttributesPanel;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.gwt.client.panels.resources.inspector.custom.WidgetFactory;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.custom.ResourceDescriptor;

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
public abstract class AbstractResourceInspector extends AbstractInspector {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	protected static final String JDBC = "JDBC";
	protected static final String FTP = "FTP";
	protected static final String JMS = "JMS";
	protected static final String HTTP = "HTTP";
	protected static final String JPPF = "JPPF";
	protected static final String JEM = "JEM";
	
	protected static final Map<String, Class<? extends ResourcesPropertiesPanel>> RESOURCES_TYPES = new HashMap<String, Class<? extends ResourcesPropertiesPanel>>();
	static {
		RESOURCES_TYPES.put(JDBC, JDBCAttributesPanel.class);
		RESOURCES_TYPES.put(FTP, FTPAttributesPanel.class);
		RESOURCES_TYPES.put(JMS, JMSAttributesPanel.class);
		RESOURCES_TYPES.put(HTTP, HTTPAttributesPanel.class);
		RESOURCES_TYPES.put(JPPF, JPPFAttributesPanel.class);
		RESOURCES_TYPES.put(JEM, JEMAttributesPanel.class);
	}
	
	private Resource resource = null;

	protected VerticalPanel mainContainer = new VerticalPanel();	
	protected SimplePanel resourcePanelContainer = new SimplePanel();
	private Actions actionsPanel = null;
	
	/**
	 * Constructs privately a new component to manage the resource
	 * 
	 * @param resource resource to manage
	 * @param action 
	 */
	public AbstractResourceInspector(Resource resource) {
		super(true);
		this.resource = resource;
		// adds ActionsButtonPanel
		actionsPanel = new Actions(resource);
		actionsPanel.setInspector(this);
	}

	/**
	 * Show the resource panel passed as patameter. The concrete implementations should handle the sizes.
	 * @param activePanel the panel to be shown
	 */
	public abstract void showResourcePanel(ResourcesPropertiesPanel activePanel);
	
	/**
	 * Cancel if pressed, so hide the popup
	 */
	public void cancel() {
		hide();
	}

	/**
	 * Render the panel of a OOTB resource 
	 * @param ootbResourceType the type of the resource panels
	 * @return a {@link ResourcesPropertiesPanel}
	 */
	public ResourcesPropertiesPanel renderOOTBResourcePanel(String ootbResourceType) {
		ResourcesPropertiesPanel ootbPanel = null;
		if (ootbResourceType.equals(JDBC)) {
			ootbPanel = new JDBCAttributesPanel(resource);
		} else if (ootbResourceType.equals(FTP)) {
			ootbPanel = new FTPAttributesPanel(resource);
		} else if (ootbResourceType.equals(JMS)) {
			ootbPanel = new JMSAttributesPanel(resource);
		} else if (ootbResourceType.equals(HTTP)) {
			ootbPanel = new HTTPAttributesPanel(resource);
		} else if (ootbResourceType.equals(JPPF)) {
			ootbPanel = new JPPFAttributesPanel(resource);
		} else if (ootbResourceType.equals(JEM)) {
			ootbPanel = new JEMAttributesPanel(resource);
		} else {
			throw new IllegalArgumentException("I don't know the panel to render for resource type " + ootbResourceType);
		}
		return ootbPanel;
	}
	
	/**
	 * Render the panel that fit a CUSTOM reosuce
	 * @param resourceDescriptor the panel descriptor
	 * @return a {@link ResourcesPropertiesPanel}
	 */
	public ResourcesPropertiesPanel renderCustomResourcePanel(ResourceDescriptor resourceDescriptor) {
		return WidgetFactory.INSTANCE.renderResource(resourceDescriptor, resource);
	}

	/**
	 * Check for mandatory attributes
	 * @return <code>true</code> if all mandatory attributes have a value set
	 */
	protected boolean checkMandatoryAttributes() {
		// send this call to current selected ResourcesPropertiesPanel, regardless if it's a ootb or a custom one 
		ResourcesPropertiesPanel selectedPanel = (ResourcesPropertiesPanel) resourcePanelContainer.getWidget();
		boolean allMandatoryAttributesAreFilled = false;
		if (selectedPanel != null) {
			allMandatoryAttributesAreFilled = selectedPanel.checkMandatory();
		}
		if (!allMandatoryAttributesAreFilled) {
			new Toast(MessageLevel.WARNING, "You have to fill all required attributes before save the resource", "Mandatory attributes missing").show();
		}
		return allMandatoryAttributesAreFilled;
	}
	
	/**
	 * @return <code>true</code> if all values matches the validation regular expression
	 */
	public boolean validate() {
		ResourcesPropertiesPanel selectedPanel = (ResourcesPropertiesPanel) resourcePanelContainer.getWidget();
		boolean validated = false;
		if (selectedPanel != null) {
			validated = selectedPanel.validate();
		}
		return validated;
	}
	
	/**
	 * save the resource
	 */
	public abstract void save();
	
    /**
     * @return the underlying resource
     */
	public Resource getResource() {
		return resource;
	}

    @Override
    public abstract FlexTable getHeader();
	
    @Override
    public Panel getContent() {
	    return mainContainer;
    }

    @Override
    public Panel getActions() {
	    return actionsPanel;
    }

}