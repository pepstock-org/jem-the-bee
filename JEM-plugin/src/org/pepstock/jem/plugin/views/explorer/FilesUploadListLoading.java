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

import java.util.Collection;

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.plugin.util.ShellLoading;

/**
 * Extends loading and it is used when it submits a list of files, which represents JCL, to JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class FilesUploadListLoading extends ShellLoading {
	
	private int type = -1;

	private Collection<String> fileNames = null;
	
	/**
	 * Creates the object with list of files to submit
	 * @param shell Eclipse shell
	 * @param fileNames  list of files to submit
	 * @param type GFS type
	 * @param gfsPath Gfspath set
	 */
    public FilesUploadListLoading(Shell shell, Collection<String> fileNames, int type) {
    	super(shell);
	    this.fileNames = fileNames;
	    this.type = type;
    }
    
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the fileNames
	 */
	public Collection<String> getFileNames() {
		return fileNames;
	}

}