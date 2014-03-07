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
package org.pepstock.jem.gwt.client.panels.nodes;

import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.panels.components.AbstractActionsButtonPanel;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * Component with buttons to perform actions on selected nodes.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodesActions extends AbstractActionsButtonPanel<NodeInfoBean> {

	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	/**
	 * 
	 */
    public NodesActions() {
	    super();
	    init();
    }

	@Override
	protected void initButtons() {
		addStartButton();
		addDrainButton();
	}

	private void addStartButton() {
		// checks if user has the permission to START job
		if (ClientPermissions.isAuthorized(Permissions.NODES, Permissions.NODES_START)) {
			Button startButton = new Button("Start", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					@SuppressWarnings("unchecked")
					MultiSelectionModel<NodeInfoBean> selectionModel = (MultiSelectionModel<NodeInfoBean>) getUnderlyingTable().getTable().getSelectionModel();
					if (selectionModel.getSelectedSet().isEmpty()) {
						// do nothing
						new Toast(MessageLevel.WARNING, "No node is selected and it's not possible to perform START command.", "No node selected!").show();
						return;
					}
					// do!
					start(selectionModel.getSelectedSet());
					selectionModel.clear();
				}
			});
			add(startButton);
			startButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(startButton, "Start the nodes");
		}
	}

	private void addDrainButton() {
		// checks if user has the permission to DRAIN job
		if (ClientPermissions.isAuthorized(Permissions.NODES, Permissions.NODES_DRAIN)) {
			Button drainButton = new Button("Drain", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// gets the selected jobs
					@SuppressWarnings("unchecked")
					MultiSelectionModel<NodeInfoBean> selectionModel = (MultiSelectionModel<NodeInfoBean>) getUnderlyingTable().getTable().getSelectionModel();
					if (selectionModel.getSelectedSet().isEmpty()) {
						new Toast(MessageLevel.WARNING, "No node is selected and it's not possible to perform DRAIN command.", "No node selected!").show();
						return;
					}
					// do!
					drain(selectionModel.getSelectedSet());
					// clear selection
					selectionModel.clear();
				}
			});
			add(drainButton);
			drainButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(drainButton, "Pause the nodes");
		}
	}


	/**
	 * @param nodes collection of nodes to drain
	 */
	private void drain(final Collection<NodeInfoBean> nodes) {
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.NODES_MANAGER.drain(nodes, new DrainAsyncCallback());
			}
	    });
	}

	private class DrainAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			// if has success, refresh the data, to see in table that they are drained
			if (getSearcher() != null){
				getSearcher().refresh();
			}
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Drain command error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
	/**
	 * @param nodes collection of nodes to start
	 */
	private void start(final Collection<NodeInfoBean> nodes) {
		Loading.startProcessing();
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.NODES_MANAGER.start(nodes, new StartAsyncCallback());
			}
	    });

	}

	private class StartAsyncCallback extends ServiceAsyncCallback<Boolean> {
		@Override
		public void onJemSuccess(Boolean result) {
			// if has success, refresh the data, to see in table that they are started
			if (getSearcher() != null){
				getSearcher().refresh();
			}
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Start command error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
}