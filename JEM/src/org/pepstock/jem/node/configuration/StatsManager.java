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
package org.pepstock.jem.node.configuration;

/**
 * Bean used in XML configuration file to activate the statistics manager of JEM.
 * <br>
 * To configure it, we could pass a path (of data path) where all nodes of JEM store
 * the statistics files, for further computation for batches.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class StatsManager {
	
	private String path = null;

	/**
	 * Empty constructor
	 */
	public StatsManager() {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StatisticsManager [path=" + path + "]";
	}
}