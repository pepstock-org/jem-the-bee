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
package org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config;

import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.DefaultInspectorItem;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.node.configuration.SwarmConfiguration;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NetworkViewConfig extends DefaultInspectorItem {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}

	private SwarmConfiguration swarmConfiguration = null;

	private FlexTable layoutNetwork = null;

	/**
	 * @param swarmConfiguration 
	 */
	public NetworkViewConfig(SwarmConfiguration swarmConfiguration) {

	    /*
	     * NETWORK INFO
	     */
	    VerticalPanel envVp = new VerticalPanel();
	    envVp.setWidth(Sizes.HUNDRED_PERCENT);

	    Label netLabel = new Label("Network information");
	    netLabel.setWidth(Sizes.HUNDRED_PERCENT);
	    netLabel.setStyleName(Styles.INSTANCE.inspector().title());
	    netLabel.addStyleName(Styles.INSTANCE.common().bold());
	    envVp.add(netLabel);

	    this.layoutNetwork = new FlexTable();
	    layoutNetwork.setCellPadding(10);
	    layoutNetwork.setWidth(Sizes.HUNDRED_PERCENT);

	    layoutNetwork.setHTML(0, 0, "Nodes");

	    envVp.add(layoutNetwork);
	    
	    // main
	    add(envVp);
	    
	    setSwarmConfiguration(swarmConfiguration);

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
	public final void setSwarmConfiguration(SwarmConfiguration swarmConfiguration) {
		this.swarmConfiguration = swarmConfiguration;
		loadConfiguration();
	}

	/**
	 * 
	 */
	private final void loadConfiguration() {
		List<String> nodes = swarmConfiguration.getNetworks();
	    for(int i=0; i<nodes.size(); i++){
	    	layoutNetwork.setHTML(i, 1, nodes.get(i));
	    }
	    UITools.setFlexTableStyles(layoutNetwork, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight(),
	    		Styles.INSTANCE.common().bold());
	    
	}

}