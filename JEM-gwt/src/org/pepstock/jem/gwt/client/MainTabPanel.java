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
package org.pepstock.jem.gwt.client;

import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.Administration;
import org.pepstock.jem.gwt.client.panels.CommonResources;
import org.pepstock.jem.gwt.client.panels.Gfs;
import org.pepstock.jem.gwt.client.panels.HomePage;
import org.pepstock.jem.gwt.client.panels.Input;
import org.pepstock.jem.gwt.client.panels.Nodes;
import org.pepstock.jem.gwt.client.panels.Output;
import org.pepstock.jem.gwt.client.panels.Roles;
import org.pepstock.jem.gwt.client.panels.Routing;
import org.pepstock.jem.gwt.client.panels.Running;
import org.pepstock.jem.gwt.client.panels.Status;
import org.pepstock.jem.gwt.client.panels.Swarm;
import org.pepstock.jem.gwt.client.panels.components.BasePanel;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is main componet of JEM web app. It cointains all panels necesary to see and operate with JEM.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class MainTabPanel extends TabPanel implements ResizeCapable {

	static {
		Styles.INSTANCE.tabBar().ensureInjected();
		Styles.INSTANCE.tabPanel().ensureInjected();
	}

	/**
	 * Constructs all panels 
	 */
	public MainTabPanel() {
		
		// Home page is visible to all
		HomePage home = new HomePage();
		add(home, "Home", true);
		addPermissionDrivenPanels();
		// select home
		selectTab(0);
		
		addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Object obj = getWidget(event.getSelectedItem());
				if (obj instanceof BasePanel<?>){
					BasePanel<?> panel = (BasePanel<?>) obj;
					panel.search();
				}
			}
		});
	}
	
	private void addPermissionDrivenPanels() {
		// checks id user is authorized to see input tab	
		if (ClientPermissions.isAuthorized(Permissions.VIEW_INPUT)){
			Input inputQueueLayoutPanel = new Input();
			add(inputQueueLayoutPanel, "Input", false);
		}

		// checks id user is authorized to see running tab	
		if (ClientPermissions.isAuthorized(Permissions.VIEW_RUNNING)){
			Running runningQueueLayoutPanel = new Running(); 
			add(runningQueueLayoutPanel, "Running", false);
		}
		
		// checks id user is authorized to see output tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_OUTPUT)){
			Output output2QueueLayoutPanel = new Output(); 
			add(output2QueueLayoutPanel, "Output", false);
		}
		
		// checks id user is authorized to see routing tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_ROUTING)){
			Routing routingQueueLayoutPanel = new Routing();
			add(routingQueueLayoutPanel, "Routing", false);
		}
		
		// checks id user is authorized to see status tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_STATUS)){
			Status statusLayoutPanel = new Status();
			add(statusLayoutPanel, "Status", false);
		}
		
		// checks id user is authorized to see nodes tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_NODES)){
			Nodes nodesLayoutPanel = new Nodes();
			add(nodesLayoutPanel, "Nodes", false);
		}
		
		// checks id user is authorized to see swarm nodes tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_SWARM_NODES)){
			Swarm swarmNodesLayoutPanel = new Swarm();
			add(swarmNodesLayoutPanel, "Swarm", false);
		}
		
		// checks id user is authorized to see role tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_ROLES)){
			Roles roles = new Roles();
			add(roles, "Roles", true);
		}

		// checks id user is authorized to see role tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_RESOURCES)){
			CommonResources resource = new CommonResources();
			add(resource, "Resources", true);
		}
		
		// checks id user is authorized to see admin tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_GFS_EXPLORER)){
			Gfs v = new Gfs();
			add(v, "Explorer", true);
		}
		
		// checks id user is authorized to see admin tab
		if (ClientPermissions.isAuthorized(Permissions.VIEW_ADMIN)){
			Administration admin = new Administration();
			add(admin, "Administration", true);
		}
	}
	
	/**
	 * @see ResizeCapable#onResize(int, int)
	 */
	@Override
	public void onResize(int availableWidth, int availableHeight) {
		int desiredWidth = availableWidth 
			- Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT 
			- Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT 
			- Sizes.MAIN_TAB_PANEL_BORDER
			- Sizes.MAIN_TAB_PANEL_BORDER;
		int desiredHeight = availableHeight 
			- Sizes.TABBAR_HEIGHT_PX 
			- Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT 
			- Sizes.MAIN_TAB_PANEL_PADDING_BOTTOM 
			- Sizes.MAIN_TAB_PANEL_BORDER;
		
		for (int i=0; i<getWidgetCount(); i++) {
			Widget w = getWidget(i);
    		w.setSize(Sizes.toString(desiredWidth), Sizes.toString(desiredHeight));
			if (w instanceof ResizeCapable) {
				((ResizeCapable) w).onResize(desiredWidth, desiredHeight);
			}
		}
	}
}