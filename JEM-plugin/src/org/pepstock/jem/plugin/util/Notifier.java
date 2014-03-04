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
package org.pepstock.jem.plugin.util;

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.log.MessageLevel;

/**
 * Utility to show a message dialog.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class Notifier {

	/**
	 * Private constructor to avoid new instantiations 
	 */
	private Notifier() {
	}

	/**
	 * Shows a message dialog, receiving all information necessary as title, message and level.
	 * @param shell shell container, used as parent for message dialog
	 * @param title title of message
	 * @param message content of message 
	 * @param level severity of message
	 */
	public static void showMessage(ShellContainer shell, String title, String message, MessageLevel level) {
		showMessage(shell.getShell(), title, message, level);
	}
	
	/**
	 * Shows a message dialog, receiving all information necessary as title, message and level.
	 * @param shell used as parent for message dialog
	 * @param title title of message
	 * @param message content of message 
	 * @param level severity of message
	 */
	public static void showMessage(Shell shell, String title, String message, MessageLevel level) {
		shell.getShell().getDisplay().asyncExec(new MessageShower(shell, title, message, level.getIntLevel()));
	}

}
