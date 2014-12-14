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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.plugin.views.Searcher;

/**
 * Utility to submit jobs inside of JEM, using DND.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class FilesUploadDropListener extends ViewerDropAdapter implements ShellContainer{
	
	private int type = -1;
	
	private Searcher searcher = null;

	/**
	 * Creates the drop adapter by viewer.
	 * 
	 * @param viewer viewer instance
	 * @param searcher searcher text field
	 * @param type type of GFS
	 */
	public FilesUploadDropListener(Viewer viewer, Searcher searcher, int type) {
		super(viewer);
		this.type = type;
		this.searcher = searcher;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
	 */
	@Override
	public boolean performDrop(Object object) {
		String[] listFiles = (String[])object;
		// checks list of files
		if (listFiles != null && listFiles.length > 0){
			Collection<String> files = Collections.unmodifiableCollection(Arrays.asList(listFiles));
			// all files to copy
			// creates the file upload job
			// with all info to do it
			FileUploadJob job = new FileUploadJob();
			job.setSearcherText(searcher.getText());
			job.setSearcher(searcher);
			job.setType(type);
			job.setFileNames(files);
			job.setUser(true);
			job.schedule();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	public boolean validateDrop(Object arg0, int arg1, TransferData arg2) {
		// it can drop only if is connected
		if (Client.getInstance().isLogged()){
			// and only it has got the authorization to GFS
			// checks here the type of file-system to scan
			switch (type) {
				case GfsFileType.DATA:
					// is not possible to load data on GFS DATA
					return false;
				case GfsFileType.LIBRARY:
					return Client.getInstance().isAuthorized(Permissions.GFS, Permissions.GFS_LIBRARY);
				case GfsFileType.SOURCE:
					return Client.getInstance().isAuthorized(Permissions.GFS, Permissions.GFS_SOURCES);
				case GfsFileType.CLASS:
					return Client.getInstance().isAuthorized(Permissions.GFS, Permissions.GFS_CLASS);
				case GfsFileType.BINARY:
					return Client.getInstance().isAuthorized(Permissions.GFS, Permissions.GFS_BINARY);
				default:
					return false;
			}
		}
		// otherwise always FALSE!!
		return false;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.ShellContainer#getShell()
	 */
    @Override
    public Shell getShell() {
	    return getViewer().getControl().getShell();
    }
}