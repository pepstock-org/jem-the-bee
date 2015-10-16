/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractPager;
import org.pepstock.jem.gwt.client.commons.DefaultTablePager;
import org.pepstock.jem.gwt.client.commons.UpdateListener;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.SectionDescriptor;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main panel of properties of a resource.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class CustomPropertiesEditor extends PagePropertiesPanel implements UpdateListener<ResourceProperty> {
	
	/**
	 * Key empty of resource property means empty to be edited
	 */
	public static final String NO_VALUE = "";
	
	private VerticalPanel scrollable = new VerticalPanel();
	private CustomPropertiesTable table = new CustomPropertiesTable();
	
	private List<ResourceProperty> list = null;

	/**
	 * @param resource
	 * @param descriptor
	 * @param resourceType
	 */
    public CustomPropertiesEditor(Resource resource, SectionDescriptor descriptor, String resourceType) {
	    super(resource, descriptor, resourceType, true);
    }

	/**
	 * @param resource
	 * @param descriptor
	 */
    public CustomPropertiesEditor(Resource resource, SectionDescriptor descriptor) {
	    this(resource, descriptor, null);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.UpdateListener#update(java.lang.Object)
	 */
    @Override
    public void update(final ResourceProperty node) {
    	// sort after every insert of property key
	    Collections.sort(list, new CustomPropertiesComparator(0));
	    table.setRowData(list);
    }
    
	/**
	 * @throws wrap a {@link #addListFieldPanel(ListFieldPanel)} call
	 */
	@Override
	public void addFieldPanel(AbstractFieldPanel<?, ?, ?, ?> fieldPanel) {
		scrollable.setWidth(Sizes.HUNDRED_PERCENT);
		scrollable.add(table.getTable());
		AbstractPager pager = new DefaultTablePager(table.getPager());
		scrollable.add(pager);
		scrollable.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		add(scrollable);
		
		table.setInspectListener(this);
		// load properties
		loadCustomProperties();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.resources.inspector.ResourceUIComponent#checkMandatory()
	 */
    @Override
    public boolean checkMandatory() {
	    return true;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.resources.inspector.ResourceUIComponent#validate()
	 */
    @Override
    public boolean validate() {
    	Map<String, String> map = new HashMap<String, String>();
    	for (ResourceProperty p : list){
    		if (!p.getName().equals(NO_VALUE)){
    			map.put(p.getName(), p.getValue());
    		}
    	}
    	getResource().setCustomProperties(map);
	    return true;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.resources.inspector.ResourceUIComponent#loadProperties()
	 */
    @Override
    public void loadProperties() {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(ResourceProperty object) {
	    // do nothing
    }	
    
    /**
     * Loads properties in the table
     */
    private void loadCustomProperties(){
    	// maximum number 50
    	int totalRows = 50;
    	if (list != null){
    		list.clear();
    	} else {
    		list = new ArrayList<ResourceProperty>();
    	}
    	if (getResource().getCustomProperties() !=null && !getResource().getCustomProperties().isEmpty()){
    		for (String key : getResource().getCustomProperties().keySet()){
    			ResourceProperty rp = new ResourceProperty();
    			rp.setName(key);
    			rp.setValue(getResource().getCustomProperties().get(key));
    			list.add(rp);
    			totalRows--;
    		}
    	}
    	
    	// loads empty rows to be edited
    	for (int i=0; i<totalRows; i++){
    		ResourceProperty rp = new ResourceProperty();
    		rp.setName("");
    		rp.setValue("");
    		list.add(rp);
    	}
    	table.setRowData(list);
    }

}