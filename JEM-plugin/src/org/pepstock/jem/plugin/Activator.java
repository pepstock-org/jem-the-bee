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
package org.pepstock.jem.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.plugin.preferences.PreferencesManager;

/**
 * The activator class controls the plug-in life cycle.
 * This is the entry point of JEM plugin
 * @author Enrico Frigo
 * @version 1.4
 */
public class Activator extends AbstractUIPlugin implements IWorkbenchListener{

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "JEM-plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws JemException {
		try {
	        super.start(context);
        } catch (Exception e) {
	        throw new JemException(e.getMessage(), e);
        }
		// starts log
		LogAppl.getInstance();
		// sets plugin
		setPlugin(this);
		// create client
		Client.getInstance();
		try {
			// loads preferences if exists
			PreferencesManager.load();
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		// adds this a listener of workbench
		PlatformUI.getWorkbench().addWorkbenchListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws JemException {
		try{
			// logout 
			Client.getInstance().logout(true);
		}catch(Exception e){
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		//resets plugin 
		setPlugin(null);
		try {
	        super.stop(context);
        } catch (Exception e) {
        	throw new JemException(e.getMessage(), e);
        }
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * @param plugin the plugin to set
	 */
	private static void setPlugin(Activator plugin) {
		Activator.plugin = plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchListener#postShutdown(org.eclipse.ui.IWorkbench)
	 */
    @Override
    public void postShutdown(IWorkbench arg0) {
	    //NOP
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchListener#preShutdown(org.eclipse.ui.IWorkbench, boolean)
	 */
    @Override
    public boolean preShutdown(IWorkbench arg0, boolean arg1) {
		try {
			// loads preferences if exists
			PreferencesManager.store();
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
		}
		// always true
	    return true;
    }
}
