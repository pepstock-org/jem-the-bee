package org.pepstock.jem.gwt.client.panels.resources.inspector.custom;

import java.util.Map;

import org.pepstock.jem.gwt.client.commons.MandatoryTextBox;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.custom.fields.TextFieldDescriptor;

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
	public TextFieldPanel(TextFieldDescriptor descriptor, CustomResourcePropertiesPanel<?> panel) {
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
		getPanel().getResource().setProperty(getDescriptor().getKey(), value);
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
