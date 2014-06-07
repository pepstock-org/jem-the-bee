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
package org.pepstock.jem.gfs;

import java.io.File;
import java.io.Serializable;

/**
 * It represent a file that will be uploaded to the global file system of JEM
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class UploadedGfsFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private File uploadedFile;

	private String gfsPath;
	
	private int type;

	/**
	 * 
	 * @return the file to be uploaded to the GFS
	 */
	public File getUploadedFile() {
		return uploadedFile;
	}

	/**
	 * 
	 * @param uploadedFile the file to be uploaded to the GFS
	 */
	public void setUploadedFile(File uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	/**
	 * 
	 * @return the path where the uploaded file will be uploaded. The path is
	 *         relative to a folder while the name of the file will remain the
	 *         same of the uploaded file
	 */
	public String getGfsPath() {
		return gfsPath;
	}

	/**
	 * 
	 * @param gfsPath the path where the uploaded file will be uploaded. The path is
	 *         relative to a folder while the name of the file will remain the
	 *         same of the uploaded file.
	 */
	public void setGfsPath(String gfsPath) {
		this.gfsPath = gfsPath;
	}

	/**
	 * 
	 * @return an integer that identifies the folder of the GFS.
	 * @see {@link GfsFileType}
	 * 
	 */
	public int getType() {
		return type;
	}

	/**
	 * 
	 * @param type an integer that identifies the folder of the GFS.
	 * @see {@link GfsFileType}
	 */
	public void setType(int type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UploadedGfsFile [uploadedFile=" + uploadedFile + ", gfsPath=" + gfsPath + ", type=" + type + "]";
	}
	
}
