/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.stats;

import java.io.Serializable;

/**
 * Is a bean which contains the usage of a specific file system mounted on JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class FileSystemUtilization implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name = null;
	
	private String path = null;

	long free = Long.MIN_VALUE;
	
	long total = Long.MIN_VALUE;
	
	long used = Long.MIN_VALUE;

	/**
	 * Empty constructor
	 */
	public FileSystemUtilization() {
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
	 * @return the free
	 */
	public long getFree() {
		return free;
	}

	/**
	 * @param free the free to set
	 */
	public void setFree(long free) {
		this.free = free;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	/**
	 * @return the used
	 */
	public long getUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	public void setUsed(long used) {
		this.used = used;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileSystemUtilization [name=" + name + ", path=" + path + ", free=" + free + ", total=" + total + ", used=" + used + "]";
	}
}