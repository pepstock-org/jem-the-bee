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
package org.pepstock.jem.gwt.server.rest.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO Container of user-id and password to use in REST login manager to log in JEM web application.<br>
 * Uses the annotation XmlRootElement to be serialized. 
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@XmlRootElement
public class UploadedGfsFile implements Serializable {

    private static final long serialVersionUID = 1L;

	private String fileName = null;
	
	private byte[] content = null;
	
	/**
	 * Empty constructor
	 */
	public UploadedGfsFile() {
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
	    return "UploadedGfsFile [fileName=" + fileName + ", contentLength=" + (content != null ? content.length : 0)  + "]";
    }
}