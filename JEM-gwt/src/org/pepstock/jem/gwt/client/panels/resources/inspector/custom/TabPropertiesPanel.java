package org.pepstock.jem.gwt.client.panels.resources.inspector.custom;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.custom.ResourceDescriptor;

import com.google.gwt.user.client.ui.TabPanel;

/**
 * Build a multi-page properties panel, using a {@link TabPanel} for renderings
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class TabPropertiesPanel extends CustomResourcePropertiesPanel<ResourceDescriptor> {

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
