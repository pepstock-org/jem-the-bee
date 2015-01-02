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
import java.util.ArrayList;
import java.util.List;

/**
 * Is a bean with a subset of all information usually extract inside the JEM cluster.
 * This bean is used to show information in the user interface
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class LightSample implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * This is Time format of the sample 
	 */
	public static final String FORMAT = "yyyy-MM-dd HH:mm";
	
	private String key = null;
	
	private String time = null;
	
	private String date = null;

	private List<LightMemberSample> members = new ArrayList<LightMemberSample>();

	/**
	 * Empty constructor
	 */
	public LightSample() {
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
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
	 * @return the members
	 */
	public List<LightMemberSample> getMembers() {
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<LightMemberSample> members) {
		this.members = members;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LightSample [key=" + key + ", time=" + time + ", date=" + date + ", members=" + members + "]";
	}
}