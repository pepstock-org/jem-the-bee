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

import org.eclipse.swt.widgets.Shell;

/**
 * Interface to define the object as a shell container. Shell is used to show message dialog.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public interface ShellContainer {
	
	/**
	 * Returns the shell, parent for message dialog 
	 * @return the shell, parent for message dialog 
	 */
	Shell getShell();

}
