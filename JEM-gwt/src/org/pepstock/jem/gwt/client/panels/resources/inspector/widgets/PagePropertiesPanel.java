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

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;
import org.pepstock.jem.node.resources.definition.SectionDescriptor;
import org.pepstock.jem.util.ColumnIndex;

/**
 * Builds a simple (single) property panel
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class PagePropertiesPanel extends CommonResourcePropertiesPanel<SectionDescriptor> {

	protected List<AbstractFieldPanel<?,?,?,?>> fields = new LinkedList<AbstractFieldPanel<?,?,?,?>>(); 
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
	 * @param resourceType the resource type, needed for single-page resource
	 */
	public PagePropertiesPanel(Resource resource, SectionDescriptor descriptor, String resourceType) {
		this(resource, descriptor, resourceType, false);
	}
	
	/**
	 * Builds the panel
	 * @param resource the underlying resource
	 * @param descriptor the {@link ResourceDescriptor}
	 * @param resourceType the resource type, needed for single-page resource
	 * @param hasComplex if you need a complex panel
	 */
	public PagePropertiesPanel(Resource resource, SectionDescriptor descriptor, String resourceType, boolean hasComplex) {
		super(resource, descriptor, hasComplex);
		this.resourceType = resourceType;
		if (!hasComplex){
			getTable().setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
			getTable().setCellSpacing(5);
		}
	}

	@Override
	public boolean checkMandatory() {
		boolean result = true;
		for (int i=0; i<fields.size() && result; i++) {
			result &= ((AbstractFieldPanel<?,?,?,?>)fields.get(i)).checkMandatory();
		}
		return result;
	}

	@Override
	public boolean validate() {
		boolean result = true;
		for (int i=0; i<fields.size() && result; i++) {
			result &= ((AbstractFieldPanel<?,?,?,?>)fields.get(i)).validate();
		}
		return result;
	}

	@Override
	public void loadProperties() {
		for (int i=0; i<fields.size(); i++) {
			((AbstractFieldPanel<?,?,?,?>)fields.get(i)).loadProperties();
		}
	}

	/**
	 * Adds a {@link AbstractFieldPanel} to this container 
	 * @param fieldPanel
	 */
	public void addFieldPanel(AbstractFieldPanel<?,?,?,?> fieldPanel) {
		fields.add(fieldPanel);
		int row = getTable().getRowCount();
		getTable().setHTML(row,ColumnIndex.COLUMN_1, fieldPanel.getLabel());
		getTable().setWidget(row,ColumnIndex.COLUMN_2, fieldPanel.getInputObject());
		getTable().setHTML(row,ColumnIndex.COLUMN_3, fieldPanel.getDescription());
		stylize();
	}
	
	protected void stylize() {
		if (!stylized) {
			// disable word wrapping for first column
			getTable().getColumnFormatter().addStyleName(0, Styles.INSTANCE.common().noWrap());
			// set column size
			getTable().getColumnFormatter().setWidth(ColumnIndex.COLUMN_1, "15%");
			getTable().getColumnFormatter().setWidth(ColumnIndex.COLUMN_2, "45%");
			getTable().getColumnFormatter().setWidth(ColumnIndex.COLUMN_3, "40%");
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
