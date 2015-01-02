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
package org.pepstock.jem.ant.tasks;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.jem.ant.AntException;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.DataDescriptionStep;
import org.pepstock.jem.node.ResourceLock;
import org.pepstock.jem.node.rmi.ResourceLocker;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;

/**
 * Utility to lock and unlock resources declared on ANT JCL.
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
	 * @throws AntException if any RMI exception occurs
	 */
	Locker() throws AntException {
		try {
			// gets the locker
			locker = InitiatorManager.getResourceLocker();
			// get instance of data container, necessary to implement the referback
			// feature
			ImplementationsContainer.getInstance();
		} catch (RemoteException e) {
			throw new AntException(AntMessage.JEMA059E, e);
		} catch (UnknownHostException e) {
			throw new AntException(AntMessage.JEMA059E, e);
		}
	}
	
	/**
	 * Locks the resources
	 * 
	 * @throws AntException if any exception occurs
	 */
	void lock() throws AntException{
		// sorts the data descriptions by steps order 
		Collections.sort(StepsContainer.getInstance().getDataDescriptionSteps(), new Comparator<DataDescriptionStep>() {
			@Override
			public int compare(DataDescriptionStep dd0, DataDescriptionStep dd1) {
				return dd0.getOrder() - dd1.getOrder();
			}
		});

		try {
			// scans all data description and locks by step repreentation
			for (DataDescriptionStep item : StepsContainer.getInstance().getDataDescriptionSteps()){
				load(item);
			}
		} catch (IOException e) {
			throw new AntException(AntMessage.JEMA060E, e);
		}
		
		try {
			// if list of resources is empty, don't lock anything
			if (!resources.isEmpty()){
				// ask to JEM node by locker to lock them
				// waiting if they are not available
				locker.lock(JobId.VALUE, resources);
			}
		} catch (RemoteException e) {
			throw new AntException(AntMessage.JEMA060E, e);
		}

	}
	
	/**
	 * Loads all datadescription resources and locks ones.
	 * 
	 * @param item step representation
	 * @throws IOException if any exception occurs
	 */
	private void load(DataDescriptionStep item) throws IOException{
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
	 * @throws AntException if any excpetion occurs
	 */
	void unlock() throws AntException{
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
				StepsContainer.getInstance().getDataDescriptionSteps().clear();
			}
		} catch (RemoteException e) {
			throw new AntException(AntMessage.JEMA061E, e);
		}
	}
}