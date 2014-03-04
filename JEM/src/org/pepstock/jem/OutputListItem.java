/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem;

import java.io.Serializable;

/**
 * Represents a single item of output tree, which is a output file of a specific job. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class OutputListItem implements Serializable{

	private static final long serialVersionUID = 1L;

	private String label = null;
	
	private String parent = null;
	
	private String fileRelativePath = null;
	
	
	/**
	 * Empty constructor
	 */
	public OutputListItem() {
	}


	/**
	 * Returns readable name of file
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * Sets readable name of file
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}


	/**
	 * Returns parent directory file name
	 * 
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}


	/**
	 * Sets parent directory file name
	 * 
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}


	/**
	 * Returns relative path of the file, based on <code>output</code> configured path.
	 * 
	 * @return the fileRelativePath
	 */
	public String getFileRelativePath() {
		return fileRelativePath;
	}


	/**
	 * Sets relative path of the file, based on <code>output</code> configured path.
	 * 
	 * @param fileRelativePath the fileRelativePath to set
	 */
	public void setFileRelativePath(String fileRelativePath) {
		this.fileRelativePath = fileRelativePath;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OutputListItem [label=" + label + ", parent=" + parent + ", fileRelativePath=" + fileRelativePath + "]";
	}

}