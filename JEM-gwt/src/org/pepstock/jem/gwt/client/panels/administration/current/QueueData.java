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
package org.pepstock.jem.gwt.client.panels.administration.current;


/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class QueueData {
	
	private String shortName = null;
	
	private String fullName = null;
	
	private String time = null;
	
	private long entries = 0;
	
	
	/**
	 * @return the queue shoer name
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the queue short name
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}


	/**
	 * @return the queue full name
	 */
	public String getFullName() {
		return fullName;
	}


	/**
	 * @param fullName the queue full name to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}


	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}


	/**
	 * @return the entries
	 */
	public long getEntries() {
		return entries;
	}


	/**
	 * @param entries the entries to set
	 */
	public void setEntries(long entries) {
		this.entries = entries;
	}
}