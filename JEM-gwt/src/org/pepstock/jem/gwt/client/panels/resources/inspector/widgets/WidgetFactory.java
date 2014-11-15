package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.resources.AbstractResourceInspector;
import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;
import org.pepstock.jem.node.resources.definition.SectionDescriptor;
import org.pepstock.jem.node.resources.definition.fields.AbstractFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.CheckBoxFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.CheckBoxesListFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.MultiSelectableListFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.PasswordFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.SingleSelectableListFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.TextFieldDescriptor;
import org.pepstock.jem.node.resources.definition.fields.TextListFieldDescriptor;

/**
 * This factory builds UI widgets in order to render a custom {@link Resource}, described by a {@link ResourceDescriptor}
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class WidgetFactory {

	/**
	 * Singleton
	 */
	public static final WidgetFactory INSTANCE = new WidgetFactory();
	
	protected WidgetFactory() {
	}

	/**
	 * Return a panel which can be used as {@link AbstractResourceInspector} content
	 * @param descriptor the panel configuration
	 * @param resource resource instance
	 * @return the panel rendered
	 */
	public ResourcesPropertiesPanel renderResource(ResourceDescriptor descriptor, Resource resource) {
		ResourcesPropertiesPanel toReturn = null;
		// if has more than one section, render as tab panel
		if (descriptor.hasSections()) {
			toReturn = render((TabPropertiesPanel)new TabPropertiesPanel(resource, descriptor));
			((TabPropertiesPanel)toReturn).getTabPanel().selectTab(0);
		} else {
			SectionDescriptor sd = descriptor.getSections().getFirst();
			if (sd.isPropertiesEditor()){
				toReturn = render (new CustomPropertiesEditor(resource, sd, descriptor.getType()));
			} else {
				// if the section contains only a list field descriptor, render it properly
				if (sd.getFields().getFirst() instanceof TextListFieldDescriptor) {
					toReturn = render(new ListFieldPagePanel(resource, sd, descriptor.getType()));
				} else {
					// otherwise render as a simple page
					toReturn = render(new PagePropertiesPanel(resource, sd, descriptor.getType()));
				}
			}
		}
		toReturn.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		return toReturn;
	}
	
	protected PagePropertiesPanel render(PagePropertiesPanel panel) {
		SectionDescriptor section = panel.getDescriptor();
		List<AbstractFieldDescriptor> fields = section.getFields();
		
		if (section.isPropertiesEditor()){
			panel.addFieldPanel(null);
		} else {
			for (AbstractFieldDescriptor afd : fields) {
				AbstractFieldPanel<?,?,?> fieldPanel;
				if (afd instanceof PasswordFieldDescriptor) {
					fieldPanel = new PasswordFieldPanel((PasswordFieldDescriptor)afd, panel);
				} else if (afd instanceof TextFieldDescriptor) {
					fieldPanel = new TextFieldPanel((TextFieldDescriptor)afd, panel);
				} else if (afd instanceof CheckBoxFieldDescriptor) {
					fieldPanel = new CheckBoxFieldPanel((CheckBoxFieldDescriptor)afd, panel);
				} else if (afd instanceof SingleSelectableListFieldDescriptor) {
					SingleSelectableListFieldDescriptor sslfd = (SingleSelectableListFieldDescriptor) afd;
					if (sslfd.isRenderAsRadio()){
						fieldPanel = new RadioButtonsFieldPanel((SingleSelectableListFieldDescriptor)afd, panel);
					} else {
						fieldPanel = new ComboBoxFieldPanel((SingleSelectableListFieldDescriptor)afd, panel);
					}
				} else if (afd instanceof CheckBoxesListFieldDescriptor) {
					fieldPanel = new CheckBoxesFieldPanel((MultiSelectableListFieldDescriptor)afd, panel);
				} else if (afd instanceof TextListFieldDescriptor) {
					fieldPanel = new ListFieldPanel((TextListFieldDescriptor)afd, panel);
				} else {
					throw new JemRuntimeException("I don't know how to build an AbstractField of runtime type " + afd.getClass().getName());
				}
				panel.addFieldPanel(fieldPanel);
			}
		}
		panel.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		return panel;
	}

	protected TabPropertiesPanel render(TabPropertiesPanel panel) {
		List<SectionDescriptor> sections = panel.getDescriptor().getSections();
		for (SectionDescriptor sd : sections) {
			PagePropertiesPanel page = null;
			if (sd.isPropertiesEditor()){
				page = render(new CustomPropertiesEditor(panel.getResource(), sd));
			} else {
				if (sd.getFields().getFirst() instanceof TextListFieldDescriptor) {
					page = render(new ListFieldPagePanel(panel.getResource(), sd));
				} else {
					page = render(new PagePropertiesPanel(panel.getResource(), sd));
				}
			}
			panel.addTab(page, sd.getName());
		}
		return panel;
	}
	
}
