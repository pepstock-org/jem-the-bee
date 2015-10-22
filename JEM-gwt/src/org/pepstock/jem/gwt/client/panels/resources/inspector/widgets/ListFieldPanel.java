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
import java.util.Set;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.CSVUtil;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.node.resources.definition.SectionDescriptor;
import org.pepstock.jem.node.resources.definition.fields.TextListFieldDescriptor;

/**
 * Builds a text-based properties editor, in which the user can free type values.
 * Rendered with a {@link ListEditor}
 * @author Marco "Fuzzo" Cuccato
 */
public final class ListFieldPanel extends AbstractFieldPanel<TextListFieldDescriptor, ListEditor, String[], SectionDescriptor> implements ListEditorListener<String[]> {

	/**
	 * Builds the panel
	 * @param descriptor the descriptor who knows how to render the panel
	 * @param panel the parent panel
	 */
	public ListFieldPanel(TextListFieldDescriptor descriptor, PagePropertiesPanel panel) {
		super(descriptor, panel);
		build();
	}

	@Override
	protected void build() {
		inputObject = new ListEditor(getDescriptor().getInputLabel(), "Values", getDescriptor().getRegExValidator());
		inputObject.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		
		ResourceProperty existingProperty = getPanel().getResource().getProperties().get(getDescriptor().getKey());
		if (existingProperty == null && getDescriptor().hasDefaultValues()) {
			Set<String> defaultValue = getDescriptor().getDefaultValues();
			String[] defaultValueArray = defaultValue.toArray(new String[0]);
			setSelectedValue(defaultValueArray);
			saveProperty(defaultValueArray);
		}
		
		inputObject.setListener(this);
	}

	@Override
	public boolean checkMandatory() {
		if (getDescriptor().isMandatory()) {
			if (inputObject.getValues().length > 0) {
				return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean validate() {
		boolean allMatch = true;
		String regExValidator = getDescriptor().getRegExValidator();
		String notMatchingValue = null;
		
		if (regExValidator != null && !regExValidator.trim().isEmpty()) {
			try {
				// validator exists, try to validate value
				for (String currentValue : getSelectedValue()) {
					if (currentValue.matches(regExValidator)) {
						allMatch &= true;
					} else {
						notMatchingValue = currentValue;
						allMatch = false;
						break;
					}
				}
			} catch (Exception e) {
				LogClient.getInstance().warning(e.getMessage(), e);
				new Toast(MessageLevel.ERROR, "Unable to parse regular expression '" + regExValidator + "'. Check the Resource Descriptor.", "Validator error").show();
				return false;
			}
		}
		
		// allMatch is true if all values are valid or if no validation is needed
		if (allMatch) {
			return true;
		}
		
		new Toast(MessageLevel.WARNING, "The value '" + notMatchingValue + "' in list must match the regular expression '" + regExValidator + "'.", "Illegal value").show();
		return false;
	}

	@Override
	public void loadProperties() {
		Map<String, ResourceProperty> props = getPanel().getResource().getProperties();
		ResourceProperty p = props.get(getDescriptor().getKey());
		if (p != null && p.getValue() != null) {
			String[] toBeSelectedValues = CSVUtil.split(p.getValue());
			setSelectedValue(toBeSelectedValues);
		}
	}

	@Override
	public String[] getSelectedValue() {
		return inputObject.getValues();
	}

	@Override
	public void setSelectedValue(String[] value) {
		inputObject.setValues(value);
	}

	@Override
	public void valuesChanged(String[] newValues) {
		saveProperty(newValues);
	}

	@Override
	public void saveProperty(String[] value) {
		String key = getDescriptor().getKey();
		String csv = CSVUtil.getCSVPhrase(value);
		ResourcePropertiesUtil.addProperty(getPanel().getResource(), key, csv, getDescriptor().isVisible(), getDescriptor().isOverride());
		setCommonPropertyAttributes();
	}

}
