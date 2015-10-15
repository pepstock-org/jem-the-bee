/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.panels.resources.inspector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.CellTableStyle;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.common.ListHeader;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class ResourceDescriptorsList extends VerticalPanel implements ResizeCapable,  SearchListener {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.administration().ensureInjected();
	}
	
	private Filter searcher = null;
	
	private static final ResourceDescriptorCell RESOURCE_DESCRIPTOR_CELL = new ResourceDescriptorCell();

	private CellList<ResourceDescriptor> cellList = null;
	
	private final SingleSelectionModel<ResourceDescriptor> selectionModel = new SingleSelectionModel<ResourceDescriptor>();
	
	private ScrollPanel cellListScroller = new ScrollPanel();
	
	private InspectListener<ResourceDescriptor> listener = null;
	
	private ResourceDescriptor selectedResourceDescriptor = null;
	
	private VerticalPanel listContainer = new VerticalPanel();
	
	private List<ResourceDescriptor> descriptors = null;
	
	/**
	 * 
	 */
	public ResourceDescriptorsList() {
		// input panel instantiation
		searcher = new Filter();
		searcher.setListener(this);
		setSpacing(Sizes.SPACING);
		
		cellListScroller.getElement().setId("ResourceDescriptorsList.cellListScroller");
		
		// Create a CellList that uses the cell.
		cellList = new CellList<ResourceDescriptor>(RESOURCE_DESCRIPTOR_CELL, (Resources) GWT.create(CellTableStyle.class));
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		cellList.getElement().setId("ResourceDescriptorsList.cellList");
		
		// Add a selection model to handle user selection.
		cellList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				ResourceDescriptor selected = selectionModel.getSelectedObject();
				if (selected != null) {
					selectedResourceDescriptor = selected;
					if (listener != null){
						listener.inspect(selected);
					}
				} 
			}
		});

	    cellListScroller.setWidget(cellList);

	    VerticalPanel scrollHolder = new VerticalPanel();
	    scrollHolder.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    
	    scrollHolder.getElement().setId("ResourceDescriptorsList.scrollHolder (with Style)");
	    
	    scrollHolder.addStyleName(Styles.INSTANCE.administration().nodeList());
	    scrollHolder.add(cellListScroller);

	    listContainer.add(new ListHeader("Resource types list"));
	    listContainer.add(scrollHolder);
	    
	    add(searcher);
	    add(listContainer);
		setCellWidth(listContainer, Sizes.HUNDRED_PERCENT);
		setCellHeight(listContainer, Sizes.HUNDRED_PERCENT);
	}

	/**
	 * @return the listener
	 */
	public InspectListener<ResourceDescriptor> getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<ResourceDescriptor> listener) {
		this.listener = listener;
	}
	
	/**
	 * @return the selectedResourceDescriptor
	 */
	public ResourceDescriptor getSelectedResourceDescriptor() {
		return selectedResourceDescriptor;
	}

	/**
	 * @param selectedResourceDescriptor the selectedResourceDescriptor to set
	 */
	public void setSelectedResourceDescriptor(ResourceDescriptor selectedResourceDescriptor) {
		this.selectedResourceDescriptor = selectedResourceDescriptor;
	}

	/**
	 * @return the selectionModel
	 */
	public SingleSelectionModel<ResourceDescriptor> getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @param descriptors
	 */
	public void setRowData(Collection<ResourceDescriptor> descriptors){
		this.descriptors = new ArrayList<ResourceDescriptor>(descriptors);
		Collections.sort(this.descriptors, new Comparator<ResourceDescriptor>() {
			@Override
            public int compare(ResourceDescriptor o1, ResourceDescriptor o2) {
	            return o1.getType().compareToIgnoreCase(o2.getType());
            }
		});

	    // Set the total row count. This isn't strictly necessary, but it affects
	    // paging calculations, so its good habit to keep the row count up to date.
	    cellList.setRowCount(descriptors.size(), true);

	    // Push the data into the widget.
	    cellList.setRowData(0, this.descriptors);
	    
	    cellList.redraw();
	    
	    selectedResourceDescriptor = null;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	int height = availableHeight - Sizes.NODE_LIST_HEADER_PX - Sizes.SEARCHER_WIDGET_HEIGHT - 2 * Sizes.SPACING - 3 * Sizes.SPACING;
    	int width = availableWidth - 2 * Sizes.MAIN_TAB_PANEL_BORDER - 2 * Sizes.SPACING;
	    cellListScroller.setSize(Sizes.toString(width), Sizes.toString(height));
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.SearchListener#search(java.lang.String)
	 */
    @Override
    public void search(String filterParm) {
    	String filter = filterParm;
    	List<ResourceDescriptor> data = null;
    	if ((filter == null) || (filter.length() == 0)){
    		data = descriptors;
    	} else {
    	    data = new ArrayList<ResourceDescriptor>();
    	    
    	    // to update as java pattern
    	    if (filter.contains("*")){
    	    	filter = filter.replace("*", ".*");
    	    }
    	    
    	    RegExp regEx = RegExp.compile(filter);
    	    
    	    for (ResourceDescriptor rDesc : descriptors){
    	    	if (regEx.test(rDesc.getType()) || regEx.test(rDesc.getDescription())){
    	    		data.add(rDesc);
    	    	}
    	    }
    	}
	    cellList.setRowCount(data.size(), true);

	    // Push the data into the widget.
	    cellList.setRowData(0, data);
	    
	    cellList.redraw();

    }

}