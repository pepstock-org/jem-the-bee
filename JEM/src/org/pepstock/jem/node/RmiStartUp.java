/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.rmi.RemoteException;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.rmi.CommonResourcer;
import org.pepstock.jem.node.rmi.CommonResourcerImpl;
import org.pepstock.jem.node.rmi.ExternalObject;
import org.pepstock.jem.node.rmi.ResourceLocker;
import org.pepstock.jem.node.rmi.ResourceLockerImpl;
import org.pepstock.jem.node.rmi.TasksDoor;
import org.pepstock.jem.node.rmi.TasksDoorImpl;
import org.pepstock.jem.util.rmi.RegistryContainer;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class RmiStartUp {
	
	private static final String CLASS_FOR_EXTERNAL = "org.pepstock.jem.ant.AntUtilManager";

	/**
	 * to avoid any instantiation
	 */
	private RmiStartUp() {
		
	}
	
	/**
	 * Initialize RMI registry with all binds of all objects
	 * @throws RemoteException if any RMI exception occurs
	 */
	public static void initialize() throws RemoteException{
		try {
			// create and load RMI object for step listeners
			RegistryContainer.getInstance().addRmiObject(TasksDoor.NAME, new TasksDoorImpl());
			LogAppl.getInstance().emit(NodeMessage.JEMC015I, TasksDoor.NAME);

			// create and load RMI object for global resource looking
			RegistryContainer.getInstance().addRmiObject(ResourceLocker.NAME, new ResourceLockerImpl());
			LogAppl.getInstance().emit(NodeMessage.JEMC015I, ResourceLocker.NAME);

			// create and load RMI object for common resources
			RegistryContainer.getInstance().addRmiObject(CommonResourcer.NAME, new CommonResourcerImpl());
			LogAppl.getInstance().emit(NodeMessage.JEMC015I, CommonResourcer.NAME);

			try {
				// Try to load internal utilities. Due to they are not free
				// and in another project, avoiding to create useless dependency they are
				// loaded dynamically, by reflection
				@SuppressWarnings("rawtypes")
				Class internals = Class.forName(CLASS_FOR_EXTERNAL);
				ExternalObject externalObject = (ExternalObject)internals.newInstance();
				// create and load RMI object for internal utilities
				RegistryContainer.getInstance().addRmiObject(externalObject.getName(), externalObject.getObject());
				LogAppl.getInstance().emit(NodeMessage.JEMC195I, externalObject.getName());

			} catch (ClassNotFoundException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC196W, CLASS_FOR_EXTERNAL);
			} catch (InstantiationException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC196W, e, CLASS_FOR_EXTERNAL);
			} catch (IllegalAccessException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC196W, e, CLASS_FOR_EXTERNAL);
			} catch (RemoteException e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC196W, e, CLASS_FOR_EXTERNAL);
			} 
		} catch (RemoteException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC016E, e, TasksDoor.NAME);
			throw e;
		}
	}
}
