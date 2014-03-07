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
package org.pepstock.jem.node;

import java.io.Serializable;

/**
 * Represents the status of node.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Status extends Object implements Serializable {

	/**
	 * This status is set when the node leaves the cluster
	 */
	public static final Status UNKNOWN = new Status(-1, "UNKNOWN");

	/**
	 * This status is set when the node is starting, during the configuration
	 * loading
	 */
	public static final Status STARTING = new Status(0, "STARTING");

	/**
	 * This status is set when the node is ready to manage job but there is
	 * anyone in queue
	 */
	public static final Status INACTIVE = new Status(1, "INACTIVE");

	/**
	 * This status is set when the node is managing job which is in execution
	 */
	public static final Status ACTIVE = new Status(2, "ACTIVE");

	/**
	 * This status is set when a user send a stop command and the node has ended
	 * its activity and then it's stopped
	 */
	public static final Status DRAINED = new Status(3, "DRAINED");

	/**
	 * This status is set when a user send a stop command and the node is ending
	 * its activity
	 */
	public static final Status DRAINING = new Status(4, "DRAINING");

	/**
	 * This status is set when the node is shutting down (not implmenented)
	 */
	public static final Status SHUTTING_DOWN = new Status(5, "SHUTTING_DOWN");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int index = 0;

	private String description = null;

	/**
	 * Constructs status object with unique index and a description
	 * 
	 * @param index unique key of object
	 * @param description description string
	 */
	protected Status(int index, String description) {
		this.index = index;
		this.description = description;
	}

	/**
	 * Sets the unique index
	 * 
	 * @param index unique key of object
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Sets the description string
	 * 
	 * @param description description string
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the unique index
	 * 
	 * @return unique key of object
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the description string
	 * 
	 * @return description string
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Checks if object parameter is the same of Status object
	 * 
	 * @param o could be Status object
	 * @return <code>true </code> if status indexes are the same or if status
	 *         description are the same, <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Status) {
			Status st = (Status) o;
			return st.getIndex() == getIndex();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getIndex(); 
	}

	/**
	 * Returns the string description
	 * 
	 * @return string description of status
	 */
	@Override
	public String toString() {
		return description;
	}
}