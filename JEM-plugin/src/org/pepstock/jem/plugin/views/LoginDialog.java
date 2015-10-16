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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pepstock.jem.plugin.preferences.Coordinate;
import org.pepstock.jem.plugin.util.Images;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.util.Numbers;

/**
 * The dialog to insert userid and password, to connect to JEM 
 * when the coordinate doesn't have any password
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class LoginDialog extends Dialog implements ShellContainer {
	
	// sets size of default
	private static final int DEFAULT_WIDTH = 375;
	
	private static final int DEFAULT_MARGIN_VERTICAL = 15; 
	
	private static final int DEFAULT_MARGIN_HORIZONTAL = 10;

	private Text userid;
	private Text password;
	private Coordinate coordinate;

	/**
	 * Creates object using the parent and coordinate 
	 * @param parentShell composite parent
	 * @param coordinate coordinate to use to login
	 */
	public LoginDialog(Shell parentShell, Coordinate coordinate) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		// uses a clone of coordinate
        this.coordinate = coordinate.getClone();
	}
	
	/**
	 * @return the coordinate
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		// updates the buttons
		updateButtonStatus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
    @Override
    protected Control createDialogArea(Composite parent) {
    	// sets title
		getShell().setText("Login to "+coordinate.getName());
		// main composite
		Composite main = new Composite(parent, SWT.LEFT);
		// creates a grid layout
		GridLayout mainLayout = new GridLayout(Numbers.N_2, false);
		// sets default margin
		mainLayout.marginTop = DEFAULT_MARGIN_VERTICAL;
		mainLayout.marginBottom = DEFAULT_MARGIN_VERTICAL;
		mainLayout.marginLeft = DEFAULT_MARGIN_HORIZONTAL;
		mainLayout.marginRight = DEFAULT_MARGIN_HORIZONTAL;
		mainLayout.marginWidth = 0;
		mainLayout.marginHeight = 0;
		mainLayout.verticalSpacing = DEFAULT_MARGIN_HORIZONTAL;
		main.setLayout(mainLayout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		// sets minimum size of dialog
		data.minimumWidth = DEFAULT_WIDTH;
		main.setLayoutData(data);

		// sets ICON
		Label icon = new Label(main, SWT.LEFT);
		icon.setImage(Images.ONLINE_FAVICON);
		// sets label
		Label descLabel = new Label(main, SWT.LEFT);
		descLabel.setText("Insert userid and password:");

		// USER
		Label useridLabel = new Label(main, SWT.NONE);
		useridLabel.setText("UserId:");
		userid = new Text(main, SWT.BORDER);
		userid.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// loads the userid of current JEM connection or
		// the default one
		userid.setText(coordinate.getUserId());
		// adds a modify listener
		// if user id length > 0
		// then password field will be enable
		// otherwise reset also the password field
		userid.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
				if (userid.getText().length() > 0){
					password.setEnabled(true);
				} else {
					password.setEnabled(false);
					password.setText("");
				}
			}
		});

		// PASSWORD
		Label pwdLabel = new Label(main, SWT.NONE);
		pwdLabel.setText("Password:");
		password = new Text(main, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// loads the password of current JEM connection or
		// the default one		
		password.setText(coordinate.getPassword());
		// every password updates will change the button status
		password.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateButtonStatus();
			}
		});
		password.setFocus();
		return main;
	}

    /**
     * Updates buttons, enabling and disabling them.
     */
	private void updateButtonStatus() {
		// enables the OK button ONLY if userid 
		// and password are not empty
		boolean enabled =(userid.getText().length() > 0) && (password.getText().length() > 0);
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
    @Override
    protected void okPressed() {
    	// saves userid and password in clone of coordinate
		coordinate.setUserId(userid.getText());
		coordinate.setPassword(password.getText());
		// presses ok
		super.okPressed();
	}
}
