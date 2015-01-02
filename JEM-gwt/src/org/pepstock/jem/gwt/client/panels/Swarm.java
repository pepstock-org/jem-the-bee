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
package org.pepstock.jem.gwt.client.panels;

import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.panels.administration.commons.NodeInspectListener;
import org.pepstock.jem.gwt.client.panels.common.GetQueueAsyncCallback;
import org.pepstock.jem.gwt.client.panels.components.BasePanel;
import org.pepstock.jem.gwt.client.panels.components.CommandPanel;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.gwt.client.panels.swarm.NodesActions;
import org.pepstock.jem.gwt.client.panels.swarm.NodesSearcher;
import org.pepstock.jem.gwt.client.panels.swarm.NodesTable;
import org.pepstock.jem.gwt.client.panels.swarm.commons.NodeInfoInspector;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Main panel of swarm nodes manager. 
 * Shows the list of nodes defined with the possibilities to act on them.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Swarm extends BasePanel<NodeInfoBean> implements SearchListener, InspectListener<NodeInfoBean> {
	
	/**
	 * Constructs all UI 
	 */
	public Swarm() {
		super(new TableContainer<NodeInfoBean>(new NodesTable()),
				new CommandPanel<NodeInfoBean>(new NodesSearcher(), new NodesActions()));
		getTableContainer().getUnderlyingTable().setInspectListener(this);
	}

	/**
	 * @see test.client.main.JobsSearchListener#search(java.lang.String)
	 */
	@Override
	public void search(final String filter) {
		getCommandPanel().getSearcher().setEnabled(false);
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.SWARM_NODES_MANAGER.getNodesByFilter(filter, new GetQueueAsyncCallback<NodeInfoBean>(getTableContainer().getUnderlyingTable(), getCommandPanel().getSearcher()) {
					@Override
					public void onJemSuccess(Collection<NodeInfoBean> result) {
						super.onJemSuccess(result);
						// gets status every search
						NodesActions na = (NodesActions)getCommandPanel().getActions();
						na.getStatus();
					}
				});
			}
	    });
	}

	/**
	 * @see {@link NodeInspectListener#inspect(NodeInfoBean)}
	 */
	@Override
	public void inspect(NodeInfoBean object) {
		try {
			NodeInfoInspector inspector = new NodeInfoInspector(object);
			inspector.setWidth(Sizes.toString(Window.getClientWidth()*3/4));
			inspector.setHeight(Sizes.toString(Window.getClientHeight()*3/4));
			inspector.setModal(true);
			inspector.setTitle(object.getLabel());
			inspector.center();

			// adds itself listener for closing and refreshing the data
			inspector.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> arg0) {
					// do nothing
				}
			});
		} catch (Exception e) {
			LogClient.getInstance().warning(e.getMessage(), e);
			new Toast(MessageLevel.ERROR, e.getMessage(), "Inspect error!").show();
		} 
		
	}		

}