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
    
 Linking JEM, the BEE statically or dynamically with other modules is making a combined work based on JEM, the BEE. 
 Thus, the terms and conditions of the GNU General Public License cover the whole combination.

 As a special exception, the copyright holders of JEM, the BEE give you permission to combine JEM, the BEE program with 
 free software programs or libraries that are released under the GNU LGPL and with independent modules 
 that communicate with JEM, the BEE solely through the org.pepstock.jem.node.RequestLock interface. 
 You may copy and distribute such a system following the terms of the GNU GPL for JEM, the BEE and the licenses 
 of the other code concerned, provided that you include the source code of that other code when and as 
 the GNU GPL requires distribution of source code and provided that you do not modify the 
 org.pepstock.jem.node.RequestLock interface.

 Note that people who make modified versions of JEM, the BEE are not obligated to grant this special exception
 for their modified versions; it is their choice whether to do so. The GNU General Public License
 gives permission to release a modified version without this exception; this exception also makes it
 possible to release a modified version which carries forward this exception. If you modify the 
 org.pepstock.jem.node.RequestLock interface, this exception does not apply to your modified version of 
 JEM, the BEE, and you must remove this exception when you distribute your modified version.

 This exception is an additional permission under section 7 of the GNU General Public License, version 3
 (GPLv3)
     
*/
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.Map;



/**
 * Is the container for all resources that you have to lock.<br>
 * Contains the id and name of requestor, necessary to gets locks.<br>
 * Locks all resources and waits for their availability. Asks in only one
 * request all resources and wait (use count-down latch) for all notifications
 * for each resources<br>
 * Unlocks resource all togheter.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface RequestLock extends Serializable {

	/**
	 * Default requestor name, used if missing
	 */
	String NO_REQUESTOR_NAME = "N/A";

	/**
	 * Returns the requestor id.<br>
	 * RequestorID is the identifier of entity which asks for locking. Usually
	 * is not the member of GRS cluster.<br>
	 * For JEM, is JOB ID.
	 * 
	 * @return requestor id
	 */
	String getRequestorId() ;

	/**
	 * Sets the requestor id. RequestorID is the identifier of entity which asks
	 * for locking. Usually is not the member of GRS cluster.<br>
	 * For JEM, is JOB ID.
	 * 
	 * @param requestorId requestor id
	 */
	void setRequestorId(String requestorId);

	/**
	 * Returns the requestor name.<br>
	 * Requestor name is the name (readable) of entity which asks for locking.
	 * Usually is not the member of GRS cluster.<br>
	 * For JEM, is JOB NAME.
	 * 
	 * @return requestor name
	 */
	String getRequestorName();
	/**
	 * Requestor name is the name (readable) of entity which asks for locking.
	 * Usually is not the member of GRS cluster.<br>
	 * For JEM, is JOB NAME.
	 * 
	 * @param requestorName requestor name
	 */
	void setRequestorName(String requestorName);

	/**
	 * Gets the which contains all resources to lock<br>
	 * Is a map with resource name as key and resource lock object as value
	 * 
	 * @return map with all resources
	 */
	Map<String, ResourceLock> getResources();

	/**
	 * Performs the lock request into GRS.<br>
	 * 
	 * @throws InterruptedException occurs if someone interrupts from wait state
	 */
	void tryLock() throws InterruptedException ;

	/**
	 * Unlocks all resources previously locked.
	 */
	void unlock();

}