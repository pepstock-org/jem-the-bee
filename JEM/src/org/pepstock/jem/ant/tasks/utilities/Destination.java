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
package org.pepstock.jem.ant.tasks.utilities;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsFile;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.rest.services.GfsManager;

/**
 * Contains all the information about the files to upload to JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Destination extends Task {
	
	private String type = GfsFileType.DATA_NAME;
	
	private String path = null;
	
	private Vector<FileSet> filesets = new Vector<FileSet>();
	
	private GfsManager gfsManager = null;

	/**
	 * Empty constructor
	 */
	public Destination() {
		
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(GfsTypeEnum type) {
		this.type = type.getValue();
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
     * @param fileset
     */
    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }
    
	/**
	 * @param gfsManager the gfsManager to set
	 */
	void setGfsManager(GfsManager gfsManager) {
		this.gfsManager = gfsManager;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		// checks parmaters
		if (type == null){
			throw new BuildException(AntMessage.JEMA063E.toMessage().getFormattedMessage("type"));
		}
		
		if (path == null){
			throw new BuildException(AntMessage.JEMA063E.toMessage().getFormattedMessage("path"));
		}
		
		if (gfsManager == null){
			throw new BuildException(AntMessage.JEMA064E.toMessage().getFormattedMessage());
		}
		
		// gets GFS type
		int gfsType = GfsFileType.getType(type);
		log(AntMessage.JEMA067I.toMessage().getFormattedMessage(GfsFileType.getName(gfsType)));
		
		int count = 0;
		int error = 0;
		// scan filesets
        for(FileSet fs : filesets ) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] includedFiles = ds.getIncludedFiles();
            
            log(AntMessage.JEMA068I.toMessage().getFormattedMessage(fs.getDir(), includedFiles.length));
            // scans all files matched
            for(int i=0; i<includedFiles.length; i++) {
            	log(AntMessage.JEMA069I.toMessage().getFormattedMessage(includedFiles[i]));
          	
            	File file = new File(fs.getDir(), includedFiles[i]);
            	if (file.exists()){
                	UploadedGfsFile uploadFile = new UploadedGfsFile();
                	uploadFile.setType(gfsType);
                	uploadFile.setUploadedFile(file);
                	uploadFile.setGfsPath(path);
                	uploadFile.setRelativePath(includedFiles[i]);
                	try {
    					gfsManager.upload(uploadFile);
    					count++;
    				} catch (JemException e) {
    					error++;
    					log(e.getMessage());
    				}
            	}
            }
        }
        // final report
        log(AntMessage.JEMA065I.toMessage().getFormattedMessage(count));
        if (error > 0){
        	log(AntMessage.JEMA066W.toMessage().getFormattedMessage(error));
        }
    }

}
