/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.plugin.views.explorer;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.gwt.client.rest.UploadListener;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.views.Searcher;

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
	 * @param name
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
		int totUnits = 0;
		for (String fullFile : getFileNames()){
			File fileToGetLenght = new File(fullFile);
			totUnits += fileToGetLenght.length();
		}
		monitor.beginTask("Copying files ...", totUnits); 
		
		FileUploadListener listener = new FileUploadListener(monitor);
		Client.getInstance().addUploadListener(listener);
		
		File file = null;
		try {
			// scans all files
			for (String fullFile : getFileNames()){
				file = new File(fullFile);
	
				if (file.exists()){
					monitor.subTask("Copying " + file.getName());
					String gfsPath = getSearcherText();
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

					int status = Client.getInstance().upload(fileToupload);
					if (status != 200){
						throw new JemException("Status code incorrect: "+status);
					}
				}
			}
			return Status.OK_STATUS;
		} catch (JemException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			showError(file, e);
			return Status.CANCEL_STATUS;
		} finally {
			Client.getInstance().removeUploadListener(listener);
			refreshSearcher();
			monitor.done(); 
		}
	} 

	/**
	 * Refresh of files list
	 */
	private final void refreshSearcher(){
	    Display.getDefault().asyncExec(new Runnable() {
	        public void run() {
	          searcher.refresh();
	        }
	      });
	}
	
	/**
	 * Shows a message box with the exception
	 * @param file
	 * @param e
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
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.1
	 */
	class FileUploadListener implements UploadListener{
		
		private IProgressMonitor monitor = null;

		/**
		 * @param monitor
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
        	if (units >= 0){
        		monitor.worked(units);
        	}
        }
		
	}
	
}
