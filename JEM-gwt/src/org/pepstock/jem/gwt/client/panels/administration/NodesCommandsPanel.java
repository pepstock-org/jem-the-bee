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
package org.pepstock.jem.gwt.client.panels.administration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.NodesSearcher;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.CommandExecutor;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.NodesList;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.Separator;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.SeparatorListener;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.commands.LogCommand;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.commands.TopCommand;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.commands.ViewClusterCommand;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.nodescommand.CommandResultPanel;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Main panel to executes commands on nodes.<br>
 * You can choose a subset of nodes in the cluster to put in the list.<br>
 * When you choose a node, you can perform some commands and to see the result on editor.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesCommandsPanel extends AdminPanel implements SearchListener, InspectListener<NodeInfoBean>, SeparatorListener, ResizeCapable  {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	/**
	 * List of available commands
	 */
	public static final List<CommandExecutor> COMMANDS = new ArrayList<CommandExecutor>();
	static {
		COMMANDS.add(new LogCommand());
		COMMANDS.add(new TopCommand());
		COMMANDS.add(new ViewClusterCommand());
	}
	
	private int currentCommandExecutor = 0;
	
	private NodesSearcher searcher = new NodesSearcher(PreferencesKeys.ADMIN_NODES_COMMANDS);
	
	private HorizontalPanel secondRow = new HorizontalPanel();
	
	private NodesList list = new NodesList();
	
	private CommandResultPanel resultPanel = new CommandResultPanel(this);
	
	private Separator separator = new Separator();

	/**
	 * Constructs the panel 
	 */
	public NodesCommandsPanel() {
		super();
		
		// add the always visible searcher
		add(searcher);
		searcher.setSearchListener(this);

		// set the listeners
		list.setListener(this);
		separator.setListener(this);
		
		secondRow.add(list);
		secondRow.add(separator);
		secondRow.add(resultPanel);
		resultPanel.setVisible(false);

		//	add the second row to the main panel
		add(secondRow);
	}
	
	/**
	 * Called when the option is selected
	 */
	public void load(){
		// if there is already panel visible (already used previously)
		// performs again the searching 
		if (resultPanel.isVisible()){
			searcher.refresh();
		}
	}
    
	/**
	 * Returns the list of nodes
	 * 
	 * @return the list of nodes
	 */
	public NodesList getList() {
		return list;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.SearchListener#search(java.lang.String)
	 */
    @Override
    public void search(final String filter) {
		resultPanel.setVisible(false);
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

    /**
     * Executes a command in specific node.
     * 
     * @param object node instance
     * @param command index of command
     */
    public void execute(NodeInfoBean object, int command) {
		for (CommandExecutor executor : COMMANDS){
			if (executor.getIndex() == command){
				currentCommandExecutor = command;
				executor.execute(object, resultPanel);
			}
		}
		resultPanel.setVisible(true);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(NodeInfoBean object) {
	    execute(object, currentCommandExecutor);
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
    	
    	int resultWidth = getWidth() -
    			(separator.isOpen() ? Sizes.NODE_LIST_WIDTH : 0) - 
    			Separator.WIDTH;
    	
		resultPanel.setSize(Sizes.toString(resultWidth), Sizes.toString(height));
		resultPanel.onResize(resultWidth, height);
    }
}