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

import java.util.Collection;

import org.pepstock.jem.gwt.client.services.CertificatesManagerService;
import org.pepstock.jem.gwt.server.services.CertificatesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.CertificateEntry;

/**
 * This service manages all roles and their authorizations and users relations
 * inside of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CertificatesManagerServiceImpl extends DefaultManager implements CertificatesManagerService {

	private static final long serialVersionUID = 1L;

	private transient CertificatesManager certificatesManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.services.CertificatesManagerService#
	 * getCertificates(java.lang.String)
	 */
	@Override
	public Collection<CertificateEntry> getCertificates(String filter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (certificatesManager == null) {
			initManager();
		}
		try {
			return certificatesManager.getCertificates(filter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG065E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.services.CertificatesManagerService#
	 * removeCertificates(java.util.Collection)
	 */
	@Override
	public Boolean removeCertificates(Collection<CertificateEntry> entries) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (certificatesManager == null) {
			initManager();
		}
		try {
			return certificatesManager.removeCertificate(entries);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG065E, ex);
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
		if (certificatesManager == null) {
			try {
				certificatesManager = new CertificatesManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG065E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}

}