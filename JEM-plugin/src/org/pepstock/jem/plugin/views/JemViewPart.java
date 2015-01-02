/*******************************************************************************
* Copyright (C) 2012-2015 pepstock.org.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     Andrea "Stock" Stocchero
******************************************************************************/
package org.pepstock.jem.plugin.views;

import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.event.EnvironmentEventListener;
import org.pepstock.jem.plugin.util.ShellContainer;

/**
 * Abstract implementation of view part. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class JemViewPart extends ViewPart implements EnvironmentEventListener, ShellContainer{
	
	private String internalTitle = null;
   
    /**
	 * @return the internalTitle
	 */
	public String getInternalTitle() {
		return internalTitle;
	}
	
	/**
	 * Returns a string which represents the selected object for title of view part
	 * @return string which represents the selected object
	 */
	public abstract String getSelectedObjectName();


	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
	 */
    @Override
    public void init(IViewSite site) throws PartInitException {
		super.init(site);
		// saves title defined in plugin
		internalTitle = getTitle();
		updateName();
		// adds itself as listener
		Client.getInstance().addEnvironmentEventListener(this);
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
    @Override
    public void dispose() {
    	// when it's closing, change the name an remove itself as listener
    	updateName();
		super.dispose();
		Client.getInstance().removeEnvironmentEventListener(this);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
    @Override
    public void setFocus() {
		getShell().setFocus();
	}

	/**
	 * Updates name of view part name
	 */
	public void updateName(){
		String name = getSelectedObjectName();
		if (name != null){
			name = internalTitle+": "+name;
		} else {
			name = internalTitle;
		}
		setPartName(name);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentConnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentConnected(EnvironmentEvent event) {
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentDisconnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentDisconnected(EnvironmentEvent event) {
    }

}
