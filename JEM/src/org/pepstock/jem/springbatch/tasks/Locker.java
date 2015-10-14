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
package org.pepstock.jem.springbatch.tasks;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.gdg.GDGManager;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ResourceLock;
import org.pepstock.jem.node.rmi.ResourceLocker;
import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.items.DataDescriptionItem;

/**
 * Utility to lock and unlock resources declared on Spring Batch JCL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Locker {

	// all resources to lock
	private List<ResourceLock> resources = new ArrayList<ResourceLock>();
	
	// reference to locker
	private	ResourceLocker internalLocker = null;
	
	private boolean isExecutionStarted = false;
	
	/**
	 * Constructs a object with lick by RMI to lock resources
	 * @throws SpringBatchException if any RMI error occurs
	 * 
	 */
	Locker() throws SpringBatchException {
		try {
			// gets the locker
			internalLocker = InitiatorManager.getResourceLocker();
			// get instance of data container, necessary to implement the referback
			// feature
			ImplementationsContainer.getInstance();
		} catch (Exception e) {
			throw new SpringBatchException(SpringBatchMessage.JEMS041E, e);
		}
	}
	
	/**
	 * Locks resources, starting from definition of them!
	 * @throws SpringBatchException if any RMI error occurs 
	 */
	void lock() throws SpringBatchException{
		//scan all definition loading the implementations 
		for (Definition object : DefinitionsContainer.getInstance().getObjects()){
			if (object.isJemTasklet()){
				loadTasklet(object);
			} else { 
				loadChunk(object);
			}
		}
		
		try {
			// if list of resources is empty, don't lock anything
			if (!resources.isEmpty()){
				// ask to JEM node by locker to lock them
				// waiting if they are not available
				internalLocker.lock(JobId.VALUE, resources);
			}
		} catch (RemoteException e) {
			throw new SpringBatchException(SpringBatchMessage.JEMS039E, e);
		}
		
		try {
			// for chunks, it's necessary to load GDGs info if
			// GDGs are used. For tasklet not, because already in JemTasklet
			for (Definition object : DefinitionsContainer.getInstance().getObjects()){
				if (object.isChunkItem()){
					loadChunkPostLock(object);
				}
			}
			isExecutionStarted = true;
		} catch (IOException e) {
			throw new SpringBatchException(SpringBatchMessage.JEMS039E, e);
		}
	}
	
	/**
	 * Loads all data description implementation starting from TASKLET definition
	 * @param item definition of data description
	 * @throws Exception if any error occurs 
	 */
	private void loadTasklet(Definition taskletandstepname) throws SpringBatchException{

		JemTasklet tasklet = (JemTasklet)taskletandstepname.getObject();
		String stepName = taskletandstepname.getStepName();

		// scans all data description loaded
		for (DataDescription dd : tasklet.getDataDescriptionList()) {
			// creates a data description impl from data description
			// data description impl is necessary object to have locking
			DataDescriptionImpl ddImpl = DataDescriptionManager.createDataDescriptionImpl(dd, stepName);

			// loads the data description impl for locking, saving it into a
			// container
			// necessary to lock all in one shot
			InitiatorManager.addResourcesLock(ddImpl, resources);
		}
		// adds all defined locks
		for (Lock lock : tasklet.getLocks()){
			// checks if the name is not null
			if (lock.getName() != null){
				// creates resource lock and adds it to container
				ResourceLock rLock = new ResourceLock(lock.getName(), ResourceLock.WRITE_MODE);
				resources.add(rLock);
			}
		}

	}

	/**
	 * Loads all data description implementation starting from CHUNK definition
	 * @param item definition of data description
	 * @throws Exception if any error occurs 
	 */
	private void loadChunk(Definition object) throws SpringBatchException{
		DataDescriptionItem item = (DataDescriptionItem)object.getObject();
		
		String stepName = object.getStepName();
		// creates a data description impl from data description
		// data description impl is necessary object to have locking
		DataDescriptionImpl ddImpl = DataDescriptionManager.createDataDescriptionImpl(item.getDataDescription(), stepName);
		item.setDataDescriptionImpl(ddImpl);
		// loads the data description impl for locking, saving it into a
		// container
		// necessary to lock all in one shot
		InitiatorManager.addResourcesLock(ddImpl, resources);
		
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
	 * Specific method to load GDGs info after lock! 
	 * 
	 * @param item definition of data description
	 * @throws IOException if any error occurs 
	 */
	private void loadChunkPostLock(Definition object) throws IOException{
		DataDescriptionItem item = (DataDescriptionItem)object.getObject();
		GDGManager.load(item.getDataDescriptionImpl());

		LogAppl.getInstance().emit(SpringBatchMessage.JEMS023I, item.getDataDescriptionImpl());
		// dataDescription is only a container. The datasets are real
		// resources to read so prepares an Array to pass the setResources
		// method
		int size = item.getDataDescription().getDatasets().size();
		DataSet[] datasets = new DataSet[size];
		for (int i = 0; i < size; i++) {
			datasets[i] = item.getDataDescription().getDatasets().get(i);
		}
		item.setResources(datasets);
	}
	/**
	 * Specific method to store GDGs info before unlock! 
	 * 
	 * @param item definition of data description
	 * @throws IOException if any error occurs 
	 */
	private void loadChunkPostUnLock(Definition object) throws IOException{
		DataDescriptionItem item = (DataDescriptionItem)object.getObject();
		if (isExecutionStarted){
			GDGManager.store(item.getDataDescriptionImpl());
		}
	}
	
	/**
	 * Unlocks resources, starting from definition of them!
	 * @throws SpringBatchException if any exception occurs
	 */
	void unlock() throws SpringBatchException{
		try {
			for (Definition object : DefinitionsContainer.getInstance().getObjects()){
				if (object.isChunkItem()){
					loadChunkPostUnLock(object);
				}
			}
			// if resource locked container is empty, ends
			if (!resources.isEmpty()) {
				// ask to unlock all resources
				// is not necessary to pass the container because JEM node
				// saved the list
				// when the this class asked
				internalLocker.unlock(JobId.VALUE);
				resources.clear();
				DefinitionsContainer.getInstance().getObjects().clear();
			}
		} catch (Exception e) {
			throw new SpringBatchException(SpringBatchMessage.JEMS040E, e);
		}
	}
}