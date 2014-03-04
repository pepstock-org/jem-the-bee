/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.Resource;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all actions for common
 * resources management
 */
@RemoteServiceRelativePath(Services.COMMON_RESOURCES)
public interface CommonResourcesManagerService extends RemoteService {

	/**
	 * Returns the common resources using a filter by common resource name.
	 * 
	 * @param filter
	 *            common resource name filter
	 * @return list of common resources
	 * @throws JemException
	 *             if error occurs
	 */
	Collection<Resource> getCommonResources(String filter) throws JemException;

	/**
	 * Adds a new common resource
	 * 
	 * @param resource
	 *            new common resource to add
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	Boolean addCommonResource(Resource resource) throws JemException;

	/**
	 * Updates an existing common resource
	 * 
	 * @param resource
	 *            common resource instance to update
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	Boolean updateCommonResource(Resource resource) throws JemException;

	/**
	 * Removes a list of common resources
	 * 
	 * @param resources
	 *            list of common resources to be removed
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	Boolean removeCommonResource(Collection<Resource> resources) throws JemException;

	/**
	 * 
	 * @param secret
	 *            secret to encryt
	 * @return encrypted secret
	 * @throws JemException
	 */
	CryptedValueAndHash getEncryptedSecret(String secret) throws JemException;

}