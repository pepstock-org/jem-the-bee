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
package org.pepstock.jem.plugin.views.explorer;

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.plugin.util.ShellLoading;

/**
 * Extends loading and it is used when it is downloading a file from JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class FileLoading extends ShellLoading {

	private int type = -1;

	private GfsFile file = null;

	/**
	 * Creates the object, saving data type and file to download fom JEM.
	 * @param shell Shell of Eclipse widget
	 * @param type data type (DATA, SOURCE, LIBRARY, CLASS, BINARY).
	 * @param file file to download form JEM
	 */
    public FileLoading(Shell shell, int type, GfsFile file) {
	    super(shell);
	    this.type = type;
	    this.file = file;
    }

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the file
	 */
	public GfsFile getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(GfsFile file) {
		this.file = file;
	}

	
}