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
package org.pepstock.jem.gwt.client.panels.administration.nodesqueues;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.administration.commons.NodeInspectListener;

import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class OverviewPanel extends AdminPanel implements ResizeCapable {

	private NodesTableContainer nodes = new NodesTableContainer(new NodesTable());

	private ScrollPanel scroller = new ScrollPanel(nodes);
	
	private NodeInspectListener listener = null;

	/**
	 * 
	 */
	public OverviewPanel() {
		setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		setSpacing(Sizes.SPACING);

		add(scroller);
		setCellHeight(scroller, Sizes.HUNDRED_PERCENT);
	}

	/**
	 * @return the listener
	 */
	public NodeInspectListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(NodeInspectListener listener) {
		this.listener = listener;
		nodes.getNodesTable().setInspectListener(listener);
	}
	/**
	 * @param memberKey 
	 * 
	 */
	public void load(){
    	nodes.getNodesTable().setRowData(Instances.getLastSample().getMembers());
    	nodes.refresh();
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
		
		scroller.setHeight(Sizes.toString(getHeight()));
		scroller.setWidth(Sizes.toString(getWidth()));
    }
}