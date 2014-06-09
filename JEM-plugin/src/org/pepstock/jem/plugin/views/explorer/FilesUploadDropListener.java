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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.Notifier;
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
			FilesUploadList loading = new FilesUploadList(getShell(), files, type);
			loading.setSearcher(searcher);
			loading.run();
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
		return false;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.plugin.util.ShellContainer#getShell()
	 */
    @Override
    public Shell getShell() {
	    return getViewer().getControl().getShell();
    }
 
    
    /**
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private static class FilesUploadList extends FilesUploadListLoading{
    	
    	private Searcher searcher = null;
    	
		/**
		 * @param shell
		 * @param fileNames
		 * @param type 
		 * @param gfsPath 
		 */
        public FilesUploadList(Shell shell, Collection<String> fileNames, int type) {
	        super(shell, fileNames, type);
        }
        
		/**
		 * @param searcher the searcher to set
		 */
		public void setSearcher(Searcher searcher) {
			this.searcher = searcher;
		}

		@Override
		public void execute() throws JemException {
			
			if (searcher == null){
				return;
			}
			// scans all files
			for (String fullFile : getFileNames()){
				File file = new File(fullFile);
				
				if (file.exists()){
					String gfsPath = searcher.getText();
					String path = null;
					if (gfsPath.trim().length() == 0){
						path = "";
					} else {
						path = "*".equalsIgnoreCase(gfsPath) ? "" : gfsPath+File.separator;
					}
					UploadedGfsFile fileToupload = new UploadedGfsFile();
					fileToupload.setUploadedFile(file);
					fileToupload.setType(getType());
					fileToupload.setGfsPath(path);

					try {
                       int status = Client.getInstance().upload(fileToupload);
                       if (status != 200){
                    	   throw new JemException("Status code incorrect: "+status);
                       }
	                   Notifier.showMessage(super.getShell(), "File uploaded", "'"+file.getName()+"' has been uploaded", 
	                    		   MessageLevel.INFO);

                    } catch (JemException e) {
                    	LogAppl.getInstance().ignore(e.getMessage(), e);
                       Notifier.showMessage(super.getShell(), "Unable to upload file", "Unable to upload '"+file.getName()+"' due to following exception: "+e.getMessage(), 
                    		   MessageLevel.ERROR);
                    }
				}
			}
			// search again
			searcher.refresh();
		}
    }
    
    
}