/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
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
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.views.Searcher;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.services.UploadListener;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Progress monitor when some files are uploading to GFS.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class FileUploadJob extends Job {

	private Collection<String> fileNames = null;
	
	private Searcher searcher = null;
	
	private String searcherText= null;
	
	private int type = -1;
	
	/**
	 * Constructs the object, setting to the Job of Eclipse
	 * the name showes during the copy
	 */
	public FileUploadJob() {
		super("Copying files...");
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
	 * @return the searcher
	 */
	public Searcher getSearcher() {
		return searcher;
	}

	/**
	 * @param searcher the searcher to set
	 */
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	/**
	 * @return the fileNames
	 */
	public Collection<String> getFileNames() {
		return fileNames;
	}

	/**
	 * @param fileNames the fileNames to set
	 */
	public void setFileNames(Collection<String> fileNames) {
		this.fileNames = fileNames;
	}

	/**
	 * @return the searcherText
	 */
	public String getSearcherText() {
		return searcherText;
	}

	/**
	 * @param searcherText the searcherText to set
	 */
	public void setSearcherText(String searcherText) {
		this.searcherText = searcherText;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
    @Override
	protected IStatus run(IProgressMonitor monitor) {
    	// executes the jobs
    	// counter for progress bar
		int totUnits = 0;
		// scans all files to calculate
		// all units
		for (String fullFile : getFileNames()){
			File fileToGetLenght = new File(fullFile);
			if (!fileToGetLenght.isDirectory()){
				totUnits += fileToGetLenght.length();
			}
		}
		// begin task
		monitor.beginTask("Copying files ...", totUnits); 
		
		// creates a upload listener and 
		// it has been set to RESTapi
		FileUploadListener listener = new FileUploadListener(monitor);
		Client.getInstance().addUploadListener(listener);
		
		File file = null;
		try {
			// scans all files
			for (String fullFile : getFileNames()){
				// gets new file
				file = new File(fullFile);
				// copies only if exists and not a folder
				if (file.exists() && !file.isDirectory()){
					// showes the file that is copying
					monitor.subTask("Copying " + file.getName());
					String gfsPath = getSearcherText();
					// creates the path to used on the JEM
					// to load files
					String path = null;
					if (gfsPath.trim().length() == 0){
						// if nothing is the text box to search
						path = "";
					} else {
						// if star, add nothing or the path inserted plus file separator
						path = "*".equalsIgnoreCase(gfsPath) ? "" : gfsPath+File.separator;
					}
					// creates the entity which contains
					// all info to upload the file
					UploadedGfsFile fileToupload = new UploadedGfsFile();
					// file
					fileToupload.setUploadedFile(file);
					// type, which GFS file system
					fileToupload.setType(getType());
					// path where to store the file
					fileToupload.setGfsPath(path);
					// UPLOAD by REST!!!
					boolean status = Client.getInstance().upload(fileToupload);
					// if Return Code HTTP not equals 200, excpetion
					if (!status){
						throw new RestException(ClientResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Unable to upload the file "+file);
					}
				}
			}
			return Status.OK_STATUS;
		} catch (RestException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// shows the error with
			// file and exception
			showError(file, e);
			return Status.CANCEL_STATUS;
		} finally {
			// finally it removes the listener
			// close monitor
			// refreshes the table of searcher
			Client.getInstance().removeUploadListener(listener);
			refreshSearcher();
			monitor.done(); 
		}
	} 

	/**
	 * Refresh of files list
	 */
	private final void refreshSearcher(){
		// refreshes the table of GFS
	    Display.getDefault().asyncExec(new Runnable() {
	        public void run() {
	          searcher.refresh();
	        }
	      });
	}
	
	/**
	 * Shows a message box with the exception
	 * @param file file which creates the exception
	 * @param e exception occurred durng the upload of the file to JEM
	 */
	private final void showError(final File file, final Exception e){
	    Display.getDefault().asyncExec(new Runnable() {
	        public void run() {
	    		Notifier.showMessage(Display.getDefault().getActiveShell(), "Unable to upload file", "Unable to upload '"+ ((file != null) ? file.getName() : "null") +"' due to following exception: "+e.getMessage(), 
	    		 		   MessageLevel.ERROR);
	        }
	      });
	}

	/**
	 * REST upload listener, called by REST API when every chunk has been
	 * uploaded to JEM
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.1
	 */
	static class FileUploadListener implements UploadListener{
		
		private IProgressMonitor monitor = null;

		/**
		 * Creates the listener, saving the monitor.
		 * Monitor needs to show the progress bar
		 * @param monitor needs to show the progress bar
		 */
        private FileUploadListener(IProgressMonitor monitor) {
	        super();
	        this.monitor = monitor;
        }

		/* (non-Javadoc)
		 * @see org.pepstock.jem.gwt.client.rest.UploadListener#setUnitsDone(int)
		 */
        @Override
        public void setUnitsDone(int units) {
        	// changes the progress bar if units more than 0
        	// untis are passed by REST API
        	if (units >= 0){
        		monitor.worked(units);
        	}
        }
	}
}