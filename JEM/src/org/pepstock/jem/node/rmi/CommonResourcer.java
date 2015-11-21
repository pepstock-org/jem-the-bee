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
package org.pepstock.jem.node.rmi;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.Reference;

import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.util.rmi.RmiObject;

/**
 * is RMI interface for having datasources or other common resources.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface CommonResourcer extends RmiObject {

	/**
	 * RMI object ID for binding.
	 */
	String NAME = "COMMON_RESOURCE";

	/**
	 * Asks by name for the common resource.
	 * 
	 * @param jobId
	 * @param resourceName resource name
	 * @return resource as common resource
	 * @throws RemoteException occurs if errors
	 */
	Resource lookup(String jobId, String resourceName) throws RemoteException;

	/**
	 * Asks by type for JNDI reference of the custom common resource.
	 * 
	 * @param jobId
	 * @param resourceType resource type
	 * @return JDNI reference of custom common resource
	 * @throws RemoteException occurs if errors
	 */
	Reference lookupReference(String jobId, String resourceType) throws RemoteException;
	
	/**
	 * Returns the properties of the JEM factory plugin, identified by jobId.
	 * @param jobId jobId
	 * @return the properties
	 * @throws RemoteException if any error occurs
	 */
	Properties getJemFactoryProperties(String jobId) throws RemoteException;

}