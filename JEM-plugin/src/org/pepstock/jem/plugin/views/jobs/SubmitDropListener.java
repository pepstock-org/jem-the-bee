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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.util.ShellContainer;
import org.pepstock.jem.rest.RestException;

/**
 * Utility to submit jobs inside of JEM, using DND.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class SubmitDropListener extends ViewerDropAdapter implements ShellContainer{

	/**
	 * Creates the drop adapter by viewer.
	 * 
	 * @param viewer viewer instance
	 */
	public SubmitDropListener(Viewer viewer) {
		super(viewer);
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
			// submits all files as JOB
			SubmitFilesListLoading loading = new SubmitFilesList(getShell(), files);
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
			// and only it has got the authorization to submit jobs
			return Client.getInstance().isAuthorized(Permissions.JOBS_SUBMIT);
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
     * Submit a list of files using a loading progress bar
     * 
     * @author Andrea "Stock" Stocchero
     * @version 2.0
     */
    private static class SubmitFilesList extends SubmitFilesListLoading{
    	
		/**
		 * Shell of graphic container and list of files to submit are necessary
		 * to create the object
		 * @param shell Shell of graphic container
		 * @param fileNames list of files to submit
		 */
        public SubmitFilesList(Shell shell, Collection<String> fileNames) {
	        super(shell, fileNames);
        }

		/* (non-Javadoc)
		 * @see org.pepstock.jem.plugin.util.Loading#execute()
		 */
        @Override
        protected void execute() throws JemException {
			// scans all files
			for (String file : getFileNames()){
				File jcl = new File(file);
				// if file exist 
				if (jcl.exists()){
					try {
						// then it submits the file
                        String jobid = Client.getInstance().submit(jcl);
                        // show message that it can submit the file
	                    Notifier.showMessage(super.getShell(), "Job submitted", "'"+jcl.getName()+"' has been submitted and this is job id : "+jobid, 
	                    		   MessageLevel.INFO);

                    } catch (RestException e) {
                    	// error for any REST API calls will fail
                    	LogAppl.getInstance().ignore(e.getMessage(), e);
                       Notifier.showMessage(super.getShell(), "Unable to submit job", "Unable to submit '"+jcl.getName()+"' due to following exception: "+e.getMessage(), 
                    		   MessageLevel.ERROR);
                    }
				}
			}
		}
    }
}