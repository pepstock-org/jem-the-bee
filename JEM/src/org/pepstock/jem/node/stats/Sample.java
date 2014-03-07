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
package org.pepstock.jem.node.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Sample implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * This is Time format of the sample 
	 */
	public static final String FORMAT = "yyyy-MM-dd HH:mm";
	
	private String key = null;
	
	private String time = null;
	
	private String date = null;
	
	private String environment = null;
	
	private List<MemberSample> members = new ArrayList<MemberSample>();

	/**
	 * 
	 */
	public Sample() {
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
	 * @return the environment
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
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
	public List<MemberSample> getMembers() {
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<MemberSample> members) {
		this.members = members;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sample [key=" + key + ", time=" + time + ", date=" + date + ", environment=" + environment + ", members=" + members + "]";
	}

}