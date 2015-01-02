/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
*     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.views.explorer;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.Loading;
import org.pepstock.jem.plugin.util.Notifier;

/**
 * Delete file from GFS action.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class RemoveFileLoading  extends Loading {

	private GfsFile file = null;

	private ExplorerTableContainer container = null;
	
	/**
	 * Constructs the action using the file to remove and the container
	 * @param file file to remove
	 * @param container container GFS
	 */
	public RemoveFileLoading(GfsFile file, ExplorerTableContainer container) {
		this.file = file;
		this.container = container;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.Loading#getDisplay()
	 */
    @Override
    public Display getDisplay() {
		// gets the display
    	Control ctrl = container.getViewer().getControl();
    	// if not control of is disposed, do nothing
		if (ctrl == null || ctrl.isDisposed()) {
			return null;
		}
		return ctrl.getDisplay();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.Loading#execute()
	 */
    @Override
    public void execute() throws JemException {
    	// removes the file by REST
		try {
			// REST call to remove the file 
			Client.getInstance().delete(container.getType(), file.getLongName(), file.getDataPathName());
		} catch (JemException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			Notifier.showMessage(getDisplay().getActiveShell(), "Unable to delete " + file.getName(), e.getMessage(), MessageLevel.ERROR);
		}
		// gets the control to start
		// the synchronized execution 
		Control ctrl = container.getViewer().getControl();
		if (ctrl == null || ctrl.isDisposed()) {
			return;
		}
		// refreshes the search result post file removing
		ctrl.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				container.refresh();
			}
		});
	}
}
