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
package org.pepstock.jem.gwt.server;

import java.util.ArrayList;
import java.util.Collection;

import org.pepstock.jem.gwt.client.services.CommonResourcesManagerService;
import org.pepstock.jem.gwt.server.services.CommonResourcesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.Resource;

/**
 * Is GWT server service which can provide all methods to manage common
 * resources
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CommonResourcesManagerServiceImpl extends DefaultManager implements CommonResourcesManagerService {

	private static final long serialVersionUID = 1L;

	private transient CommonResourcesManager resourcesManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.UserManagerService#getCommonResources()
	 */
	@Override
	public Collection<Resource> getCommonResources(String filter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (resourcesManager == null) {
			initManager();
		}

		try {
			return new ArrayList<Resource>(resourcesManager.values(filter));
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG037E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.UserManagerService#addCommonResource(org.
	 * pepstock.jem.gwt.client.security.CommonResource)
	 */
	@Override
	public Boolean addCommonResource(Resource resource) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (resourcesManager == null) {
			initManager();
		}
		try {
			return Boolean.valueOf(resourcesManager.put(resource));
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG037E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.UserManagerService#updateCommonResource(org
	 * .pepstock.jem.gwt.client.security.CommonResource)
	 */
	@Override
	public Boolean updateCommonResource(Resource resource) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (resourcesManager == null) {
			initManager();
		}
		try {
			return Boolean.valueOf(resourcesManager.put(resource));
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG037E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.UserManagerService#removeCommonResource(java
	 * .util.Collection)
	 */
	@Override
	public Boolean removeCommonResource(Collection<Resource> resources) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (resourcesManager == null) {
			initManager();
		}
		try {
			boolean result = true;
			for (Resource resource : resources) {
				result = result && resourcesManager.remove(resource.getName());
			}
			return Boolean.valueOf(result);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG037E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.services.CommonResourcesManagerService#
	 * getEncryptedSecret(java.lang.String)
	 */
	@Override
	public CryptedValueAndHash getEncryptedSecret(String secret) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (resourcesManager == null) {
			initManager();
		}
		try {
			return resourcesManager.getEncryptedSecret(secret);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG037E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/**
	 * Initializes a manager
	 * 
	 * @throws JemException
	 *             if any exception occurs
	 */
	private synchronized void initManager() throws JemException {
		if (resourcesManager == null) {
			try {
				resourcesManager = new CommonResourcesManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG037E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}

}
