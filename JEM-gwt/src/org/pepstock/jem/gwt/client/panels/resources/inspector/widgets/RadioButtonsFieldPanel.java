package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.fields.SingleSelectableListFieldDescriptor;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Build a list-based property field, with only one value can be selected.
 * @author Marco "Fuzzo" Cuccato
 */
public final class RadioButtonsFieldPanel extends AbstractFieldPanel<SingleSelectableListFieldDescriptor, Grid, String> {

	/**
	 * Used internally. Needed to create always different button groups automatically
	 */
	private static int instances = 0;
	private RadioButton[] buttons = null;
	
	/**
	 * Builds the panel
	 * @param descriptor the descriptor who knows how to render the panel
	 * @param panel the parent panel
	 */
	public RadioButtonsFieldPanel(SingleSelectableListFieldDescriptor descriptor, CommonResourcePropertiesPanel<?> panel) {
		super(descriptor, panel);
		build();
	}

	@Override
	protected final void build() {
		inputObject = new Grid((int)Math.ceil(getDescriptor().getValues().size() / 2f), 2);
		inputObject.setWidth(Sizes.HUNDRED_PERCENT);
		buttons = new RadioButton[getDescriptor().getValues().size()];
		// builds a random radio button group name, the same for all buttons
		final String radioButtonGroupName = "RadioButtonGroup-" + (++instances);
		// builds the buttons
		Handler handler = new Handler();
		List<String> valueList = new ArrayList<String>(getDescriptor().getValues());
		Collections.sort(valueList);
		int i = 0;
		int row = 0;
		for (String v : valueList) {
			RadioButton rb = new RadioButton(radioButtonGroupName, v);

			// events
			rb.addValueChangeHandler(handler);
			
			buttons[i] = rb;
			inputObject.setWidget(row, i % 2 == 0 ? 0 : 1, rb);
			if (++i % 2 == 0) {
				row++;
			}
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
	}

	@Override
	public boolean checkMandatory() {
		if (getDescriptor().isMandatory()) {
			for (RadioButton rb : buttons) {
				if (rb.getValue()) {
					return true;
				}
			}
			return false;
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
		for (RadioButton rb : buttons) {
			if (rb.getValue()) {
				return rb.getText();
			}
		}
		return null;
	}

	@Override
	public void setSelectedValue(String value) {
		for (RadioButton rb : buttons) {
			if (rb.getText().equals(value)) {
				rb.setValue(true);
				break;
			}
		}
	}

	@Override
	public void saveProperty(String value) {
		String key = getDescriptor().getKey();
		getPanel().getResource().setProperty(key, value);
		setCommonPropertyAttributes();
	}

	class Handler implements ValueChangeHandler<Boolean> {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			String value = getSelectedValue();
			saveProperty(value);
		}

	}

}
