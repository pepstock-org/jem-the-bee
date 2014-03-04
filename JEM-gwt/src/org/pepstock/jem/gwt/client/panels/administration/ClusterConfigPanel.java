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
import org.pepstock.jem.gwt.client.panels.administration.clusterconfig.ConfigPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;

/**
 * Component which shows the configuration files of JEM environment.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class ClusterConfigPanel extends AdminPanel implements ResizeCapable {
	
	private ConfigPanel config = new ConfigPanel();
	
	/**
	 * Creates configuration panel
	 *  
	 * @param nodes table of nodes 
	 */
	public ClusterConfigPanel() {
		super();
		add(config);
	}

	/**
	 * Called before showing. It loads the configuration files.
	 */
	public void load(){
		config.load();
	}
   
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
		config.onResize(getWidth(), getHeight());
    }
}