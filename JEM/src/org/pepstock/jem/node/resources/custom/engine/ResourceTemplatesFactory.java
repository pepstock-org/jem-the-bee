/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.resources.custom.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.custom.ResourceDescriptor;
import org.pepstock.jem.node.resources.custom.ResourceMessage;
import org.pepstock.jem.node.resources.custom.SectionDescriptor;
import org.pepstock.jem.node.resources.custom.engine.xml.AbstractFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.CheckBoxFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.ListFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.MultiSelectableListFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.PasswordFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.ResourceTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.ResourceTemplates;
import org.pepstock.jem.node.resources.custom.engine.xml.SectionTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.SingleSelectableListFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.TextFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.ValueTemplate;
import org.pepstock.jem.node.resources.custom.fields.AbstractFieldDescriptor;
import org.pepstock.jem.node.resources.custom.fields.CheckBoxFieldDescriptor;
import org.pepstock.jem.node.resources.custom.fields.CheckBoxesListFieldDescriptor;
import org.pepstock.jem.node.resources.custom.fields.MultiSelectableListFieldDescriptor;
import org.pepstock.jem.node.resources.custom.fields.PasswordFieldDescriptor;
import org.pepstock.jem.node.resources.custom.fields.SingleSelectableListFieldDescriptor;
import org.pepstock.jem.node.resources.custom.fields.TextFieldDescriptor;
import org.pepstock.jem.node.resources.custom.fields.TextListFieldDescriptor;

/**
 * <code>ResourceTemplatesFactory</code> builds a list of 
 * {@link ResourceDescriptor} starting from a {@link ResourceTemplates} object.
 * 
 * @author Alessandro Zambrini
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
		if (null == resourceTemplates) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "ResourceDescriptor Iterator", "ResourceTemplates" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "ResourceDescriptor Iterator", "ResourceTemplates" }));
		}
		List<ResourceDescriptor> resources = new ArrayList<ResourceDescriptor>();
		Iterator<ResourceTemplate> templates = resourceTemplates.getContent().iterator();
		while (templates.hasNext()) {
			ResourceDescriptor resource = buildResourceDescriptor(templates.next());
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
		if (null == resourceTemplate) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "ResourceDescriptor", "ResourceTemplate" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "ResourceDescriptor", "ResourceTemplate" }));
		}
		if (null == resourceTemplate.getType()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "ResourceDescriptor", "ResourceTemplate Type" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "ResourceDescriptor", "ResourceTemplate Type" }));
		}
		ResourceDescriptor resource = new ResourceDescriptor(resourceTemplate.getType());
		List<SectionTemplate> sectionTemplates = resourceTemplate.getContent();
		for (int i = 0; i < sectionTemplates.size(); i++) {
			SectionDescriptor section = buildSectionDescriptor(sectionTemplates.get(i));
			resource.addSections(section);
		}
		return resource;
	}
	
	/**
	 * Builds a {@link SectionDescriptor} starting from a {@link SectionTemplate} object.
	 * @param sectionTemplate section template that contains the description of the section descriptor to build.
	 * @return the <code>SectionDescriptor</code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static SectionDescriptor buildSectionDescriptor(SectionTemplate sectionTemplate) throws ResourceTemplateException{
		if (null == sectionTemplate) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "SectionDescriptor", "SectionTemplate" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "SectionDescriptor", "SectionTemplate" }));
		}
		SectionDescriptor section = null;
		if (null == sectionTemplate.getName()) {
			section = new SectionDescriptor();
		} else {
			section = new SectionDescriptor(sectionTemplate.getName());
		}
		List<AbstractFieldTemplate> abstractFieldTemplates = sectionTemplate.getContent();
		if (null != abstractFieldTemplates) {
			for (int i = 0; i < abstractFieldTemplates.size(); i++) {
				if (abstractFieldTemplates.get(i) instanceof ListFieldTemplate && abstractFieldTemplates.size() > 1) {
					LogAppl.getInstance().emit(ResourceMessage.JEMR022E);
					throw new ResourceTemplateException(ResourceMessage.JEMR022E.toMessage().getMessage());
				}
				AbstractFieldDescriptor field = buildFieldDescriptor(abstractFieldTemplates.get(i));
				section.addFields(field);
			}
		}
		return section;
	}
	
	/**
	 * Builds a {@link AbstractFieldDescriptor} starting from a {@link AbstractFieldTemplate} object.
	 * @param abstractFieldTemplate field template that contains the description of the field descriptor to build.
	 * @return the <code>FieldDescriptor<code> builded.
	 * @throws ResourceTemplateException if errors occurred.
	 */
	private static AbstractFieldDescriptor buildFieldDescriptor(AbstractFieldTemplate abstractFieldTemplate) throws ResourceTemplateException{
		if (null == abstractFieldTemplate) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "FieldDescriptor", "FieldTemplate" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "FieldDescriptor", "FieldTemplate" }));
		}
		AbstractFieldDescriptor abstractFieldDescriptor = null;
		if (abstractFieldTemplate instanceof PasswordFieldTemplate) {
			abstractFieldDescriptor = buildPasswordFieldDescriptor((PasswordFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof TextFieldTemplate) {
			abstractFieldDescriptor = buildTextFieldDescriptor((TextFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof CheckBoxFieldTemplate) {
			abstractFieldDescriptor = buildCheckBoxFieldDescriptor((CheckBoxFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof SingleSelectableListFieldTemplate) {
			abstractFieldDescriptor = buildSingleSelectableListFieldDescriptor((SingleSelectableListFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof ListFieldTemplate) {
			abstractFieldDescriptor = buildTextListFieldDescriptor((ListFieldTemplate) abstractFieldTemplate);
		} else if (abstractFieldTemplate instanceof MultiSelectableListFieldTemplate) {
			abstractFieldDescriptor = buildCheckBoxesListFieldDescriptor((MultiSelectableListFieldTemplate) abstractFieldTemplate);
		} else {
			LogAppl.getInstance().emit(ResourceMessage.JEMR008E, abstractFieldTemplate.getClass().getName());
			throw new ResourceTemplateException(ResourceMessage.JEMR008E.toMessage().getFormattedMessage(abstractFieldTemplate.getClass().getName()));
		}
		addCommonAttributes(abstractFieldTemplate, abstractFieldDescriptor);
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
		if (abstractFieldTemplate.isMandatory()) {
			abstractFieldDescriptor.setMandatory(true);
		}
		if (!abstractFieldTemplate.isOverride()) {
			abstractFieldDescriptor.setOverride(false);
		}
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
		if (null == passwordFieldTemplate.getKey()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "PasswordFieldDescriptor", "FieldTemplate Key" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "PasswordFieldDescriptor", "FieldTemplate Key" }));
		}
		if (null == passwordFieldTemplate.getLabel()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "PasswordFieldDescriptor", "FieldTemplate Label" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "PasswordFieldDescriptor", "FieldTemplate Label" }));
		}
		PasswordFieldDescriptor passwordField = new PasswordFieldDescriptor(passwordFieldTemplate.getKey(), passwordFieldTemplate.getLabel());
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
		if (null == textFieldTemplate.getKey()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "TextFieldDescriptor", "FieldTemplate Key" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "TextFieldDescriptor", "FieldTemplate Key" }));
		}
		if (null == textFieldTemplate.getLabel()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "TextFieldDescriptor", "FieldTemplate Label" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "TextFieldDescriptor", "FieldTemplate Label" }));
		}
		TextFieldDescriptor textField = new TextFieldDescriptor(textFieldTemplate.getKey(), textFieldTemplate.getLabel());
		if (null != textFieldTemplate.getDescription()) {
			textField.setDescription(textFieldTemplate.getDescription());
		}
		if (null != textFieldTemplate.getDefaultValue()) {
			textField.setDefaultValue(textFieldTemplate.getDefaultValue());
		}
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
		if (null == checkBoxFieldTemplate.getKey()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "CheckBoxFieldDescriptor", "FieldTemplate Key" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "TextFieldDescriptor", "FieldTemplate Key" }));
		}
		if (null == checkBoxFieldTemplate.getLabel()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "CheckBoxFieldDescriptor", "FieldTemplate Label" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "TextFieldDescriptor", "FieldTemplate Label" }));
		}
		CheckBoxFieldDescriptor checkBoxField = new CheckBoxFieldDescriptor(checkBoxFieldTemplate.getKey(), checkBoxFieldTemplate.getLabel());
		if (null != checkBoxFieldTemplate.getDescription()) {
			checkBoxField.setDescription(checkBoxFieldTemplate.getDescription());
		}
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
		if (null == singleSelectableListFieldTemplate.getKey()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "SingleSelectableListFieldDescriptor", "FieldTemplate Key" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "SingleSelectableListFieldDescriptor", "FieldTemplate Key" }));
		}
		if (null == singleSelectableListFieldTemplate.getLabel()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "SingleSelectableListFieldDescriptor", "FieldTemplate Label" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "SingleSelectableListFieldDescriptor", "FieldTemplate Label" }));
		}
		if (null == singleSelectableListFieldTemplate.getContent() || singleSelectableListFieldTemplate.getContent().isEmpty()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR009E);
			throw new ResourceTemplateException(ResourceMessage.JEMR009E.toMessage().getMessage());
		}
		String defaultValue = singleSelectableListFieldTemplate.getDefaultValue();
		List<ValueTemplate> valueTemplates = singleSelectableListFieldTemplate.getContent();
		String[] values = new String[valueTemplates.size()];
		boolean foundDefaultValue = false;
		for (int i = 0; i < valueTemplates.size(); i++) {
			ValueTemplate valueTemplate = valueTemplates.get(i);
			if (null != defaultValue && defaultValue.equals(valueTemplate.getContent())) {
				foundDefaultValue = true;
			}
			values[i] = valueTemplate.getContent();
		}
		if (null != defaultValue && !foundDefaultValue) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR010E, defaultValue);
			throw new ResourceTemplateException(ResourceMessage.JEMR010E.toMessage().getFormattedMessage(defaultValue));
		}
		SingleSelectableListFieldDescriptor singleSelectableListField = new SingleSelectableListFieldDescriptor(singleSelectableListFieldTemplate.getKey(), singleSelectableListFieldTemplate.getLabel(), values);
		if (null != defaultValue) {
			singleSelectableListField.setDefaultValue(defaultValue);
		}
		if (null != singleSelectableListFieldTemplate.getDescription()) {
			singleSelectableListField.setDescription(singleSelectableListFieldTemplate.getDescription());
		}
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
		if (null == multiSelectableListFieldTemplate.getKey()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "CheckBoxesListFieldDescriptor", "FieldTemplate Key" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "CheckBoxesListFieldDescriptor", "FieldTemplate Key" }));
		}
		if (null == multiSelectableListFieldTemplate.getLabel()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "CheckBoxesListFieldDescriptor", "FieldTemplate Label" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "CheckBoxesListFieldDescriptor", "FieldTemplate Label" }));
		}
		if (null == multiSelectableListFieldTemplate.getContent() || multiSelectableListFieldTemplate.getContent().isEmpty()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR009E);
			throw new ResourceTemplateException(ResourceMessage.JEMR009E.toMessage().getMessage());
		}
		CheckBoxesListFieldDescriptor checkBoxesListFieldDescriptor = new CheckBoxesListFieldDescriptor(multiSelectableListFieldTemplate.getKey(), multiSelectableListFieldTemplate.getLabel());
		List<ValueTemplate> valueTemplates = multiSelectableListFieldTemplate.getContent();
		for (int i = 0; i < valueTemplates.size(); i++) {
			ValueTemplate valueTemplate = valueTemplates.get(i);
			String value = valueTemplate.getContent();
			checkBoxesListFieldDescriptor.setValues(value);
			if (valueTemplate.isSelected()) {
				checkBoxesListFieldDescriptor.setDefaultValues(value);
			}
		}
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
		if (null == listFieldTemplate.getKey()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "TextFieldDescriptor", "FieldTemplate Key" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "TextFieldDescriptor", "FieldTemplate Key" }));
		}
		if (null == listFieldTemplate.getLabel()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR007E, new Object[] { "TextFieldDescriptor", "FieldTemplate Label" });
			throw new ResourceTemplateException(ResourceMessage.JEMR007E.toMessage().getFormattedMessage(new Object[] { "TextFieldDescriptor", "FieldTemplate Label" }));
		}
		TextListFieldDescriptor textListFieldDescriptor = new TextListFieldDescriptor(listFieldTemplate.getKey(), listFieldTemplate.getLabel(), null);
		List<ValueTemplate> valueTemplates = listFieldTemplate.getContent();
		if (null != valueTemplates) {
			for (int i = 0; i < valueTemplates.size(); i++) {
				ValueTemplate valueTemplate = valueTemplates.get(i);
				String value = valueTemplate.getContent();
				textListFieldDescriptor.setValues(value);
				textListFieldDescriptor.setDefaultValues(value);
			}
		}
		if (null != listFieldTemplate.getDescription()) {
			textListFieldDescriptor.setDescription(listFieldTemplate.getDescription());
		}
		if (null != listFieldTemplate.getRegExValidator()) {
			textListFieldDescriptor.setRegExValidator(listFieldTemplate.getRegExValidator());
		}
		return textListFieldDescriptor;
	}
}
