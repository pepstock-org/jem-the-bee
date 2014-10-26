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
package org.pepstock.jem.jbpm.tasks;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.jem.jbpm.JBpmException;
import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.jbpm.Task;
import org.pepstock.jem.node.ResourceLock;
import org.pepstock.jem.node.rmi.ResourceLocker;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;

/**
 * Utility to lock and unlock resources decalred on ANT JCL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Locker {

	private List<ResourceLock> resources = new ArrayList<ResourceLock>();
	
	// reference to locker
	private	ResourceLocker locker = null;

	
	/**
	 * Gets resource locker instance to lock and unlock resources by RMI.<br>
	 * Creates the implementation container, to look for dataste references.
	 * 
	 * @throws Exception if any RMI exception occurs
	 */
	Locker() throws JBpmException {
		try {
			// gets the locker
			locker = InitiatorManager.getResourceLocker();
			// get instance of data container, necessary to implement the referback
			// feature
			ImplementationsContainer.getInstance();
		} catch (RemoteException e) {
			throw new JBpmException(JBpmMessage.JEMM059E, e);
		} catch (UnknownHostException e) {
			throw new JBpmException(JBpmMessage.JEMM059E, e);
		}
	}
	
	/**
	 * Locks the resources
	 * 
	 * @throws JBpmException if any exception occurs
	 */
	void lock() throws JBpmException{
		// sorts the data descriptions by steps order 
		List<Task> cloneTasks = new ArrayList<Task>(TaskContainer.getInstance().getTasks().values());
		Collections.sort(cloneTasks, new Comparator<Task>() {
			@Override
			public int compare(Task dd0, Task dd1) {
				return (int)(dd0.getNodeId() - dd1.getNodeId());
			}
		});
		
		try {
			// scans all data description and locks by step repreentation
			for (Task task : cloneTasks){
				load(task);
			}
		} catch (IOException e) {
			throw new JBpmException(JBpmMessage.JEMM060E, e);
		}
		
		try {
			// if list of resources is empty, don't lock anything
			if (!resources.isEmpty()){
				// ask to JEM node by locker to lock them
				// waiting if they are not available
				locker.lock(JobId.VALUE, resources);
			}
		} catch (RemoteException e) {
			throw new JBpmException(JBpmMessage.JEMM060E, e);
		}

	}
	
	/**
	 * Loads all datadescription resources and locks ones.
	 * 
	 * @param item step representation
	 * @throws IOException if any exception occurs
	 * @throws JBpmException 
	 */
	private void load(Task item) throws IOException, JBpmException{
		// scans all data description loaded
		for (DataDescription dd : item.getDataDescriptions()) {
			// creates a data description impl from data description
			// data description impl is necessary object to have locking
			DataDescriptionImpl ddImpl = DataDescriptionManager.createDataDescriptionImpl(dd, item);
			
			// loads the data description impl for locking, saving it into a
			// container
			// necessary to lock all in one shot
			InitiatorManager.addResourcesLock(ddImpl, resources);
		}
		// adds all defined locks
		for (Lock lock : item.getLocks()){
			// checks if the name is not null
			if (lock.getName() != null){
				// creates resource lock and adds it to container
				ResourceLock rLock = new ResourceLock(lock.getName(), ResourceLock.WRITE_MODE);
				resources.add(rLock);
			}
		}
	}

	/**
	 * Unlocks all resources 
	 * 
	 * @throws JBpmException if any excpetion occurs
	 */
	void unlock() throws JBpmException{
		try {
			// if resource locked container is empty, ends
			if (!resources.isEmpty()) {
				// ask to unlock all resources
				// is not necessary to pass the container because JEM node
				// saved the list
				// when this class asked
				locker.unlock(JobId.VALUE);
				// clears the contianers
				resources.clear();
				TaskContainer.getInstance().getTasks().clear();
			}
		} catch (RemoteException e) {
			throw new JBpmException(JBpmMessage.JEMM061E, e);
		}
	}
}