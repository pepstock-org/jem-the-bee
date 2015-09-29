/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
*     Enrico Frigo - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.actions;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.preferences.Coordinate;
import org.pepstock.jem.plugin.preferences.PreferencesManager;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.views.LoginDialog;
import org.pepstock.jem.rest.RestException;

/**
 * Action of popup menu on package explorer of Eclipse to submit the selected
 * file.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class SubmitAction implements IObjectActionDelegate {

	private Shell shell;
	
	private IWorkbenchPart part;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		part = targetPart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		Coordinate choosen = null;
		// if not logged
		if (!Client.getInstance().isLogged()) {
			// scans all coordinates to get the default one to use it
			for (Coordinate coordinate : PreferencesManager.ENVIRONMENTS.values()) {
				if (coordinate.isDefault()) {
					choosen = coordinate;
				}
			}
			// if there isn't any default
			// ERROR!!!!
			if (choosen == null) {
				Notifier.showMessage(shell, "Unable to login to JEM!", "You're not connected to JEM and there is not any JEM definition by default", MessageLevel.ERROR);
				return;
			}
		} else {
			// gets the coordinate from the current connection
			choosen = Client.getInstance().getCurrent();
		}
		// extracts the file to submit
		IStructuredSelection sSelection = (IStructuredSelection) part.getSite().getSelectionProvider().getSelection();
		IResource resource = (IResource) sSelection.getFirstElement();
		File jcl = resource.getLocation().toFile();
		// runs loading 
		LoginAndSubmitLoading loading = new LoginAndSubmit(shell, choosen, jcl);
		loading.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}

	/**
	 * Loading widget to display the loginn and Submit of job
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.0
	 */
	private class LoginAndSubmit extends LoginAndSubmitLoading {
		
		/**
		 * Creates a loading using a shell, coordinate and the file to submit
		 * @param shell is the target part where the action has been called
		 * @param coordinate current coordinate 
		 * @param file the file to submit
		 */
		public LoginAndSubmit(Shell shell, Coordinate coordinate, File file) {
			super(shell, coordinate, file);
		}

		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
        public void execute() throws JemException {
			try {
				// if client not logged
				if (!Client.getInstance().isLogged()) {
					Coordinate choosed = getCoordinate();
					// checks if coordinate has got password. If not, asks for
					if ((choosed.getPassword() == null) || (choosed.getPassword().length() == 0)) {
						// asks for LOGIN
						LoginDialog dialog = new LoginDialog(shell, getCoordinate());
						if (dialog.open() == Dialog.OK) {
							choosed = dialog.getCoordinate();
						} else {
							// if cancel, nop
							return;
						}
					}
					// login
					Client.getInstance().login(choosed);
				}
				// submit the job
				String id = Client.getInstance().submit(getFile());
				Notifier.showMessage(shell, "Job submitted!", "Job submitted with id: " + id, MessageLevel.INFO);
			} catch (RestException e) {
				// if any error occurs during the submit by REST
				LogAppl.getInstance().ignore(e.getMessage(), e);
				Notifier.showMessage(shell, "Unable to login to " + getCoordinate().getName() + "!", "Error occurred during login to '" + getCoordinate().getName() + "': " + e.getMessage(), MessageLevel.ERROR);
			}
		}
	}
}
