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
package org.pepstock.jem.plugin.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.log.MessageLevel;

/**
 * Shows a message dialog, receiving all information necessary as title, message and level.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class MessageShower implements Runnable {
	
	private Shell shell = null;
	
	private String title = null;
	
	private String message = null;
	
	private int level = MessageLevel.INFO.getIntLevel();

	/**
	 * Creates object with all necessary information.
	 * @param shell used as parent for message dialog
	 * @param title title of message
	 * @param message content of message 
	 * @param level severity of message
	 */
    public MessageShower(Shell shell, String title, String message, int level) {
	    super();
	    this.shell = shell;
	    this.title = title;
	    this.message = message;
	    this.level = level;
    }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (level == MessageLevel.INFO.getIntLevel()) {
			MessageDialog.openInformation(shell, title, message);
		} else if (level == MessageLevel.WARNING.getIntLevel()){
			MessageDialog.openWarning(shell, title, message);
		} else if (level == MessageLevel.ERROR.getIntLevel()){
			MessageDialog.openError(shell, title, message);
		} else {
			MessageDialog.openInformation(shell, title, message);
		}
	}

}
