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
package org.pepstock.jem.node.resources.definition.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pepstock.jem.node.resources.definition.ResourceDescriptor;
import org.pepstock.jem.node.resources.definition.ResourceMessage;
import org.pepstock.jem.node.resources.definition.SectionDescriptor;
import org.pepstock.jem.node.resources.definition.engine.xml.AbstractFieldTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.CheckBoxFieldTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.ListFieldTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.MultiSelectableListFieldTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.PasswordFieldTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.ResourceTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.ResourceTemplates;
import org.pepstock.jem.node.resources.definition.engine.xml.SectionTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.SingleSelectableListFieldTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.TextFieldTemplate;
import org.pepstock.jem.node.resources.definition.engine.xml.ValueTemplate;
import org.pepstock.jem.node.resources.definition.fields.AbstractFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.CheckBoxFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.CheckBoxesListFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.MultiSelectableListFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.PasswordFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.SingleSelectableListFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.TextFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.TextListFieldDescriptor;

/**
 * <code>ResourceTemplatesFactory</code> builds a list of 
 * {@link ResourceDescriptor} starting from a {@link ResourceTemplates} object.
 * 
 * @author Alessandro Zambrini
 * @version 1.4
 *
 */
public class ResourceTemplatesFactory {
	
	/**
	 * To avoid any instantiation
	 */
	private ResourceTemplatesFactory() {
		
	}

	/**
	 * Builds an <code>Iterator</code> of {@link ResourceDescriptor} starting from a {@link ResourceTemplates} object.
	 * @param resourceTemplates the resource templates that contains the description of the resources to build.
	 * @return the <code>Iterator</code> of {@link ResourceDescriptor}
	 * @throws ResourceTemplateException if errors occurred.
	 * @see Iterator
	 */
	public static Iterator<ResourceDescriptor> buildResourceDescriptors(ResourceTemplates resourceTemplates) throws ResourceTemplateException{
		// checks the resources template
		if (null == resourceTemplates) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "ResourceDescriptor Iterator", "ResourceTemplates");
		}
		// creates a list of resource descriptors
		List<ResourceDescriptor> resources = new ArrayList<ResourceDescriptor>();
		// scans all resource templates
		Iterator<ResourceTemplate> templates = resourceTemplates.getContent().iterator();
		while (templates.hasNext()) {
			// builds the descriptor using the templates
			ResourceDescriptor resource = buildResourceDescriptor(templates.next());
			// adds resource descriptor
			resources.add(resource);
		}
		return resources.iterator();
	}
	
	/**
	 * Builds a {@link ResourceDescriptor} starting from a {@link ResourceTemplate} object.
	 * @param resourceTemplate the resource template that contains the description of the resource descriptor to build.
	 * @return the <code>ResourceDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	public static ResourceDescriptor buildResourceDescriptor(ResourceTemplate resourceTemplate) throws ResourceTemplateException{
		// checks if template is null
		if (null == resourceTemplate) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "ResourceDescriptor", "ResourceTemplate");
		}
		// checks if templates has got the type
		if (null == resourceTemplate.getType()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "ResourceDescriptor", "ResourceTemplate Type");
		}
		// creates a resource descriptor
		ResourceDescriptor resource = new ResourceDescriptor();
		// sets type and description
		resource.setType(resourceTemplate.getType());
		resource.setDescription(resourceTemplate.getDescription());
		// scans all the sections
		List<SectionTemplate> sectionTemplates = resourceTemplate.getContent();
		for (int i = 0; i < sectionTemplates.size(); i++) {
			// creates the sections
			SectionDescriptor section = buildSectionDescriptor(sectionTemplates.get(i));
			// adds the sections
			resource.addSections(section);
		}
		// returns the resource descriptor
		return resource;
	}
	
	/**
	 * Builds a {@link SectionDescriptor} starting from a {@link SectionTemplate} object.
	 * @param sectionTemplate section template that contains the description of the section descriptor to build.
	 * @return the <code>SectionDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static SectionDescriptor buildSectionDescriptor(SectionTemplate sectionTemplate) throws ResourceTemplateException{
		// checks if the section template is null
		if (null == sectionTemplate) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "SectionDescriptor", "SectionTemplate");
		}
		// creates a section descriptor
		// if there is a name, it uses to
		// build the object
		SectionDescriptor section = null;
		if (null == sectionTemplate.getName()) {
			section = new SectionDescriptor();
		} else {
			section = new SectionDescriptor(sectionTemplate.getName());
			section.setPropertiesEditor(sectionTemplate.isPropertiesEditor());
		}
		// gets all the defined fields
		List<AbstractFieldTemplate> abstractFieldTemplates = sectionTemplate.getContent();
		// if there are
		if (null != abstractFieldTemplates) {
			// scans all fields
			for (int i = 0; i < abstractFieldTemplates.size(); i++) {
				// checks the list field. 
				// if used, must be ALONE!!!
				if (abstractFieldTemplates.get(i) instanceof ListFieldTemplate && abstractFieldTemplates.size() > 1) {
					throw new ResourceTemplateException(ResourceMessage.JEMR022E);
				}
				// creates the field descriptor
				AbstractFieldDescriptor field = buildFieldDescriptor(abstractFieldTemplates.get(i));
				// adds to the section 
				section.addFields(field);
			}
		}
		// return the section
		return section;
	}
	
	/**
	 * Builds a {@link AbstractFieldDescriptor} starting from a {@link AbstractFieldTemplate} object.
	 * @param abstractFieldTemplate field template that contains the description of the field descriptor to build.
	 * @return the <code>FieldDescriptor<code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static AbstractFieldDescriptor buildFieldDescriptor(AbstractFieldTemplate abstractFieldTemplate) throws ResourceTemplateException{
		// checks if the field template is not null
		if (null == abstractFieldTemplate) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "FieldDescriptor", "FieldTemplate");
		}
		AbstractFieldDescriptor abstractFieldDescriptor = null;
		// in according with the typ of field
		// creates the right field 
		if (abstractFieldTemplate instanceof PasswordFieldTemplate) {
			// PASSWORD! pay attention: must be before of TEXT
			// because they are of the same type
			abstractFieldDescriptor = buildPasswordFieldDescriptor((PasswordFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof TextFieldTemplate) {
			// TEXT
			abstractFieldDescriptor = buildTextFieldDescriptor((TextFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof CheckBoxFieldTemplate) {
			// CHECKBOX
			abstractFieldDescriptor = buildCheckBoxFieldDescriptor((CheckBoxFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof SingleSelectableListFieldTemplate) {
			// SINGLE LIST
			abstractFieldDescriptor = buildSingleSelectableListFieldDescriptor((SingleSelectableListFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof ListFieldTemplate) {
			// LIST FIELD
			abstractFieldDescriptor = buildTextListFieldDescriptor((ListFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof MultiSelectableListFieldTemplate) {
			// MULTI LIST
			abstractFieldDescriptor = buildCheckBoxesListFieldDescriptor((MultiSelectableListFieldTemplate) abstractFieldTemplate);
		} else {
			// error!! is not a fvalid FIELD
			throw new ResourceTemplateException(ResourceMessage.JEMR008E, abstractFieldTemplate.getClass().getName());
		}
		// adds the common attributes
		addCommonAttributes(abstractFieldTemplate, abstractFieldDescriptor);
		// returns the field
		return abstractFieldDescriptor;
	}
	
	/**
	 * Add in the parameter <code>abstractFieldDescriptor</code> the properties
	 * common to all kind of fields. They are property of {@link AbstractFieldTemplate}
	 * 
	 * @param abstractFieldTemplate field template that contains the description of the field descriptor to build.
	 * @param abstractFieldDescriptor the <code>AbstractFieldDescriptor<code> builded.
	 */
	private static void addCommonAttributes(AbstractFieldTemplate abstractFieldTemplate, AbstractFieldDescriptor abstractFieldDescriptor){
		// sets if is mandatory
		if (abstractFieldTemplate.isMandatory()) {
			abstractFieldDescriptor.setMandatory(true);
		}
		// sets if is overridable
		if (!abstractFieldTemplate.isOverride()) {
			abstractFieldDescriptor.setOverride(false);
		}
		// sets if is visible
		if (!abstractFieldTemplate.isVisible()) {
			abstractFieldDescriptor.setVisible(false);
		}
	}
	
	/**
	 * Builds a {@link PasswordFieldDescriptor} starting from a {@link PasswordFieldTemplate} object.
	 * @param passwordFieldTemplate password field template that contains the description of the password field descriptor to build.
	 * @return the <code>PasswordFieldDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static PasswordFieldDescriptor buildPasswordFieldDescriptor(PasswordFieldTemplate passwordFieldTemplate) throws ResourceTemplateException {
		// MUST have the key
		if (null == passwordFieldTemplate.getKey()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "PasswordFieldDescriptor", "FieldTemplate Key");
		}
		// MUST have the label
		if (null == passwordFieldTemplate.getLabel()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "PasswordFieldDescriptor", "FieldTemplate Label");
		}
		// creates a password descriptor, using key and label
		PasswordFieldDescriptor passwordField = new PasswordFieldDescriptor(passwordFieldTemplate.getKey(), passwordFieldTemplate.getLabel());
		// checks if there is the description
		if (null != passwordFieldTemplate.getDescription()) {
			passwordField.setDescription(passwordFieldTemplate.getDescription());
		}
		return passwordField;
	}
	
	/**
	 * Builds a {@link TextFieldDescriptor} starting from a {@link TextFieldTemplate} object.
	 * @param textFieldTemplate text field template that contains the description of the text field descriptor to build.
	 * @return the <code>TextFieldDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static TextFieldDescriptor buildTextFieldDescriptor(TextFieldTemplate textFieldTemplate) throws ResourceTemplateException {
		// MUST have the key
		if (null == textFieldTemplate.getKey()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "TextFieldDescriptor", "FieldTemplate Key");
		}
		// MUST have the label
		if (null == textFieldTemplate.getLabel()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "TextFieldDescriptor", "FieldTemplate Label");
		}
		// creates a text descriptor, using key and label
		TextFieldDescriptor textField = new TextFieldDescriptor(textFieldTemplate.getKey(), textFieldTemplate.getLabel());
		// checks if there is the description
		if (null != textFieldTemplate.getDescription()) {
			textField.setDescription(textFieldTemplate.getDescription());
		}
		// checks if there is a default value
		if (null != textFieldTemplate.getDefaultValue()) {
			textField.setDefaultValue(textFieldTemplate.getDefaultValue());
		}
		// checks if there is a regular expression for content check
		if (null != textFieldTemplate.getRegExValidator()) {
			textField.setRegExValidator(textFieldTemplate.getRegExValidator());
		}
		return textField;
	}
	
	/**
	 * Builds a {@link CheckBoxFieldDescriptor} starting from a {@link CheckBoxFieldTemplate} object.
	 * @param checkBoxFieldTemplate single check box field template that contains the description of the check box field descriptor to build.
	 * @return the <code>CheckBoxFieldDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static CheckBoxFieldDescriptor buildCheckBoxFieldDescriptor(CheckBoxFieldTemplate checkBoxFieldTemplate) throws ResourceTemplateException {
		// MUST have the key
		if (null == checkBoxFieldTemplate.getKey()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "TextFieldDescriptor", "FieldTemplate Key");
		}
		// MUST have the label
		if (null == checkBoxFieldTemplate.getLabel()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "TextFieldDescriptor", "FieldTemplate Label");
		}
		// creates a checkbox descriptor, using key and label
		CheckBoxFieldDescriptor checkBoxField = new CheckBoxFieldDescriptor(checkBoxFieldTemplate.getKey(), checkBoxFieldTemplate.getLabel());
		// checks if there is the description
		if (null != checkBoxFieldTemplate.getDescription()) {
			checkBoxField.setDescription(checkBoxFieldTemplate.getDescription());
		}
		// checks if there is a default value
		if (checkBoxFieldTemplate.isDefaultValue()) {
			checkBoxField.setDefaultValue(true);
		} else {
			checkBoxField.setDefaultValue(false);
		}
		return checkBoxField;
	}
	
	/**
	 * Builds a {@link SingleSelectableListFieldDescriptor} starting from a {@link SingleSelectableListFieldTemplate} object.
	 * @param singleSelectableListFieldTemplate single selectable list field template that contains the description of the field descriptor to build.
	 * @return the <code>SingleSelectableListFieldDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static SingleSelectableListFieldDescriptor buildSingleSelectableListFieldDescriptor(SingleSelectableListFieldTemplate singleSelectableListFieldTemplate) throws ResourceTemplateException {
		// MUST have the key
		if (null == singleSelectableListFieldTemplate.getKey()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "SingleSelectableListFieldDescriptor", "FieldTemplate Key");
		}
		// MUST have the label
		if (null == singleSelectableListFieldTemplate.getLabel()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "SingleSelectableListFieldDescriptor", "FieldTemplate Label");
		}
		// MUST have the list of possible value to be selectable
		if (null == singleSelectableListFieldTemplate.getContent() || singleSelectableListFieldTemplate.getContent().isEmpty()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR009E);
		}
		// gets the default value
		String defaultValue = singleSelectableListFieldTemplate.getDefaultValue();
		// gets the list of values
		List<ValueTemplate> valueTemplates = singleSelectableListFieldTemplate.getContent();
		String[] values = new String[valueTemplates.size()];
		boolean foundDefaultValue = false;
		// scans all value to find the default and loads the array 
		for (int i = 0; i < valueTemplates.size(); i++) {
			ValueTemplate valueTemplate = valueTemplates.get(i);
			// checks with the default
			if (null != defaultValue && defaultValue.equals(valueTemplate.getContent())) {
				foundDefaultValue = true;
			}
			// loads the array
			values[i] = valueTemplate.getContent();
		}
		// DEFAULT MUST be in the content list!!
		if (null != defaultValue && !foundDefaultValue) {
			throw new ResourceTemplateException(ResourceMessage.JEMR010E, defaultValue);
		}
		// creates the field list
		SingleSelectableListFieldDescriptor singleSelectableListField = new SingleSelectableListFieldDescriptor(singleSelectableListFieldTemplate.getKey(), singleSelectableListFieldTemplate.getLabel(), values);
		// sets the default if there is
		if (null != defaultValue) {
			singleSelectableListField.setDefaultValue(defaultValue);
		}
		// sets the description if there is
		if (null != singleSelectableListFieldTemplate.getDescription()) {
			singleSelectableListField.setDescription(singleSelectableListFieldTemplate.getDescription());
		}
		// sets if teh object must be represented by a radio button or not
		if (singleSelectableListFieldTemplate.isRenderAsRadio()) {
			singleSelectableListField.setRenderAsRadio(singleSelectableListFieldTemplate.isRenderAsRadio());
		}
		return singleSelectableListField;
	}

	/**
	 * Builds a {@link CheckBoxesListFieldDescriptor} starting from a {@link MultiSelectableListFieldTemplate} object.
	 * @param multiSelectableListFieldTemplate multiple selectable list field template that contains the description of the field descriptor to build.
	 * @return the <code>CheckBoxesListFieldDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static CheckBoxesListFieldDescriptor buildCheckBoxesListFieldDescriptor(MultiSelectableListFieldTemplate multiSelectableListFieldTemplate) throws ResourceTemplateException {
		// MUST have the key
		if (null == multiSelectableListFieldTemplate.getKey()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "CheckBoxesListFieldDescriptor", "FieldTemplate Key");
		}
		// MUST have the label
		if (null == multiSelectableListFieldTemplate.getLabel()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "CheckBoxesListFieldDescriptor", "FieldTemplate Label");
		}
		// MUST have the list of possible value to be selectable
		if (null == multiSelectableListFieldTemplate.getContent() || multiSelectableListFieldTemplate.getContent().isEmpty()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR009E);
		}
		// creaets a checkbox list
		CheckBoxesListFieldDescriptor checkBoxesListFieldDescriptor = new CheckBoxesListFieldDescriptor(multiSelectableListFieldTemplate.getKey(), multiSelectableListFieldTemplate.getLabel());
		// scans all values
		List<ValueTemplate> valueTemplates = multiSelectableListFieldTemplate.getContent();
		for (int i = 0; i < valueTemplates.size(); i++) {
			ValueTemplate valueTemplate = valueTemplates.get(i);
			String value = valueTemplate.getContent();
			checkBoxesListFieldDescriptor.setValues(value);
			if (valueTemplate.isSelected()) {
				// if selected, sets as default
				checkBoxesListFieldDescriptor.setDefaultValues(value);
			}
		}
		// sets the description if there is
		if (null != multiSelectableListFieldTemplate.getDescription()) {
			checkBoxesListFieldDescriptor.setDescription(multiSelectableListFieldTemplate.getDescription());
		}
		return checkBoxesListFieldDescriptor;
	}
	
	/**
	 * Builds a {@link MultiSelectableListFieldDescriptor} starting from a {@link MultiSelectableListFieldTemplate} object.
	 * @param multiSelectableListFieldTemplate multiple selectable list field template that contains the description of the field descriptor to build.
	 * @return the <code>MultiSelectableListFieldDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static TextListFieldDescriptor buildTextListFieldDescriptor(ListFieldTemplate listFieldTemplate) throws ResourceTemplateException {
		// MUST have the key
		if (null == listFieldTemplate.getKey()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "TextFieldDescriptor", "FieldTemplate Key");
		}
		// MUST have the label
		if (null == listFieldTemplate.getLabel()) {
			throw new ResourceTemplateException(ResourceMessage.JEMR007E, "TextFieldDescriptor", "FieldTemplate Label" );
		}
		// creates a text list 
		TextListFieldDescriptor textListFieldDescriptor = new TextListFieldDescriptor(listFieldTemplate.getKey(), listFieldTemplate.getLabel(), null);
		// gets content
		List<ValueTemplate> valueTemplates = listFieldTemplate.getContent();
		if (null != valueTemplates) {
			for (int i = 0; i < valueTemplates.size(); i++) {
				ValueTemplate valueTemplate = valueTemplates.get(i);
				String value = valueTemplate.getContent();
				textListFieldDescriptor.setValues(value);
				textListFieldDescriptor.setDefaultValues(value);
			}
		}
		// sets the description if there is
		if (null != listFieldTemplate.getDescription()) {
			textListFieldDescriptor.setDescription(listFieldTemplate.getDescription());
		}
		// sets the regular expression if there is
		if (null != listFieldTemplate.getRegExValidator()) {
			textListFieldDescriptor.setRegExValidator(listFieldTemplate.getRegExValidator());
		}
		return textListFieldDescriptor;
	}
}