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

import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.tools.ant.launch.Launcher;
import org.pepstock.jem.ant.AntException;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.rmi.JobStartedObjects;
import org.pepstock.jem.node.rmi.TasksDoor;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.tasks.JobId;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.rmi.RegistryLocator;
import org.pepstock.jem.util.rmi.RmiKeys;
/**
 * Class not used. Is a wrapper of Launcher of ANT.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class AntLauncher {
	
	/**
	 * To avoid any instantiation
	 */
	private AntLauncher() {
		
	}

	/**
	 * @param args
	 * @throws UnknownHostException 
	 * @throws RemoteException 
	 * @throws AntException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws RemoteException, UnknownHostException, AntException {
		// get port number from env var
		// AntTask has passed and set this information (MUST)
		String port = System.getProperty(RmiKeys.JEM_RMI_PORT);
		// creates RMI connection with localhost (default) and using
		// port number.
		// if port is null or not a number, -1 is return and a RMI
		// Exception will occur
		RegistryLocator locator = new RegistryLocator(Parser.parseInt(port, -1));
		// check the taskdoor object is binded, if not, a exception
		// occurs
		if (locator.hasRmiObject(TasksDoor.NAME)) {
			// gets remote object
			TasksDoor door = (TasksDoor) locator.getRmiObject(TasksDoor.NAME);
			// send to JEM node the current process id.
			// uses JMX implementation of JDK.
			// BE CAREFUL! Not all JVM returns the value in same format
			// receives all roles for job user and stores in a static
			// reference
			// of realm
			JobStartedObjects objects = door.setJobStarted(JobId.VALUE, ManagementFactory.getRuntimeMXBean().getName());
			
			Collection<Role> myroles = objects.getRoles();
			// check if is already instantiated. If yes, does nothing
			// check if is already instantiated. If yes, does nothing
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new AntBatchSecurityManager(myroles));
			} else {
				throw new AntException(AntMessage.JEMA039E);
			}

			DataPathsContainer.createInstance(objects.getStorageGroupsManager());
		} else {
			throw new AntException(AntMessage.JEMA038E, TasksDoor.NAME);
		}
		Launcher.main(args);
	}

}