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
package org.pepstock.jem.gwt.client.panels.nodes.commons;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector;
import org.pepstock.jem.gwt.client.panels.nodes.commons.inspector.General;
import org.pepstock.jem.gwt.client.panels.nodes.commons.inspector.NodeHeader;
import org.pepstock.jem.gwt.client.panels.nodes.commons.inspector.System;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Component which shows all node information. Can be called to see a node.
 * 
 * @author Marco "Fuzzo" Cuccato
 * @version 1.0	
 *
 */
public class NodeInfoInspector extends AbstractTabPanelInspector {
	
	private NodeInfoBean node = null;
	
	private TabPanel main = new TabPanel();

	/**
	 * Construct the UI without output information.<br>
	 * 
	 * @param node
	 */
	public NodeInfoInspector(NodeInfoBean node){
		this.node = node;

		main.add(new General(node), "General");
		main.add(new System(node), "System");
		// selects general
		main.selectTab(0);
	}

	/**
	 * @return The {@link NodeInfoBean} instance
	 */
	public NodeInfoBean getNode() {
		return node;
	}
	
	/**
	 * @param node Set the {@link NodeInfoBean} instance
	 */
	public void setNode(NodeInfoBean node) {
		this.node = node;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector#getTabPanel()
	 */
    @Override
    public TabPanel getTabPanel() {
	    return main;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getHeader()
	 */
    @Override
    public FlexTable getHeader() {
	    return new NodeHeader(node.getLabel(), this);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getActions()
	 */
    @Override
    public CellPanel getActions() {
	    return null;
    }
}