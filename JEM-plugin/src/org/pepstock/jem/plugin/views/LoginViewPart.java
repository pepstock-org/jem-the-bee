/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.preferences.Coordinate;
import org.pepstock.jem.plugin.preferences.PreferencesManager;
import org.pepstock.jem.plugin.util.LoginLoading;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;

/**
 * Implements a view part which needs the login, using the environment header.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class LoginViewPart extends JemViewPart implements ShellContainer, IPartListener2 {

	private EnvironmentHeader envHeader = null;
	
	private TabFolder tabFolder;
	
	private IWorkbenchWindow window = null;
	
	// this is a static field, just using at startup or at first
	// activation of viewpart. This is to avoid multiple
	// request to login to JEM, switch thru different
	// JEM View parts
	private static boolean autoStartUp = true;
	
	/**
	 * Adds it self to listen part listener
	 */
    public LoginViewPart() {
	    window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null){
	        window.getActivePage().addPartListener(this);
	    }
    }
	/**
	 * @return the autoStartUp
	 */
	public static boolean isAutoStartUp() {
		return autoStartUp;
	}

	/**
	 * @param autoStartUp the autoStartUp to set
	 */
	public static void setAutoStartUp(boolean autoStartUp) {
		LoginViewPart.autoStartUp = autoStartUp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
	 */
    @Override
    public void init(IViewSite site) throws PartInitException {
    	super.init(site);
    	// adds teh view part to broker
		JemBroker.addViewPartID(getId());
    }
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.JemViewPart#dispose()
	 */
    @Override
    public void dispose() {
	    super.dispose();
	    // removes as preferences listener
	    PreferencesManager.removeEnvironmentEventListener(envHeader);
	    JemBroker.removeViewPartID(getId());
	    // if is last, logout from JEM
	    if (JemBroker.isLastViewPart()){
	    	try {
	    		Client.getInstance().logout();
	    	} catch (Exception e) {
	    		LogAppl.getInstance().ignore(e.getMessage(), e);
	    	}	    
	    }
    }

    /**
     * Checks at startup time if there is a default coordinate to use
     * to connect to JEM automatically
     */
	private void checkDefaultAtStartUp(){
		for (Coordinate coordinate : PreferencesManager.ENVIRONMENTS.values()){
			if (coordinate.isDefault()){
				// loaidng for login
				LoginLoading loading = new Login(getShell(), coordinate);
				loading.run();
				return;
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		parent.setLayout(layout);
		
		// adds environment header
		envHeader = new EnvironmentHeader(parent);
		
		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));

		loadViewPart(tabFolder);
		
		tabFolder.setSelection(0);
		if (Client.getInstance().isLogged()){
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}
    
	/**
	 * Returns the view part ID
	 * @return the view part ID
	 */
	public abstract String getId();
    /**
     * Loads all composites inside of tab folder
     * @param tabFolder tab folder,parent of all composites to load
     */
    public abstract void loadViewPart(TabFolder tabFolder);
    
	/**
	 * Enables or disables view part
	 * @param enabled <code>true</code> to enable
	 */
	public void setEnabled(final boolean enabled){
		tabFolder.setVisible(enabled);
	}
	

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.views.JemViewPart#getSelectedObjectName()
	 */
    @Override
    public String getSelectedObjectName() {
		Coordinate coordinate = Client.getInstance().getCurrent();
		if ((coordinate != null) && Client.getInstance().isLogged()){
			return coordinate.getName();
		}
		return null;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.ShellContainer#getShell()
	 */
    @Override
    public Shell getShell() {
	    return tabFolder.getShell();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentConnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentConnected(EnvironmentEvent event) {
    	super.environmentConnected(event);
    	setEnabled(true);
    	updateName();
	    envHeader.environmentConnected(event);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentDisconnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentDisconnected(EnvironmentEvent event) {
    	super.environmentDisconnected(event);
    	setEnabled(false);
    	updateName();
    	envHeader.environmentDisconnected(event);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partActivated(IWorkbenchPartReference arg0) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partBroughtToTop(IWorkbenchPartReference arg0) {
    	// checks if logged
    	if (!Client.getInstance().isLogged() && window.getActivePage().isPartVisible(this)){
    		checkDefaultAtStartUp();
    	}
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partClosed(IWorkbenchPartReference arg0) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partDeactivated(IWorkbenchPartReference arg0) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partHidden(IWorkbenchPartReference arg0) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partInputChanged(IWorkbenchPartReference arg0) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partOpened(IWorkbenchPartReference arg0) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
	 */
    @Override
    public void partVisible(IWorkbenchPartReference arg0) {
    	if (LoginViewPart.isAutoStartUp()){
        	if (!Client.getInstance().isLogged()){
        		checkDefaultAtStartUp();
        	}
        	LoginViewPart.setAutoStartUp(false);
    	}
    }
    
    /**
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private static class Login extends LoginLoading{
		/**
		 * @param shell
		 * @param coordinate
		 */
        public Login(Shell shell, Coordinate coordinate) {
	        super(shell, coordinate);
        }

		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
        public void execute() throws JemException {
			try {
				Coordinate choosed = getCoordinate();
				// checks if coordiante has got password. If not, asks for
				if ((choosed.getPassword() == null) || (choosed.getPassword().length() == 0)){
					LoginDialog dialog = new LoginDialog(super.getShell(), getCoordinate());
					if (dialog.open() == Dialog.OK) {
						choosed = dialog.getCoordinate();
					} else {
						// if cancel, nop
						return;
					}
				}
				Client.getInstance().login(choosed);
			} catch (JemException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(super.getShell(), "Unable to login to "+getCoordinate().getName()+"!",
						"Error occurred during login to '"+getCoordinate().getName()+"': "+e.getMessage(), MessageLevel.ERROR);
			}
		}

    }
}
