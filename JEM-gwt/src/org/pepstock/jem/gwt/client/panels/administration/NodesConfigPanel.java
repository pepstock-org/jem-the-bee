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



import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.NodesSearcher;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.ConfigPanel;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.NodesList;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.Separator;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.SeparatorListener;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Nodes table container for nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesConfigPanel extends AdminPanel implements SearchListener, SeparatorListener, ResizeCapable {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private NodesSearcher searcher = new NodesSearcher(PreferencesKeys.ADMIN_NODES_CONFIGURATION);
	
	private HorizontalPanel secondRow = new HorizontalPanel();
	
	private NodesList list = new NodesList();
	
	private ConfigPanel config = new ConfigPanel();
	
	private Separator separator = new Separator();
	
	/**
	 * Creates the UI by the argument (the table)
	 *  
	 * @param nodes table of nodes 
	 */
	public NodesConfigPanel() {
		super();
		
		config.getEditorContainer().setModel(list.getSelectionModel());
		config.getEditorContainer().setSeparator(separator);
		
		// add the always visible searcher
		add(searcher);
		searcher.setSearchListener(this);

		// set the listeners
		list.setListener(config);
		separator.setListener(this);

		secondRow.add(list);
		secondRow.add(separator);
		secondRow.add(config);
		config.setVisible(false);

		// add the second row to the main panel
		add(secondRow);
	}

	/**
	 * 
	 */
	public void load(){
		// ignore
	}
    
	/**
	 * @return
	 */
	public NodesList getList() {
		return list;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.SearchListener#search(java.lang.String)
	 */
    @Override
    public void search(final String filter) {
		config.setVisible(false);
		searcher.setEnabled(false);
    	Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.NODES_MANAGER.getNodes(filter, new GetNodesAsyncCallback());
			}
	    });
    }

    private class GetNodesAsyncCallback extends ServiceAsyncCallback<Collection<NodeInfoBean>> {

    	@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Search error!").show();
		}

		@Override
		public void onJemSuccess(Collection<NodeInfoBean> result) {
			if (!separator.isOpen()){
				separator.setOpen(true);
			}
			// sets data to table to show it
			list.setRowData(result);
			if (result.size() == 1){
				NodeInfoBean node = result.iterator().next();
				list.getSelectionModel().setSelected(node, true);
			}
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			searcher.setEnabled(true);
        }
    }
    

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void changeSeparator(Boolean object) {
	   if (object){
		   list.setVisible(true);
	   } else {
		   list.setVisible(false);
	   }
	   internalResize(); 
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	internalResize();
    }
    
    private void internalResize() {
    	int height = getHeight() - 
    			Sizes.SEARCHER_WIDGET_HEIGHT;
    	
    	if (separator.isOpen()){
    		list.setSize(Sizes.toString(Sizes.NODE_LIST_WIDTH), Sizes.toString(height));
    		list.onResize(Sizes.NODE_LIST_WIDTH, height);
    	} 
    	
    	separator.setSize(Sizes.toString(Separator.WIDTH), Sizes.toString(height));

    	int configWidth = getWidth() -
    			(separator.isOpen() ? Sizes.NODE_LIST_WIDTH : 0) - 
    			Separator.WIDTH;
    	
		config.setSize(Sizes.toString(configWidth), Sizes.toString(height));
		config.onResize(configWidth, height);
    }

}