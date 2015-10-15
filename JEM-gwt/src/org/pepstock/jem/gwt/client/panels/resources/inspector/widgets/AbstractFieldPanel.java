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

import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourceUIComponent;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;
import org.pepstock.jem.node.resources.definition.fields.AbstractFieldDescriptor;

import com.google.gwt.user.client.ui.Widget;

/**
 * Generic panel for field rendering.
 * <code>T</code> is the descriptor that defines the UI
 * <code>W</code> is the widget used to render the UI
 * <code>V</code> is the value that the user can choose and this panel have to return
 * @author Marco "Fuzzo" Cuccato
 * @param <T> 
 * @param <W> 
 * @param <V> 
 *
 */
public abstract class AbstractFieldPanel<T extends AbstractFieldDescriptor, W extends Widget, V> implements ResourceUIComponent {

	private T descriptor = null;
	private CommonResourcePropertiesPanel<? extends ResourcePartDescriptor> panel = null;
	
	protected String label = null;
	protected W inputObject = null;
	protected String description = null;
	
	/**
	 * Build the panel. 
	 * Subclasses should use <code>table</code> object to add their own widgets.
	 * Super calls to this constructor have to call build as it's last statement!
	 * @param descriptor the field descriptor
	 * @param panel 
	 */
	public AbstractFieldPanel(T descriptor, CommonResourcePropertiesPanel<?> panel) {
		this.descriptor = descriptor;
		this.panel = panel;
		label = renderLabel();
		description = descriptor.getDescription();
	}

	/**
	 * Useful method to be called at the end of implementing class constructor
	 */
	protected abstract void build();
	
	/**
	 * @return the descriptow which knows how to render the object
	 */
	public T getDescriptor() {
		return descriptor;
	}

	/**
	 * @return the parent panel in which this field panel is
	 */
	public CommonResourcePropertiesPanel<? extends ResourcePartDescriptor> getPanel() {
		return panel;
	}
	
	/**
	 * @return the label String
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the Panel in which there are the input objects
	 */
	public W getInputObject() {
		return inputObject;
	}

	/**
	 * @return the description String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the user selected value
	 */
	public abstract V getSelectedValue();
	
	/**
	 * This should be used when the user needs to modify an existing resource.
	 * With this method, the filed set it's value to the one passed
	 * @param value the value that this field should have
	 */
	public abstract void setSelectedValue(V value);
	
	/**
	 * Render a String that can be used as a Label paramenter
	 * @param descriptor the descriptor from which extract the label
	 * @return a String
	 */
	public final String renderLabel() {
		String newLabel = descriptor.getLabel();
		if (descriptor.isMandatory()) {
			newLabel += " <font color=\"red\"><b>*</b></font>";
		}
		return newLabel;
	}

	/**
	 * Sets "visible" and "override" attributes, common to all properties.
	 */
	public void setCommonPropertyAttributes(){
		String key = getDescriptor().getKey();
		ResourceProperty property = getPanel().getResource().getProperties().get(key);
		if (property != null){
			property.setOverride(descriptor.isOverride());
			property.setVisible(descriptor.isVisible());
		}
	}
	
	/**
	 * Save the property to the {@link Resource} with value passed as paramenter
	 * @param value the value you want save 
	 */
	public abstract void saveProperty(V value);
	
}
