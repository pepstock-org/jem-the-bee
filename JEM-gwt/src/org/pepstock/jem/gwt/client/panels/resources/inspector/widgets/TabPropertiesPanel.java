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
package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.user.client.ui.TabPanel;

/**
 * Build a multi-page properties panel, using a {@link TabPanel} for renderings
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class TabPropertiesPanel extends CommonResourcePropertiesPanel<ResourceDescriptor> {

	private Map<String, PagePropertiesPanel> tabs = new LinkedHashMap<String, PagePropertiesPanel>(); 
	private TabPanel tabPanel = new TabPanel();
	
	/**
	 * Builds the panel
	 * @param resource the underlying resource
	 * @param descriptor the {@link ResourceDescriptor}s
	 */
	public TabPropertiesPanel(Resource resource, ResourceDescriptor descriptor) {
		super(resource, descriptor, true);
		tabPanel.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		add(tabPanel);
	}

	/**
	 * Adds a {@link PagePropertiesPanel} to this container
	 * @param tab the tab to be added (is the section in configuration)
	 * @param title the title displayed in tab bar
	 */
	public void addTab(PagePropertiesPanel tab, String title) {
		tabs.put(title, tab);
		tabPanel.add(tab, title);
	}
	
	/**
	 * Gets the tab with the given name 
	 * @param name the title of tab you want to get
	 * @return a {@link PagePropertiesPanel} that represents the tab main content
	 */
	public PagePropertiesPanel getTab(String name) {
		return tabs.get(name);
	}
	
	@Override
	public boolean checkMandatory() {
		boolean result = true;
		for (PagePropertiesPanel ppp : tabs.values()) {
			result &= ppp.checkMandatory();
		}
		return result;
	}
	
	@Override
	public boolean validate() {
		boolean result = true;
		for (PagePropertiesPanel ppp : tabs.values()) {
			result &= ppp.validate();
		}
		return result;
	}

	@Override
	public void loadProperties() {
		for (PagePropertiesPanel ppp : tabs.values()) {
			ppp.loadProperties();
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	
    	int tabPanelItemHeight = availableHeight - Sizes.TABBAR_HEIGHT_PX - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_PADDING_BOTTOM - 
				Sizes.MAIN_TAB_PANEL_BORDER;
    	
    	int tabPanelItemWidth = availableWidth - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_BORDER  - 
				Sizes.MAIN_TAB_PANEL_BORDER;
    	
		for (PagePropertiesPanel ppp : tabs.values()) {
			ppp.onResize(tabPanelItemWidth, tabPanelItemHeight);
		}
    }

    /**
     * @return the underlying {@link TabPanel}
     */
	public TabPanel getTabPanel() {
		return tabPanel;
	}

	@Override
	public void initializeResource() {
		getResource().setType(getDescriptor().getType());
	}
}
