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
package org.pepstock.jem.gwt.client.panels.administration;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.administration.commons.BackListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.NodeInspectListener;
import org.pepstock.jem.gwt.client.panels.administration.nodesqueues.OverviewPanel;
import org.pepstock.jem.gwt.client.panels.administration.nodesqueues.inspector.InspectorPanel;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesQueuesPanel extends VerticalPanel implements NodeInspectListener, BackListener, ResizeCapable {
	
	private OverviewPanel overview = new OverviewPanel();
	
	private InspectorPanel inspector = new InspectorPanel(); 
	
	private int selected = 0;
	
	/**
	 * 
	 */
	public NodesQueuesPanel() {
		super();
		add(overview);
		overview.setListener(this);
		inspector.setListener(this);
	}
	
	/**
	 * 
	 */
	public void load(){
		if (selected == 1){
			back();
		}
		overview.load();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(LightMemberSample object) {
    	remove(overview);
    	add(inspector);
    	inspector.load(object.getMemberKey());
    	selected = 1;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.administration.commons.BackListener#back()
	 */
    @Override
    public void back() {
    	add(overview);
    	remove(inspector);
    	selected = 0;
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
    	overview.onResize(availableWidth, availableHeight);
    	inspector.onResize(availableWidth, availableHeight);
    }
    
}