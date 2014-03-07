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

import java.util.HashMap;
import java.util.Map;

/**
 * Is the container for all resources that you have to lock.<br>
 * Contains the id and name of requestor, necessary to gets locks.<br>
 * this implementation dosen't do any locks. This is default one.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DefaultRequestLock implements RequestLock {

	private static final long serialVersionUID = 1L;

	private String requestorId = NO_REQUESTOR_NAME;

	private String requestorName = NO_REQUESTOR_NAME;

	private Map<String, ResourceLock> resources = new HashMap<String, ResourceLock>();

	/**
	 * Empty constructor
	 */
	public DefaultRequestLock(){
	}
	
	/**
	 * Returns the requestor id.<br>
	 * RequestorID is the identifier of entity which asks for locking. Usually
	 * is not the member of GRS cluster.<br>
	 * For JEM, is JOB ID.
	 * 
	 * @return requestor id
	 */
	public String getRequestorId() {
		return requestorId;
	}

	/**
	 * Sets the requestor id. RequestorID is the identifier of entity which asks
	 * for locking. Usually is not the member of GRS cluster.<br>
	 * For JEM, is JOB ID.
	 * 
	 * @param requestorId requestor id
	 */
	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

	/**
	 * Returns the requestor name.<br>
	 * Requestor name is the name (readable) of entity which asks for locking.
	 * Usually is not the member of GRS cluster.<br>
	 * For JEM, is JOB NAME.
	 * 
	 * @return requestor name
	 */
	public String getRequestorName() {
		return requestorName;
	}

	/**
	 * Requestor name is the name (readable) of entity which asks for locking.
	 * Usually is not the member of GRS cluster.<br>
	 * For JEM, is JOB NAME.
	 * 
	 * @param requestorName requestor name
	 */
	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}

	/**
	 * Gets the which contains all resources to lock<br>
	 * Is a map with resource name as key and resource lock object as value
	 * 
	 * @return map with all resources
	 */
	public Map<String, ResourceLock> getResources() {
		return resources;
	}

	/**
	 * Performs the lock request into GRS.<br>
	 * 
	 * @throws InterruptedException occurs if someone interrupts from wait state
	 */
	public void tryLock() throws InterruptedException {
		// do nothing
	}


	/**
	 * Unlocks all resources previously locked.
	 */
	public void unlock() {
		// do nothing
	}

}