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
package org.pepstock.jem.gwt.client.panels.roles.inspector;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.ViewStackPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.commons.InputPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.AdministrationPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.CertificatesPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.GfsPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.JobsPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.NodesPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.ResourcesPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.RolesPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.SwarmPermissionsPanel;
import org.pepstock.jem.gwt.client.panels.roles.inspector.permissions.ViewPermissionsPanel;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Container of all permissions panel. 
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class PermissionsPanel extends SplitLayoutPanel implements InspectListener<String>, ResizeCapable{
	
	// common styles
	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private TreeOptions options = new TreeOptions();
	
	private ViewStackPanel viewStack = new ViewStackPanel();
	
	private VerticalPanel center = new VerticalPanel();
	
	private VerticalPanel west = new VerticalPanel();
	
	private Role role = null;

	/**
	 * Constructs whole UI using the role info passed by argument
	 * 
	 * @param role role instance
	 */
	public PermissionsPanel(Role role) {
		this.role = role;

		Label headerDomain = new Label("Domains");
		headerDomain.addStyleName(Styles.INSTANCE.common().bold());
		headerDomain.setHeight(Sizes.toString(InputPanel.LABEL_HEIGHT));
		west.add(headerDomain);
		
		options.setListener(this);
		west.add(options);
		addWest(west, Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE);
		
		// creates caption for views
		ViewPermissionsPanel viewPanel = new ViewPermissionsPanel(role);
		JobsPermissionsPanel jobsPanel = new JobsPermissionsPanel(role);
		RolesPermissionsPanel rolesPanel = new RolesPermissionsPanel(role);
		NodesPermissionsPanel nodesPanel = new NodesPermissionsPanel(role);
		SwarmPermissionsPanel swarmNodesPanel = new SwarmPermissionsPanel(role);
		ResourcesPermissionsPanel resourcesPanel = new ResourcesPermissionsPanel(role);
		GfsPermissionsPanel gfsPanel = new GfsPermissionsPanel(role);
		AdministrationPermissionsPanel adminPanel = new AdministrationPermissionsPanel(role);
		CertificatesPermissionsPanel certPanel = new CertificatesPermissionsPanel(role);
		
		viewStack.add(viewPanel);
		viewStack.add(jobsPanel);
		viewStack.add(nodesPanel);
		viewStack.add(swarmNodesPanel);
		viewStack.add(rolesPanel);
		viewStack.add(resourcesPanel);
		viewStack.add(gfsPanel);
		viewStack.add(adminPanel);
		viewStack.add(certPanel);
		
		viewStack.setSpacing(5);
		Label headerPermissions = new Label("Permissions");
		headerPermissions.addStyleName(Styles.INSTANCE.common().bold());
		headerPermissions.addStyleName(Styles.INSTANCE.common().marginLeft20());
		headerPermissions.setHeight(Sizes.toString(InputPanel.LABEL_HEIGHT));
		center.add(headerPermissions);
		center.add(viewStack);
		add(center);
		
		viewStack.showStack(0);
		
		
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
	    this.setWidth(Sizes.toString(availableWidth));
	    this.setHeight(Sizes.toString(availableHeight));
	    
	    int desiredWidth = availableWidth - Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE - Sizes.SPLIT_PANEL_SEPARATOR
	    		- viewStack.getSpacing() - viewStack.getSpacing();// spacing 
	    int desiredHeight = availableHeight - viewStack.getSpacing() - viewStack.getSpacing(); //spacing
	    desiredHeight -= InputPanel.LABEL_HEIGHT;

	    options.setSize(Sizes.toString(Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE), Sizes.toString(desiredHeight));
	    for (Widget w : viewStack.getWidgets()) {
	    	w.setSize(Sizes.toString(desiredWidth), Sizes.toString(desiredHeight));
	    	if (w instanceof ResizeCapable) {
	    		((ResizeCapable) w).onResize(desiredWidth, desiredHeight);
	    	}
	    }
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(String object) {
	   if (object.equalsIgnoreCase(TreeOptions.VIEWS)){
		   viewStack.showStack(0);
	   } else if (object.equalsIgnoreCase(TreeOptions.JOBS)){
		   viewStack.showStack(1);
	   } else if (object.equalsIgnoreCase(TreeOptions.NODES)){
		   viewStack.showStack(2);
	   } else if (object.equalsIgnoreCase(TreeOptions.SWARM)){
		   viewStack.showStack(3);
	   } else if (object.equalsIgnoreCase(TreeOptions.ROLES)){
		   viewStack.showStack(4);
	   } else if (object.equalsIgnoreCase(TreeOptions.RESOURCES)){
		   viewStack.showStack(5);
	   } else if (object.equalsIgnoreCase(TreeOptions.GFS)){
		   viewStack.showStack(6);
	   } else if (object.equalsIgnoreCase(TreeOptions.ADMINISTRATION)){
		   viewStack.showStack(7);
	   } else if (object.equalsIgnoreCase(TreeOptions.CERTIFICATES)){
		   viewStack.showStack(8);
		   
	   }
 
    }
}