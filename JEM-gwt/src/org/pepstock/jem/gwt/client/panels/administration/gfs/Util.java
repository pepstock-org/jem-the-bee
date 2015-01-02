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
package org.pepstock.jem.gwt.client.panels.administration.gfs;

import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.node.stats.FileSystemUtilization;
import org.pepstock.jem.node.stats.LightMemberSample;

/**
 * Returns the file system utilization by file system name
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class Util {

	/**
	 * To avoid any instantiation
	 */
	private Util() {
		
	}

	/**
	 * Returns the file system utilization by file system name, using last sample
	 * @param name file system name
	 * @return file system utilization
	 */
	public static FileSystemUtilization getFileSystemUtilization(String name){
		LightMemberSample msample = Instances.getLastSample().getMembers().iterator().next();
		return getFileSystemUtilization(msample, name);
	}

	/**
	 * Returns the file system utilization by file system name and a sample
	 * @param sample sample of a node of JEM cluster
	 * @param name file system name
	 * @return file system utilization
	 */
	public static FileSystemUtilization getFileSystemUtilization(LightMemberSample sample, String name){
		// if null, return null
		if (sample == null){
			return null;
		}
		// scans allto get the FS utilization, comparing the name
		for (FileSystemUtilization fsUtil : sample.getFileSystems()){
			if (fsUtil.getName().equalsIgnoreCase(name)){
				return fsUtil;
			}
		}
		// if no matches, return null
		return null;
	}

}
