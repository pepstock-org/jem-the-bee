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

import org.pepstock.jem.gwt.client.commons.MandatoryTextBox;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.fields.TextFieldDescriptor;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Build a text based property field
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class TextFieldPanel extends AbstractFieldPanel<TextFieldDescriptor, TextBox, String> {

	/**
	 * Builds the text based property panel
	 * @param descriptor the descriptor who know how to render the panel
	 * @param panel the parent panel
	 */
	public TextFieldPanel(TextFieldDescriptor descriptor, CommonResourcePropertiesPanel<?> panel) {
		super(descriptor, panel);
		build();
	}

	@Override
	protected final void build() {
		if (getDescriptor().isMandatory()) {
			inputObject = new MandatoryTextBox();
		} else {
			inputObject = new TextBox();
		}
		Handler handler = new Handler();
		
		inputObject.addKeyUpHandler(handler);
		inputObject.addValueChangeHandler(handler);
		inputObject.setWidth("98%");
		
		ResourceProperty existingProperty = getPanel().getResource().getProperties().get(getDescriptor().getKey());
		if (existingProperty != null) {
			setSelectedValue(existingProperty.getValue());
			// save not needed because it's a loaded property
		} else if (getDescriptor().hasDefaultValue()) {
			String defaultValue = getDescriptor().getDefaultValue();
			setSelectedValue(defaultValue);
			saveProperty(defaultValue);
		}
	}

	@Override
	public boolean checkMandatory() {
		if (getDescriptor().isMandatory()) {
			MandatoryTextBox mtb = (MandatoryTextBox) inputObject;
			return mtb.isCompiled();
		}
		return true;
	}
	
	@Override
	public void loadProperties() {
		Map<String, ResourceProperty> props = getPanel().getResource().getProperties();
		ResourceProperty p = props.get(getDescriptor().getKey());
		if (p != null && p.getValue() != null) {
			inputObject.setText(p.getValue());
		}
	}

	@Override
	public String getSelectedValue() {
		return inputObject.getText();
	}

	@Override
	public void setSelectedValue(String value) {
		inputObject.setValue(value);
	}

	@Override
	public void saveProperty(String value) {
		ResourcePropertiesUtil.addProperty(getPanel().getResource(), getDescriptor().getKey(), value, getDescriptor().isVisible(), getDescriptor().isOverride());
		setCommonPropertyAttributes();
	}

	class Handler implements KeyUpHandler, ValueChangeHandler<String> {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			setSelectedAndSave();
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			setSelectedAndSave();
		}

		private void setSelectedAndSave() {
			String value = getSelectedValue();
			saveProperty(value);
		}
	}

	@Override
	public boolean validate() {
		String regExValidator = getDescriptor().getRegExValidator();
		if (regExValidator != null && !regExValidator.trim().isEmpty()) {
			// validator exists, try to validate value 
			if (getSelectedValue().matches(regExValidator)) {
				return true;
			}
		} else {
			// the validator does not exist, value is always ok
			return true;
		}
		new Toast(MessageLevel.WARNING, "The value of '" + getLabel() + "' must match the regular expression '" + regExValidator + "'.", "Illegal value").show();
		return false;
	}
	
}
