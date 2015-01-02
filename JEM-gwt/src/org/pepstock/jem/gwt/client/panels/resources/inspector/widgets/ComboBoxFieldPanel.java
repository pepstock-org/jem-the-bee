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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.fields.SingleSelectableListFieldDescriptor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Build a list-based property field, with only one value can be selected.
 * @author Marco "Fuzzo" Cuccato
 */
public final class ComboBoxFieldPanel extends AbstractFieldPanel<SingleSelectableListFieldDescriptor, Grid, String> {

	private ListBox list = null;
	
	/**
	 * Builds the panel
	 * @param descriptor the descriptor who knows how to render the panel
	 * @param panel the parent panel
	 */
	public ComboBoxFieldPanel(SingleSelectableListFieldDescriptor descriptor, CommonResourcePropertiesPanel<?> panel) {
		super(descriptor, panel);
		build();
	}

	@Override
	protected final void build() {
		inputObject = new Grid(1, 1);
		inputObject.setWidth(Sizes.HUNDRED_PERCENT);
		list = new ListBox();
		// events
		Handler handler = new Handler();
		list.addChangeHandler(handler);

		List<String> valueList = new ArrayList<String>(getDescriptor().getValues());
		Collections.sort(valueList);
		for (String v : valueList) {
			list.addItem(v, v);
		}
		
		ResourceProperty existingProperty = getPanel().getResource().getProperties().get(getDescriptor().getKey());
		if (existingProperty != null) {
			setSelectedValue(existingProperty.getValue());
			// save not needed because it's aloaded property
		} else if (getDescriptor().hasDefaultValue()) {
			String defaultValue = getDescriptor().getDefaultValue();
			setSelectedValue(defaultValue);
			saveProperty(defaultValue);
		}
		inputObject.setWidget(0, 0, list);
	}

	@Override
	public boolean checkMandatory() {
		if (getDescriptor().isMandatory()) {
			return list.getSelectedIndex()> -1;
		}
		return true;
	}

	@Override
	public boolean validate() {
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
		return list.getItemText(list.getSelectedIndex());
	}

	@Override
	public void setSelectedValue(String value) {
		for (int i=0; i < list.getItemCount(); i++) {
			if (list.getItemText(i).equalsIgnoreCase(value)){
				list.setSelectedIndex(i);
				return;
			}
		}
	}

	@Override
	public void saveProperty(String value) {
		String key = getDescriptor().getKey();
		ResourcePropertiesUtil.addProperty(getPanel().getResource(), key, value, getDescriptor().isVisible(), getDescriptor().isOverride());
		setCommonPropertyAttributes();
	}

	class Handler implements ChangeHandler {
		
		@Override
		public void onChange(ChangeEvent event) {
			String value = getSelectedValue();
			saveProperty(value);
		}

	}

}
