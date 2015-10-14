/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
*     Enrico Frigo - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.jobs;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.views.LoginViewPart;
import org.pepstock.jem.plugin.views.jobs.input.InputTable;
import org.pepstock.jem.plugin.views.jobs.output.OutputTable;
import org.pepstock.jem.plugin.views.jobs.routing.RoutingTable;
import org.pepstock.jem.plugin.views.jobs.running.RunningTable;

/**
 * Is a viewPart, activated to show jobs queues. It has got the environment header to login and logoff to JEM environment.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JobsViewPart extends LoginViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = JobsViewPart.class.getName();
	
	private TabFolder tabFolder;
	
	private List<JobsTableContainer> tabs = new LinkedList<JobsTableContainer>();
	
    /* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.JemViewPart#getId()
	 */
    @Override
    public String getId() {
	    return ID;
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.LoginViewPart#loadViewPart(org.eclipse.swt.widgets.TabFolder)
	 */
    @Override
    public void loadViewPart(TabFolder tabFolder) {
    	// clear tabs
    	tabs.clear();
    	this.tabFolder = tabFolder;
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		tabFolder.setToolTipText("Jobs Queues");
    }
    
	/**
     * Due to user can have just a subset of all tab item (jobs queues),
     * here tab panel is created adding the authorized tab item.
     * @param container container of table
     * @param permission permission to check
     */
    private void createTabItem(JobsTableContainer container, String permission){
    	// checks permission ONLY if is logged.
    	// permissions are stored in LoggedUser
    	if (Client.getInstance().isLogged() && Client.getInstance().isAuthorized(permission)){
    		// creates columns for viewer
    		container.createViewer();
    		getSite().setSelectionProvider(container.getViewer());

    		// creates tab item
    		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
    		tabItem.setText(container.getName());
    		tabItem.setToolTipText(container.getName()+" queue");
    		tabItem.setControl(container.getComposite());
    		tabs.add(container);
    	}
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.LoginViewPart#setEnabled(boolean)
	 */
    @Override
    public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (!enabled){
					// clears and disposes everything 
					tabs.clear();
					for (TabItem item : tabFolder.getItems()){
						item.dispose();
					}
				} else {
					// creates here the tabs based on authorization of client
					createTabItem(new InputTable(tabFolder, SWT.SINGLE), Permissions.VIEW_INPUT);
					createTabItem(new RunningTable(tabFolder, SWT.SINGLE), Permissions.VIEW_RUNNING);
					createTabItem(new OutputTable(tabFolder, SWT.SINGLE), Permissions.VIEW_OUTPUT);
					createTabItem(new RoutingTable(tabFolder, SWT.SINGLE), Permissions.VIEW_ROUTING);
				}
			}
		});
	}
}
