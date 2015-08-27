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
package org.pepstock.jem.grs;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Represents the unit for locking. There is a unique latch info for each
 * resource and contains all requestors for same resource.<br>
 * This object is shared among members of GRS cluster.<br>
 * Counts the amount of readers (requestor which uses READ lock type) and
 * writers (requestor which uses WRITE lock type) to manage sharing and
 * synchronization on resource.<br>
 * <b>The resource name is unique key</b>.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class LatchInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String resourceName = null;

	private int readersCount = 0;

	private int writersCount = 0;

	private LinkedList<RequestorInfo> requestors = new LinkedList<RequestorInfo>();

	/**
	 * Empty constructor
	 */
	public LatchInfo() {
	}

	/**
	 * Returns the resource name
	 * 
	 * @return resource name
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * Sets the resource name
	 * 
	 * @param resourceName resource name
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * Returns the current amount of requestors as readers
	 * 
	 * @return amount of readers
	 */
	public int getReadersCount() {
		return readersCount;
	}

	/**
	 * Increments the counter of requestors as readers
	 */
	public void incrementReadersCount() {
		this.readersCount++;
	}

	/**
	 * Decrements the counter of requestors as readers
	 */
	public void decrementReadersCount() {
		this.readersCount--;
	}

	/**
	 * Returns the current amount of requestors as writes
	 * 
	 * @return amount of readers
	 */
	public int getWritersCount() {
		return writersCount;
	}

	/**
	 * Increments the counter of requestors as writers
	 */
	public void incrementWritersCount() {
		this.writersCount++;
	}

	/**
	 * Decrements the counter of requestors as writers
	 */
	public void decrementWritersCount() {
		this.writersCount--;
	}

	/**
	 * Returns the list of current requestors of resource, identified by
	 * resource name.
	 * 
	 * @return list of current requestors
	 */
	public LinkedList<RequestorInfo> getRequestors() {
		return requestors;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LatchInfo [resourceName=" + resourceName + ", readersCount=" + readersCount + ", writersCount=" + writersCount + ", requestors=" + requestors + "]";
	}

	
}