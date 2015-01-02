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

import java.util.Map;

import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.fields.CheckBoxFieldDescriptor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * Build a text based property field
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class CheckBoxFieldPanel extends AbstractFieldPanel<CheckBoxFieldDescriptor, CheckBox, String> {

	/**
	 * Builds the text based property panel
	 * @param descriptor the descriptor who know how to render the panel
	 * @param panel the parent panel
	 */
	public CheckBoxFieldPanel(CheckBoxFieldDescriptor descriptor, CommonResourcePropertiesPanel<?> panel) {
		super(descriptor, panel);
		build();
	}

	@Override
	protected void build() {
		inputObject = new CheckBox();
		Handler handler = new Handler();
		
		inputObject.addClickHandler(handler);
		
		ResourceProperty existingProperty = getPanel().getResource().getProperties().get(getDescriptor().getKey());
		if (existingProperty != null) {
			setSelectedValue(existingProperty.getValue());
			// save not needed because it's aloaded property
		} else if (getDescriptor().hasDefaultValue()) {
			String defaultValue = getDescriptor().getDefaultValue();
			setSelectedValue(defaultValue);
			saveProperty(defaultValue);
		}
	}

	@Override
	public boolean checkMandatory() {
		return true;
	}
	
	@Override
	public void loadProperties() {
		Map<String, ResourceProperty> props = getPanel().getResource().getProperties();
		ResourceProperty p = props.get(getDescriptor().getKey());
		if (p != null && p.getValue() != null) {
			setSelectedValue(p.getValue());
		}
	}

	@Override
	public String getSelectedValue() {
		return inputObject.getValue().toString();
	}

	@Override
	public void setSelectedValue(String value) {
		inputObject.setValue(Boolean.parseBoolean(value));
	}

	@Override
	public void saveProperty(String value) {
		ResourcePropertiesUtil.addProperty(getPanel().getResource(), getDescriptor().getKey(), value, getDescriptor().isVisible(), getDescriptor().isOverride());
		setCommonPropertyAttributes();
	}

	@Override
	public boolean validate() {
		return true;
	}

	class Handler implements ClickHandler {
		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
        @Override
        public void onClick(ClickEvent event) {
        	setSelectedAndSave();
        }

		private void setSelectedAndSave() {
			String value = getSelectedValue();
			saveProperty(value);
		}

		
	}

}
