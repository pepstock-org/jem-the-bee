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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class ShellLoading extends Loading {
	
	private Shell shell = null;

	/**
	 * @param shell 
	 */
	public ShellLoading(Shell shell) {
		this.shell = shell;
	}
	
	/**
	 * 
	 * @param shellContainer
	 */
	public ShellLoading(ShellContainer shellContainer) {
		this(shellContainer.getShell());
	}

	/**
	 * @return the shell
	 */
	public Shell getShell() {
		return shell;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.Loading#getDisplay()
	 */
	@Override
	public final Display getDisplay() {
		return shell.getDisplay();
	}
}
