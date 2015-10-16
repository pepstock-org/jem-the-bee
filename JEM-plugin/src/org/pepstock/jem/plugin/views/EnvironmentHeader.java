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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.OrganizationalUnit;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.event.EnvironmentEvent;
import org.pepstock.jem.plugin.event.EnvironmentEventListener;
import org.pepstock.jem.plugin.event.PreferencesEnvironmentEventListener;
import org.pepstock.jem.plugin.preferences.Coordinate;
import org.pepstock.jem.plugin.preferences.PreferencesManager;
import org.pepstock.jem.plugin.util.Images;
import org.pepstock.jem.plugin.util.Loading;
import org.pepstock.jem.plugin.util.LoginLoading;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.plugin.util.ShellLoading;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.util.Numbers;

/**
 * The header of view part with environment connection information and 
 * with combo to choose JEM environment to connect and disconnect.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class EnvironmentHeader extends Composite implements ShellContainer, EnvironmentEventListener, PreferencesEnvironmentEventListener{
	
	private static final String UNCONNECTED= " ";
	
	private static final String NOT_AVAILABLE = "n/a";
	
	private Combo envs = null;
	
	private Label icon;
	
	private Label groupId;
	
	private Label userId;
	
	private Link logoff = null;


	/**
	 * Creates the object with parent composite
	 * @param parent container of this component
	 */
    public EnvironmentHeader(Composite parent) {
	    super(parent, SWT.NONE);
	    // sets layout and data
	    setLayout(new GridLayout(Numbers.N_2, false));
	    setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 0, 0));

	    // creates a container with 5 columns
	    Composite left = new Composite(this, SWT.NONE);
	    left.setLayout(new GridLayout(Numbers.N_5, false));
	    left.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false, 0, 0));

	    // adds ICON
	    // at column 1
	    icon = new Label(left, SWT.NONE);
	    icon.setImage(Images.OFFLINE_FAVICON);
	    
	    // adds USER
	    // at column 2 and 3
		Label uidLabel = new Label(left, SWT.NONE);
		uidLabel.setText("User: ");
		uidLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		userId = new Label(left, SWT.NONE);
		userId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userId.setText(NOT_AVAILABLE);
		userId.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		// changes the font of the label
	    FontData fontData = userId.getFont().getFontData()[0];
	    Font font = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.ITALIC | SWT.BOLD));
		userId.setFont(font);
		
		// adds GROUP
		// at column 4 and 5
		Label gidLabel = new Label(left, SWT.NONE);
		gidLabel.setText("Group: ");
		gidLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		groupId = new Label(left, SWT.NONE);
		groupId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		groupId.setText(NOT_AVAILABLE);
		groupId.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
		groupId.setFont(font);
	    
		// RIGHT component
		// with 3 columns
	    Composite right = new Composite(this, SWT.NONE);
	    right.setLayout(new GridLayout(Numbers.N_3, false));
	    right.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, true, false, 0, 0));
	    
	    // ADDS label for combo
	    // column 1 and 2
	    // with the combobox with all known JEM environment
		Label numberLabel = new Label(right, SWT.NONE);
		numberLabel.setText("JEM environment: ");
		numberLabel.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
    	
		//loads combo with all JEM coordinates
		envs = new Combo(right, SWT.READ_ONLY);
		// ALWAYS the item at 0 is BLANK, used when if logged off
		envs.add(UNCONNECTED);
		for(Coordinate coordinate : PreferencesManager.ENVIRONMENTS.values()){
			envs.add(coordinate.getName());
			// sets name as key of object
			envs.setData(coordinate.getName(), coordinate);
		}
		envs.addSelectionListener(new EnvironmentSelect());
		
		// adds LOGOFF link
		// at column 3
		logoff = new Link(right, SWT.NONE);
		// is a HTMLlike,
		// it appears as a HTML anchor link
		logoff.setText("<a>Logoff</a>");
		logoff.setEnabled(false);
		logoff.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// calls the logout action
				Loading loading = new Logout(getShell());
				loading.run();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// nop
			}
		});
		// adds itself has event listener
		PreferencesManager.addEnvironmentEventListener(this);
		// checks if logged, at startup of components
		if (Client.getInstance().isLogged()){
			loggedOn(Client.getInstance().getCurrent());
		} else {
			loggedOff();
		}
    }
    
    /**
     * Logs on JEM, using the passed coordinate
     * @param coordinate JEM environment coordinate
     */
    private void loggedOn(Coordinate coordinate){
    	// enables all components
    	icon.setImage(Images.ONLINE_FAVICON);
    	
    	// gets userid and groupid
    	LoggedUser user = Client.getInstance().getUser();
    	OrganizationalUnit ou = user.getOrganizationalUnit();
    	userId.setText(user.getName()+"["+user.getId()+"]");
		groupId.setText(ou.getName()+"["+ou.getId()+"]");
		// must be redraw
		userId.getParent().pack();
		
		// enables and disables buttons
		logoff.setEnabled(true);
		envs.setEnabled(false);

		// selects the right item in combo
    	String[] items = envs.getItems();
    	for (int i=0; i<items.length; i++){
    		// if item is equals with
    		// the coordinate name. then sets the
    		// environment
    		if (coordinate.getName().equalsIgnoreCase(items[i])){
    			envs.select(i);
    			return;
    		}
    	}
    }
    
    /**
     * Logs off from JEM
     */
    private void loggedOff(){
    	// disables everything
    	icon.setImage(Images.OFFLINE_FAVICON);
    	userId.setText(NOT_AVAILABLE);
		groupId.setText(NOT_AVAILABLE);
		// must be redraw
		userId.getParent().pack();
		
		logoff.setEnabled(false);
		envs.setEnabled(true);
		// select empty item
		envs.select(0);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentConnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentConnected(EnvironmentEvent event) {
    	// logs on with the new coordinate
    	loggedOn(event.getCoordinate());
    }


	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.EnvironmentEventListener#environmentDisconnected(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentDisconnected(EnvironmentEvent event) {
    	// logs off from the current environment
    	loggedOff();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.PreferencesEnvironmentEventListener#environmentAdded(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentAdded(EnvironmentEvent event) {
    	Coordinate coordinate = event.getCoordinate();
    	// adds to the env the name and coordinate of new 
    	// coordinate, inserted by the user
    	envs.add(coordinate.getName());
    	envs.setData(coordinate.getName(), coordinate);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.PreferencesEnvironmentEventListener#environmentRemoved(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentRemoved(EnvironmentEvent event) {
    	Coordinate coordinate = event.getCoordinate();
    	// removes from the env by the name of coordinate, 
    	// deleted by the user
    	envs.remove(coordinate.getName());
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.event.PreferencesEnvironmentEventListener#environmentUpdated(org.pepstock.jem.plugin.event.EnvironmentEvent)
	 */
    @Override
    public void environmentUpdated(EnvironmentEvent event) {
    	Coordinate coordinate = event.getCoordinate();
    	// updates the environment by the name of changed environment
    	envs.setData(coordinate.getName(), coordinate);
    }
    
    /**
     * Action to log in to JEM, using the coordinate passed at constructor.
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class Login extends LoginLoading {
    	
		/**
		 * Creates the object using the shell container and the coordinate 
		 * of JEM to log in
		 *  
		 * @param shell container of loading
		 * @param coordinate coordinate of JEM to log in
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
				// performs the login by REST
				Client.getInstance().login(getCoordinate());
			} catch (RestException e) {
				// if any error occurs
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(getShell(), "Unable to login to "+getCoordinate().getName()+"!", 
						"Error occurred during login to '"+getCoordinate().getName()+"': "+e.getMessage(), MessageLevel.ERROR);
				envs.select(0);
			}
		}
    }
    
    /**
     * Action to log out from JEM, using the current coordinate used
     * for previous login.
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class Logout extends ShellLoading{
    	
		/**
		 * Creates the action using the container shell, where the logout is called from.
		 * 
		 * @param shell container where the shell is called from
		 */
        public Logout(Shell shell) {
	        super(shell);
        }

		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
        public void execute() throws JemException {
			try {
				// disables the environment handler, during the logoff
				// to avoid any action
				EnvironmentHeader.this.setEnabled(false);
				// logoff from JEM by REST
				Client.getInstance().logout();
			} catch (JemException e) {
				// if any error occurs during the logoff
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(getShell(), "Unable to logoff!", e.getMessage(), MessageLevel.WARNING);
			} finally {
				// sets enable again the environment header
				EnvironmentHeader.this.setEnabled(true);
			}
		}
    }
    
    /**
     * Listener of environment when this is selected from combo box of header
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private class EnvironmentSelect implements SelectionListener{
    	
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
        @Override
        public void widgetSelected(SelectionEvent event) {
        	// checks if the event went from combo box
			if (event.getSource() instanceof Combo){
				Combo combo = (Combo)event.getSource();
				// gets the selected coordinate
				if (combo.getSelectionIndex() > 0){
					String item = combo.getItem(combo.getSelectionIndex());
					// copies the coordinate because one of them could be changed
					Coordinate choosed = (Coordinate)combo.getData(item);
					Coordinate coordinate = choosed;
					// if coordinate doesn't have any password, 
					// asks for user and password
					if ((choosed.getPassword() == null) || (choosed.getPassword().length() == 0)){
						// login dialog if no password
						LoginDialog dialog = new LoginDialog(getShell(), choosed);
						// if user pressed OK
						if (dialog.open() == Dialog.OK) {
							coordinate = dialog.getCoordinate();
						} else {
							// if user pressed CANCEL
							// then logoff from JEM!
							loggedOff();
							return;
						}
					}

					// disable the composite to avoid
					// any action during the login
					EnvironmentHeader.this.setEnabled(false);
					// login!
					LoginLoading loading = new Login(EnvironmentHeader.this.getShell(), coordinate);
					loading.run();
					// enable again the enviroment
					EnvironmentHeader.this.setEnabled(true);
				}
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
			// nop
		}
    }
}