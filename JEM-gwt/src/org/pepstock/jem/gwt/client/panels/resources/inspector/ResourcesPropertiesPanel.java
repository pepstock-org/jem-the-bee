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
package org.pepstock.jem.gwt.client.panels.resources.inspector;


import org.pepstock.jem.gwt.client.commons.DefaultInspectorItem;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * @author Andrea "Stock" Stocchero
 * @author Marco "Fuzzo" Cuccato
 */
public abstract class ResourcesPropertiesPanel extends DefaultInspectorItem implements Initializable, ResourceUIComponent {

	private Resource resource = null;
	
	private FlexTable table = null;
	
	/**
	 * @param resource 
	 * @param hasComplexWidget 
	 * 
	 */
	public ResourcesPropertiesPanel(Resource resource, boolean hasComplexWidget) {
		this.resource = resource;
		if (!hasComplexWidget){
			table = new FlexTable();
			add(table);
		}
	}

	/**
	 * @param resource 
	 * 
	 */
	public ResourcesPropertiesPanel(Resource resource) {
		this(resource, false);
	}
	
	/**
	 * @return the table
	 */
	public FlexTable getTable() {
		return table;
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	/**
	 * In case of value equals to null or empty String, remove the property.
	 * 
	 * @param property
	 * @param name
	 * @param value
	 * @return 
	 */
	public ResourceProperty setPropertyValue(String name, String value){
		for (ResourceProperty property : getResource().getProperties().values()){
			if (property.getName().equalsIgnoreCase(name)){
				if(null != value && !value.trim().isEmpty()){
					property.setValue(value);
				} else {
					getResource().getProperties().remove(property.getName());
				}
				return property;
			} 
		}
		ResourceProperty property = new ResourceProperty();
		property.setName(name);
		property.setValue(value);
		if(null != value && !value.trim().isEmpty()){
			resource.getProperties().put(property.getName(), property);
		}
		return property;
	}
	
}