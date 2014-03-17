/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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
package org.pepstock.jem.gwt.client.panels.swarm;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.NodeStatusImages;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.panels.components.AbstractActionsButtonPanel;
import org.pepstock.jem.gwt.client.panels.swarm.commons.EditConfigInspector;
import org.pepstock.jem.gwt.client.panels.swarm.commons.ViewConfigInspector;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;

/**
 * Component with buttons to perform actions on swarm nodes.
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class NodesActions extends AbstractActionsButtonPanel<NodeInfoBean> {

	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private HTML statusLabel = new HTML();

	/**
	 * 
	 */
	public NodesActions() {
		super();
		init();
	}

	@Override
	protected void initButtons() {
		addStatus();
		addStartButton();
		addStopButton();
		addConfButton();
	}

	private void addStatus() {
		add(statusLabel);
		getStatus();
	}

	private void setStatus(String statusString) {
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		NodeStatusImages statusObject;
		if (statusString.equals(NodeStatusImages.UNKNOWN.toString())) {
			statusObject = NodeStatusImages.UNKNOWN;
		} else if (statusString.equals(NodeStatusImages.STARTING.toString())) {
			statusObject = NodeStatusImages.STARTING;
		} else if (statusString.equals(NodeStatusImages.INACTIVE.toString())) {
			statusObject = NodeStatusImages.INACTIVE;
		} else if (statusString.equals(NodeStatusImages.ACTIVE.toString())) {
			statusObject = NodeStatusImages.ACTIVE;
		} else if (statusString.equals(NodeStatusImages.DRAINED.toString())) {
			statusObject = NodeStatusImages.DRAINED;
		} else if (statusString.equals(NodeStatusImages.DRAINING.toString())) {
			statusObject = NodeStatusImages.DRAINING;
		} else if (statusString.equals(NodeStatusImages.SHUTTING_DOWN.toString())) {
			statusObject = NodeStatusImages.SHUTTING_DOWN;
		} else {
			statusObject = NodeStatusImages.INACTIVE;
		}

		sb.appendHtmlConstant("<table>");
		// adds a label for imgae
		sb.appendHtmlConstant("<tr><td align='left' valign='middle'>Swarm is</td><td width='5px'/><td>");
		// Add the contact image.
		String imageHtml = AbstractImagePrototype.create(statusObject.getImage()).getHTML();
		sb.appendHtmlConstant(imageHtml);
		sb.appendHtmlConstant("</td>");
		// Add the name and address.
		sb.appendHtmlConstant("<td align='left' valign='middle'>");
		sb.appendEscaped(statusString);

		// adds a empty space like a margin
		sb.appendHtmlConstant("</td><td width='15px'/></tr></table>");

		statusLabel.setHTML(sb.toSafeHtml());
	}

	private void addStartButton() {
		// checks if user has the permission to START swarm nodes
		if (ClientPermissions.isAuthorized(Permissions.SWARM, Permissions.SWARM_NODES_START)) {
			Button startButton = new Button("Start", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					start();
				}
			});
			add(startButton);
			startButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(startButton, "Start Swarm environment");
		}
	}

	private void addStopButton() {
		// checks if user has the permission to STOP swarm nodes
		if (ClientPermissions.isAuthorized(Permissions.SWARM, Permissions.SWARM_NODES_DRAIN)) {
			Button drainButton = new Button("Drain", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					drain();
				}
			});
			add(drainButton);
			drainButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(drainButton, "Stops Swarm environment");
		}
	}

	private void addConfButton() {
		// checks if user has the permission to EDIT swarm configuration
		if (ClientPermissions.isAuthorized(Permissions.SWARM, Permissions.SWARM_NODES_EDIT_CONFIG)) {
			Button editButton = new Button("Configure...", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					edit();
				}
			});
			add(editButton);
			editButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(editButton, "Edit Swarm configuration");
		} else if (ClientPermissions.isAuthorized(Permissions.SWARM, Permissions.SWARM_NODES_VIEW_CONFIG)) {
			Button viewButton = new Button("Configuration", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					view();
				}
			});
			add(viewButton);
			viewButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
			new Tooltip(viewButton, "View Swarm configuration");
		}
	}

	/**
	 * Stop all swarm nodes that belong to this environment. A Swarm node is an
	 * hazelcast node included in each web node that can either been start or
	 * shutdown
	 */
	private void drain() {
		Loading.startProcessing();

		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Services.SWARM_NODES_MANAGER.drain(new DrainAsyncCall());
			}
		});

	}

	/**
	 * Start all swarm nodes that belong to this environment. A Swarm node is an
	 * hazelcast node included in each web node that can either been start or
	 * shutdown
	 */
	private void start() {
		Loading.startProcessing();
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Services.SWARM_NODES_MANAGER.start(new StartAsyncCall());
			}
		});

	}

	/**
	 * Gets status of swarm changing the value
	 */
	public void getStatus() {
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Services.SWARM_NODES_MANAGER.getStatus(new GetStatusAsyncCall());
			}
		});

	}

	/**
	 * @param nodes
	 *            collection of nodes to start
	 */
	private void edit() {
		Loading.startProcessing();
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Services.ROUTING_CONFIG_MANAGER.getSwarmConfiguration(SwarmConfiguration.DEFAULT_NAME, new EditAsyncCall());
			}
		});

	}

	/**
	 * @param nodes
	 *            collection of nodes to start
	 */
	private void view() {
		Loading.startProcessing();
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				Services.ROUTING_CONFIG_MANAGER.getSwarmConfiguration(SwarmConfiguration.DEFAULT_NAME, new ViewAsyncCall());
			}
		});

	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class ActionAsyncCall extends ServiceAsyncCallback<Boolean> {

		private String action = null;

		/**
		 * @param action
		 */
		private ActionAsyncCall(String action) {
			super();
			this.action = action;
		}

		@Override
		public void onJemSuccess(Boolean result) {
			getStatus();
			// if has success, refresh the data, to see in table that they are
			// started
			if (getSearcher() != null) {
				getSearcher().refresh();
			}
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), action + " command error!").show();
		}

		@Override
		public void onJemExecuted() {
			Loading.stopProcessing();
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class DrainAsyncCall extends ActionAsyncCall {

		/**
		 * 
		 */
		private DrainAsyncCall() {
			super("Stop");
		}

		@Override
		public void onJemSuccess(Boolean result) {
			if (result) {
				new Toast(MessageLevel.INFO, "Swarm is stopped correctly!", "Swarm stopped!").show();
			} else {
				new Toast(MessageLevel.WARNING, "No swarm nodes detected!", "Stop command warning!").show();
			}
			super.onJemSuccess(result);
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class StartAsyncCall extends ActionAsyncCall {

		/**
		 * 
		 */
		private StartAsyncCall() {
			super("Start");
		}

		@Override
		public void onJemSuccess(Boolean result) {
			if (result) {
				new Toast(MessageLevel.INFO, "Swarm is started correctly!", "Swarm started!").show();
			} else {
				new Toast(MessageLevel.WARNING, "No swarm nodes detected!", "Start command warning!").show();
			}
			super.onJemSuccess(result);
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class GetStatusAsyncCall extends ServiceAsyncCallback<String> {

		@Override
		public void onJemSuccess(String result) {
			if (result != null) {
				setStatus(result);
			}
		}

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get Status error!").show();
		}

		@Override
		public void onJemExecuted() {
			// do nothing
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class EditAsyncCall extends ServiceAsyncCallback<SwarmConfiguration> {

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get configuration error!").show();
		}

		@Override
		public void onJemSuccess(SwarmConfiguration result) {
			if (result != null) {
				try {
					AbstractTabPanelInspector inspector = getInspector(result);
					inspector.setModal(true);
					inspector.setTitle("Swarm Configuration");
					inspector.center();
				} catch (Exception e) {
					LogClient.getInstance().warning(e.getMessage(), e);
					new Toast(MessageLevel.ERROR, e.getMessage(), "Config error!").show();
				}
			} else {
				new Toast(MessageLevel.WARNING, "The result of swarm configuration is empty!<br>Please contact your JEM adminitrator.", "Configuration empty!").show();
			}
		}

		@Override
		public void onJemExecuted() {
			Loading.stopProcessing();
		}

		public AbstractTabPanelInspector getInspector(SwarmConfiguration result) {
			return new EditConfigInspector(result);
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	class ViewAsyncCall extends EditAsyncCall {

		@Override
		public AbstractTabPanelInspector getInspector(SwarmConfiguration result) {
			return new ViewConfigInspector(result);
		}
	}

}