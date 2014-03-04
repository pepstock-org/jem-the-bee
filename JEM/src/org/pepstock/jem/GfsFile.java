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
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class GfsFile implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public static final int DATA = 0;

	/**
	 * 
	 */
	public static final int LIBRARY = 1;
	
	/**
	 * 
	 */
	public static final int SOURCE = 2;
	
	/**
	 * 
	 */
	public static final int CLASS = 3;
	
	/**
	 * 
	 */
	public static final int BINARY = 4;

	private String name = null;
	
	private String longName = null;
	
	private boolean isDirectory = false;
	
	private long length = 0;
	
	private long lastModified = 0;

	/**
	 * 
	 */
	public GfsFile() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the longName
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * @param longName the longName to set
	 */
	public void setLongName(String longName) {
		this.longName = longName;
	}

	/**
	 * @return the isDirectory
	 */
	public boolean isDirectory() {
		return isDirectory;
	}

	/**
	 * @param isDirectory the isDirectory to set
	 */
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	
	
	/**
	 * @return the length
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GfsFile [name=" + name + ", longName=" + longName + ", isDirectory=" + isDirectory + "]";
	}

	
}