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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig;

import java.util.ArrayList;
import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.CellTableStyle;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.common.ListHeader;

import com.google.gwt.core.client.GWT;
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
public class NodesList extends VerticalPanel implements ResizeCapable {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.administration().ensureInjected();
	}
	
	private static final NodeCell NODE_CELL = new NodeCell();

	private CellList<NodeInfoBean> cellList = null;
	
	private final SingleSelectionModel<NodeInfoBean> selectionModel = new SingleSelectionModel<NodeInfoBean>();
	
	private ScrollPanel cellListScroller = new ScrollPanel();
	
	private InspectListener<NodeInfoBean> listener = null;
	
	private NodeInfoBean selectedNode = null;
	
	/**
	 * 
	 */
	public NodesList() {
		cellListScroller.getElement().setId("NodesList.cellListScroller");
		
		// Create a CellList that uses the cell.
		cellList = new CellList<NodeInfoBean>(NODE_CELL, (Resources) GWT.create(CellTableStyle.class));
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		cellList.getElement().setId("NodesList.cellList");
		
		// Add a selection model to handle user selection.
		cellList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				NodeInfoBean selected = selectionModel.getSelectedObject();
				if (selected != null) {
					selectedNode = selected;
					if (listener != null){
						listener.inspect(selected);
					}
				} 
			}
		});

	    cellListScroller.setWidget(cellList);

	    VerticalPanel scrollHolder = new VerticalPanel();
	    scrollHolder.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    
	    scrollHolder.getElement().setId("NodesList.scrollHolder (with Style)");
	    
	    scrollHolder.addStyleName(Styles.INSTANCE.administration().nodeList());
	    scrollHolder.add(cellListScroller);

	    add(new ListHeader("Nodes List"));
	    add(scrollHolder);
	}

	/**
	 * @return the listener
	 */
	public InspectListener<NodeInfoBean> getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<NodeInfoBean> listener) {
		this.listener = listener;
	}
	
	/**
	 * @return the selectedNode
	 */
	public NodeInfoBean getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(NodeInfoBean selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	
	/**
	 * @return the selectionModel
	 */
	public SingleSelectionModel<NodeInfoBean> getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @param nodes
	 */
	public void setRowData(Collection<NodeInfoBean> nodes){

	    // Set the total row count. This isn't strictly necessary, but it affects
	    // paging calculations, so its good habit to keep the row count up to date.
	    cellList.setRowCount(nodes.size(), true);

	    // Push the data into the widget.
	    cellList.setRowData(0, new ArrayList<NodeInfoBean>(nodes));
	    
	    cellList.redraw();
	    
	    selectedNode = null;
		
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	int height = availableHeight - Sizes.NODE_LIST_HEADER_PX;
	    cellListScroller.setSize(Sizes.toString(availableWidth), Sizes.toString(height));
    }
}