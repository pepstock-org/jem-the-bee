/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
package org.pepstock.jem.gwt.client.panels.swarm.commons;

import org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.swarm.NodeActionListener;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.Actions;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config.ConfigHeader;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config.GeneralEditConfig;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config.NetworkEditConfig;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.configuration.SwarmConfiguration;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Component which shows in edit mode the informations about the hazelcast swarm
 * configuration
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public final class EditConfigInspector extends AbstractTabPanelInspector implements
		NodeActionListener {

	private GeneralEditConfig genEditConf = null;

	private SwarmConfiguration swarmConfiguration = null;
	
	private TabPanel tabpanel = new TabPanel();
	
	private Actions action = new Actions();

	/**
	 * Construct the UI<br>
	 * 
	 * @param node
	 * @param swarmConfiguration
	 */
	public EditConfigInspector(SwarmConfiguration swarmConfiguration) {
		super(true);
		
		this.swarmConfiguration = swarmConfiguration;

		genEditConf = new GeneralEditConfig(swarmConfiguration);
		tabpanel.add(genEditConf, "General");

		
		NetworkEditConfig netEditConf = new NetworkEditConfig(swarmConfiguration);
		tabpanel.add(netEditConf, "Members");

		tabpanel.selectTab(0);

		action.setListener(this);
	}

	/**
	 * @return the swarmConfiguration
	 */
	public SwarmConfiguration getSwarmConfiguration() {
		return swarmConfiguration;
	}

	/**
	 * @param swarmConfiguration
	 *            the swarmConfiguration to set
	 */
	public void setSwarmConfiguration(SwarmConfiguration swarmConfiguration) {
		this.swarmConfiguration = swarmConfiguration;
	}

	/**
	 * 
	 */
	public void save() {
		if (genEditConf.validate()) {
			hide();
			Scheduler scheduler = Scheduler.get();
			scheduler.scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					Services.ROUTING_CONFIG_MANAGER.updateSwarmConfiguration(getSwarmConfiguration(), new UpdateSwarmConfigurationAsyncCallback());
				}
			});
		}
	}

	private static class UpdateSwarmConfigurationAsyncCallback extends ServiceAsyncCallback<SwarmConfiguration> {
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Update error!").show();
		}

		@Override
		public void onJemSuccess(SwarmConfiguration result) {
			if (result == null) {
				new Toast(MessageLevel.WARNING, "The result of routing configuration is empty!<br>Please contact your JEM adminitrator.", "Configuration empty!").show();
			}
		}

		@Override
		public void onJemExecuted() {
			// do nothing
		}
	}
	
	/**
	 * 
	 */
	public void cancel() {
		hide();

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getHeader()
	 */
    @Override
    public FlexTable getHeader() {
	    return new ConfigHeader(this);
    }


	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.NewAbstractInspector#getActions()
	 */
    @Override
    public Panel getActions() {
	    return action;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector#getTabPanel()
	 */
    @Override
    public TabPanel getTabPanel() {
	    return tabpanel;
    }

}