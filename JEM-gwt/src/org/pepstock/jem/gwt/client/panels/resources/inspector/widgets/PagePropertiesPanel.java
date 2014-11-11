package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;
import org.pepstock.jem.node.resources.definition.SectionDescriptor;

/**
 * Builds a simple (single) property panel
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class PagePropertiesPanel extends CommonResourcePropertiesPanel<SectionDescriptor> {

	protected List<AbstractFieldPanel<?,?,?>> fields = new LinkedList<AbstractFieldPanel<?,?,?>>(); 
	protected boolean stylized = false;
	protected String resourceType = null;
	
	/**
	 * Builds the panel 
	 * @param resource the underlying resource
	 * @param descriptor the {@link ResourceDescriptor}
	 */
	public PagePropertiesPanel(Resource resource, SectionDescriptor descriptor) {
		this(resource, descriptor, null);
	}
	
	/**
	 * Builds the panel
	 * @param resource the underlying resource
	 * @param descriptor the {@link ResourceDescriptor}
	 * @param resourceType the resource type, needed for single-page custom resource
	 */
	public PagePropertiesPanel(Resource resource, SectionDescriptor descriptor, String resourceType) {
		super(resource, descriptor, false);
		this.resourceType = resourceType;
		getTable().setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		getTable().setCellSpacing(5);
	}

	@Override
	public boolean checkMandatory() {
		boolean result = true;
		for (int i=0; i<fields.size() && result; i++) {
			result &= ((AbstractFieldPanel<?,?,?>)fields.get(i)).checkMandatory();
		}
		return result;
	}

	@Override
	public boolean validate() {
		boolean result = true;
		for (int i=0; i<fields.size() && result; i++) {
			result &= ((AbstractFieldPanel<?,?,?>)fields.get(i)).validate();
		}
		return result;
	}

	@Override
	public void loadProperties() {
		for (int i=0; i<fields.size(); i++) {
			((AbstractFieldPanel<?,?,?>)fields.get(i)).loadProperties();
		}
	}

	/**
	 * Adds a {@link AbstractFieldPanel} to this container 
	 * @param fieldPanel
	 */
	public void addFieldPanel(AbstractFieldPanel<?,?,?> fieldPanel) {
		fields.add(fieldPanel);
		int row = getTable().getRowCount();
		getTable().setHTML(row, 0, fieldPanel.getLabel());
		getTable().setWidget(row, 1, fieldPanel.getInputObject());
		getTable().setHTML(row, 2, fieldPanel.getDescription());
		stylize();
	}
	
	protected void stylize() {
		if (!stylized) {
			// disable word wrapping for first column
			getTable().getColumnFormatter().addStyleName(0, Styles.INSTANCE.common().noWrap());
			// set column size
			getTable().getColumnFormatter().setWidth(0, "15%");
			getTable().getColumnFormatter().setWidth(1, "45%");
			getTable().getColumnFormatter().setWidth(2, "40%");
			// set flag to avoid re-stylize
			stylized = true;
		}
	}

	@Override
	public void initializeResource() {
		if (resourceType != null && !resourceType.trim().isEmpty()) {
			getResource().setType(resourceType);
		}
	}

}
