/*******************************************************************************
 * Copyright (c) 2012-2014 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.preferences;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pepstock.jem.commands.util.HttpUtil;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;

/**
 * The dialog to create/edit coordinates from table preferences.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class CoordinateDialog extends Dialog implements ShellContainer {
	
	private static final int DEFAULT_WIDTH = 375;
	
	private static final int DEFAULT_MARGIN_VERTICAL = 15; 
	
	private static final int DEFAULT_MARGIN_HORIZONTAL = 10;

	private Text name;
	private Text host;
	private Text userid;
	private Text password;
	private Text restContext;
	private Button def;
	private Coordinate coordinate;
	private Map<String, Coordinate> map;

	/**
	 * Construct a new dialog for NEW coordinate.
	 * @param parentShell parent to use for messages
	 * @param map list of coordinates, already defined. Used for checking
	 */
	public CoordinateDialog(Shell parentShell, Map<String,Coordinate> map) {
		this(parentShell, null, map);
	}

	/**
	 * Construct a new dialog for UPDATE coordinate.
	 * @param parentShell parent to use for messages
	 * @param coordinate coordinate to update
	 * @param map list of coordinates, already defined. Used for checking
	 */
	public CoordinateDialog(Shell parentShell, Coordinate coordinate, Map<String,Coordinate> map) {
		super(parentShell);
		// uses the same styles of parent, adding resize
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.coordinate = coordinate;
		this.map = map;
	}

	/**
	 * Returns the coordinate instance. Could be null if we're adding new one.
	 * @return the coordinate. Could be null if we're adding new one.
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	/**
	 * Returns <code>true</code> if dialog is open for NEW coordinate 
	 * @return <code>true</code> if dialog is open for NEW coordinate
	 */
	public boolean isAdding(){
		return coordinate == null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		updateButtonStatus();
	}
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    protected Control createDialogArea(Composite parent) {
    	// adds title of dialog
		getShell().setText((coordinate == null) ? "Add new JEM environment" : "Update "+coordinate.getName());

		// internal composite
		Composite main = new Composite(parent, SWT.LEFT);
		GridLayout mainLayout = new GridLayout(1, false);
		mainLayout.marginTop = DEFAULT_MARGIN_VERTICAL;
		mainLayout.marginBottom = DEFAULT_MARGIN_VERTICAL;
		mainLayout.marginLeft = DEFAULT_MARGIN_HORIZONTAL;
		mainLayout.marginRight = DEFAULT_MARGIN_HORIZONTAL;
		mainLayout.marginWidth = 0;
		mainLayout.marginHeight = 0;
		main.setLayout(mainLayout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.minimumWidth = DEFAULT_WIDTH;
		main.setLayoutData(data);

		// label with action
		Label descLabel = new Label(main, SWT.LEFT);
		descLabel.setText("Insert the JEM http host, rest context, userid and password:");

		// composites with all labels and text fields
		Composite composite = new Composite(main, SWT.LEFT);
		GridLayout compLayout = new GridLayout(2, false);
		compLayout.verticalSpacing = DEFAULT_MARGIN_HORIZONTAL;
		composite.setLayout(compLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// host
		Label hostLabel = new Label(composite, SWT.NONE);
		hostLabel.setText("*Host:");
		host = new Text(composite, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// adds value if is not adding
		if (!isAdding()) {
			host.setText(coordinate.getHost());
		}
		// adds listener to update buttons status
		host.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
			}
		});

		// rest context
		Label restContextLabel = new Label(composite, SWT.NONE);
		restContextLabel.setText("*REST context:");
		restContext = new Text(composite, SWT.BORDER);
		restContext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// adds value if is not adding
		// or prepares the default
		if (!isAdding()) {
			restContext.setText(coordinate.getRestContext());
		} else {
			restContext.setText(Coordinate.DEFAULT_REST_WEB_CONTEXT);
		}
		// adds listener to update buttons status
		restContext.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
			}
		});
		
		// NAME of JEM enviroment
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText("Name:");
		name = new Text(composite, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// if already exists, you CAN'T CHANGE! is the key
		if (!isAdding()) {
			name.setText(coordinate.getName());
			name.setEnabled(false);
		}

		// userid
		Label useridLabel = new Label(composite, SWT.NONE);
		useridLabel.setText("Userid:");
		userid = new Text(composite, SWT.BORDER);
		userid.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// adds value if is not adding and not null
		if (!isAdding() && (coordinate.getUserid() != null)) {
			userid.setText(coordinate.getUserid());
		}
		// adds listener to update password
		// text field because yu can inserted a password
		// without a user
		userid.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (userid.getText().length() > 0){
					password.setEnabled(true);
				} else {
					// removes password text
					password.setEnabled(false);
					password.setText("");
				}
			}
		});

		// password
		Label pwdLabel = new Label(composite, SWT.NONE);
		pwdLabel.setText("Password:");
		password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// adds value if is not adding and not null
		// otherwise is set to unable!
		if (!isAdding() && coordinate.getPassword() != null) {
			password.setText(coordinate.getPassword());
		} else {
			password.setEnabled(false);
		}
		
		// DEAFULT check box
		def = new Button(composite, SWT.CHECK);
		def.setText("default");
		def.setToolTipText("Select default environment");
		if (!isAdding()) {
			def.setSelection(coordinate.isDefault());
		} else {
			// checks if there is already another coordinate
			// with default. if not, try to set this as default
			def.setSelection(!hasDefault());
		}

		// empty
		new Label(composite, SWT.NONE);
		
		// fields mandatory label
		Label mandatorylabel = new Label(composite, SWT.NONE);
		mandatorylabel.setText("* mandatory fields");
		new Label(composite, SWT.NONE);

		return main;
	}

    /**
     * Checks if there is already a coordinate set with default
     * @return <code>true</code> if there is already a coordinate set with default
     */
	private boolean hasDefault() {
		for (Coordinate coordinate : map.values()) {
			if (coordinate.isDefault()){
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets OK button enabled if host and rest context are set 
	 */
	private void updateButtonStatus() {
		boolean enabled = (host.getText().length() > 0) && (restContext.getText().length() > 0);
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
    @Override
    protected void okPressed() {
		// check is host is correct
		URI uri;
        try {
        	// creates a URI to see if is a URL
	        uri = new URI(host.getText());
	        // checks teh scheme
			if (uri.getScheme() == null) {
				Notifier.showMessage(this, "Invalid URI", "Host property '"+host.getText()+"' is not a valid URI.", MessageLevel.ERROR);
				return;
			}
        } catch (URISyntaxException e) {
        	LogAppl.getInstance().ignore(e.getMessage(), e);
        	Notifier.showMessage(this, "Invalid URI", "Host property '"+host.getText()+"' is not a valid URI :"+ e.getMessage(), MessageLevel.ERROR);
        }
		
        // checks environment name
		String envNameLoaded = null;
		String envName = name.getText();
		boolean hasName = envName.trim().length() > 0;
		// checks if has name and if is adding a new coordinate
		if (hasName && isAdding() && map.containsKey(envName)){
			Notifier.showMessage(this, "Unable to use environment name!", "Environment name '"+envName+"' is already defined.", 
					MessageLevel.ERROR);
			return;
		}
		// loads environment name by HTTP call
		try {
			envNameLoaded = HttpUtil.getGroupName(host.getText());
			if (!hasName){
				// if name not inserted, used envName loaded
				envName = envNameLoaded;
			} else {
				// checks if they are equals
				if (envName.equalsIgnoreCase(envNameLoaded)){
					// assigned to mantain cases used in JEM
					envName = envNameLoaded;
				} else {
					// not equals! Error
					Notifier.showMessage(this, "Mismatch between environment names!", "Inserted name is '"+envName+"' but JEM, at '"+host.getText()+"', is '"+envNameLoaded+"'!", 
							MessageLevel.ERROR);
					return;
				}
			}
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// if no name, return because it doesn't have the name
			if (!hasName){
				Notifier.showMessage(this, "Unable to load environment!", "Unable to load environment name: "+e.getMessage(), 
						MessageLevel.ERROR);
				return;
			} else {
				// uses the name inserted without any check
				Notifier.showMessage(this, "Unable to check environment!", "Unable to check environment name! '"+envName+"' is used.", 
						MessageLevel.WARNING);
			}
		}
		
		// new coordinate??
		if (isAdding()) {
			coordinate = new Coordinate();
		}
		coordinate.setName(envName);
		coordinate.setHost(host.getText());
		coordinate.setUserid(userid.getText());
		coordinate.setPassword(password.getText());
		coordinate.setRestContext(restContext.getText());
		coordinate.setDefault(def.getSelection());
		super.okPressed();
	}

}
