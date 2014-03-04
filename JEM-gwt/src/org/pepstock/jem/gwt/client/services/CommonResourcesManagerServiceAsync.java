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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.Resource;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface CommonResourcesManagerServiceAsync {

	/**
	 * Returns the common resources using a filter by common resource name.
	 * 
	 * @param filter
	 *            common resource name filter
	 * @param callback
	 */
	void getCommonResources(String filter, AsyncCallback<Collection<Resource>> callback);

	/**
	 * Adds a new common resource
	 * 
	 * @param resource
	 *            new common resource to add
	 * @param callback
	 */
	void addCommonResource(Resource resource, AsyncCallback<Boolean> callback);

	/**
	 * Updates an existing common resource
	 * 
	 * @param resource
	 *            common resource instance to update
	 * @param callback
	 */
	void updateCommonResource(Resource resource, AsyncCallback<Boolean> callback);

	/**
	 * Removes a list of common resources
	 * 
	 * @param resources
	 *            list of common resources to be removed
	 * @param callback
	 */
	void removeCommonResource(Collection<Resource> resources, AsyncCallback<Boolean> callback);

	/**
	 * 
	 * @param secret
	 *            secret to encryt
	 * @param callback
	 */
	void getEncryptedSecret(String secret, AsyncCallback<CryptedValueAndHash> callback);

}