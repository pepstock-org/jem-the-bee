/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Enrico Frigo
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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.gfs.GfsFile;

/**
 * POJO container of gfsFile list and the list which is containing the GFS file.<br>
 * Uses the annotation XmlRootElement to be serialized.
 * 
 * @author Enrico Frigo
 * @version 2.2
 *
 */
@XmlRootElement
public class GfsFileList extends ReturnedObject implements Serializable{

    private static final long serialVersionUID = 1L;

	private Collection<GfsFile> gfsFiles = null;
	
	private String path = null;
	
	/**
	 * Empty constructor
	 */
	public GfsFileList() {
	}
	
	
	/**
	 * Returns the path
	 * 
	 * @see GfsFile
	 * @return the path
	 */
	public String getPath() {
		return path;
	}


	/**
	 * Sets the path name
	 * 
	 * @see GfsFile
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}


	/**
	 * Returns the gfs files collection 
	 * @return the jobs
	 */
	public Collection<GfsFile> getGfsFiles() {
		return gfsFiles;
	}

	/**
	 * Sets the gfs files collection
	 * 
	 * @param gfsFiles the gfsFiles to set
	 */
	public void setGfsFiles(Collection<GfsFile> gfsFiles) {
		this.gfsFiles = gfsFiles;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "GfsList [files=" + gfsFiles + "]";
    }

}