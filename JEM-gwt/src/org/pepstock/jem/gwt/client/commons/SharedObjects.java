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
package org.pepstock.jem.gwt.client.commons;


/**
 * Container of shared (and then static) references to use every where.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SharedObjects {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	/**
	 * License and copyright text 
	 */
	public static final String LICENSE = "JEM, the BEE - &copy; 2012-2014 pepstock.org";
	
	/**
	 * Difference time calculated between client and server
	 */
	private static long clusterDifferenceTime = 0L;
	
	/**
	 * Saves execution environment name
	 */
	private static String executionEnvironment = null;

	/**
	 * To avoid any instantiation
	 */
    private SharedObjects() {
    }

	/**
	 * Returns difference time calculated between client and server
	 * @return the clusterDifferenceTime
	 */
	public static long getClusterDifferenceTime() {
		return clusterDifferenceTime;
	}

	/**
	 * Sets difference time calculated between client and server
	 * @param clusterDifferenceTime the clusterDifferenceTime to set
	 */
	public static void setClusterDifferenceTime(long clusterDifferenceTime) {
		SharedObjects.clusterDifferenceTime = clusterDifferenceTime;
	}

	/**
	 * Returns execution environment
	 * @return the executionEnvironment
	 */
	public static String getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Sets execution environment
	 * @param executionEnvironment the executionEnvironment to set
	 */
	public static void setExecutionEnvironment(String executionEnvironment) {
		SharedObjects.executionEnvironment = executionEnvironment;
	}
	
	
}