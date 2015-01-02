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
package org.pepstock.jem.plugin.views.jobs;

import java.util.Collection;

import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.plugin.util.ShellLoading;

/**
 * Extends loading and it is used when it submits a list of files, which represents JCL, to JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public abstract class SubmitFilesListLoading extends ShellLoading {
	
	private Collection<String> fileNames = null;
	
	/**
	 * Creates the object with list of files to submit
	 * @param shell Eclipse shell
	 * @param fileNames  list of files to submit
	 */
    public SubmitFilesListLoading(Shell shell, Collection<String> fileNames) {
    	super(shell);
	    this.fileNames = fileNames;
    }

	/**
	 * @return the fileNames
	 */
	public Collection<String> getFileNames() {
		return fileNames;
	}

}