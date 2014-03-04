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
package org.pepstock.jem.plugin.actions;

import java.io.File;

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.plugin.preferences.Coordinate;
import org.pepstock.jem.plugin.util.ShellLoading;

/**
 * Extends loading and it is used when in submit action to connect and submit.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class LoginAndSubmitLoading extends ShellLoading {

	private Coordinate coordinate = null;
	
	private File file = null;

	/**
	 * Creates the object with coordinate to use to login and JCL file to submit
	 * @param shell Eclipse shell
	 * @param coordinate coordinate of JEM
	 * @param file JCL to submit
	 */
    public LoginAndSubmitLoading(Shell shell, Coordinate coordinate, File file) {
	    super(shell);
	    this.coordinate = coordinate;
	    this.file = file;
    }

	/**
	 * @return the coordinate
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
}
