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
package org.pepstock.jem.gwt.client.panels.administration.nodessystem;



import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.administration.commons.NodesFilter;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Nodes table container for nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesTableContainer extends VerticalPanel implements SearchListener {
	
	private NodesFilter searcher = new NodesFilter();

	private TableContainer<LightMemberSample> nodes = null;
	
	/**
	 * Creates the UI by the argument (the table)
	 *  
	 * @param nodes table of nodes 
	 */
	public NodesTableContainer(NodesTable nodes) {
		this.nodes = new TableContainer<LightMemberSample>(nodes);
		setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		add(searcher);
		add(this.nodes);
		setCellHeight(this.nodes, Sizes.HUNDRED_PERCENT);
		
		searcher.setListener(this);
	}

	/**
	 * @return the jobs
	 */
	public NodesTable getNodesTable() {
		return (NodesTable) nodes.getUnderlyingTable();
	}

	/**
	 * 
	 */
	public void refresh(){
		searcher.refresh();
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.SearchListener#search(java.lang.String)
	 */
    @Override
    public void search(String filterParm) {
    	String filter = filterParm;
    	List<LightMemberSample> listData = null;
    	if ((filter == null) || (filter.length() == 0)){
    		listData = Instances.getLastSample().getMembers();
    	} else {
    	    listData = new ArrayList<LightMemberSample>();
    	    
    	    // to update as java pattern
    	    if (filter.contains("*")){
    	    	filter = filter.replace("*", ".*");
    	    }
    	    
    	    RegExp regEx = RegExp.compile(filter);
    	    
    	    for (LightMemberSample msample : Instances.getLastSample().getMembers()){
    	    	if (regEx.test(msample.getMemberLabel())){
    	    		listData.add(msample);
    	    	}
    	    }
    	}
    	nodes.getUnderlyingTable().setRowData(listData);
    }

}