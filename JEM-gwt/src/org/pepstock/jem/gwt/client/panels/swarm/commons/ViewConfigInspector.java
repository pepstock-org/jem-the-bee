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
package org.pepstock.jem.gwt.client.panels.swarm.commons;

import org.pepstock.jem.gwt.client.commons.AbstractTabPanelInspector;
import org.pepstock.jem.gwt.client.panels.swarm.NodeActionListener;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.Actions;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config.ConfigHeader;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config.GeneralViewConfig;
import org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config.NetworkViewConfig;
import org.pepstock.jem.node.configuration.SwarmConfiguration;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Component which shows hazelcast swarm configuration informations
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0	
 *
 */
public class ViewConfigInspector extends AbstractTabPanelInspector implements NodeActionListener {
	
	private SwarmConfiguration swarmConfiguration = null;
	
	private TabPanel tabpanel = new TabPanel();
	
	private Actions action = new Actions(true);
	
	/**
	 * Construct the UI<br>
	 * 
	 * @param swarmConfiguration 
	 */
	public ViewConfigInspector(SwarmConfiguration swarmConfiguration){
		this.swarmConfiguration = swarmConfiguration;
		
		GeneralViewConfig gen = new GeneralViewConfig(swarmConfiguration);
		tabpanel.add(gen, "General");

		
		NetworkViewConfig net = new NetworkViewConfig(swarmConfiguration);
		tabpanel.add(net, "Members");

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
	 * @param swarmConfiguration the swarmConfiguration to set
	 */
	public void setSwarmConfiguration(SwarmConfiguration swarmConfiguration) {
		this.swarmConfiguration = swarmConfiguration;
	}
	
	
	/**
	 * 
	 */
	public void cancel(){
		hide();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.panels.swarmnodes.NodeActionListener#save()
	 */
    @Override
    public void save() {
	    cancel();
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