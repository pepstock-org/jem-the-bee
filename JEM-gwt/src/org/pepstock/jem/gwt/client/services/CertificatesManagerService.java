/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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
import org.pepstock.jem.node.security.CertificateEntry;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides "remove" action for certificates management
 */
@RemoteServiceRelativePath(Services.CERTIFICATES)
public interface CertificatesManagerService extends RemoteService {
	
	/**
	 * Returns the certificates using a filter by alais name.
	 * @param filter alias name filter
	 * @return list of certificates
	 * @throws JemException if error occurs
	 */
	Collection<CertificateEntry> getCertificates(String filter) throws JemException;
	/**
	 * Removes a list of certificates
	 * @param entries list of certificates to be removed 
	 * @return always true
	 * @throws JemException if error occurs
	 */
	Boolean removeCertificates(Collection<CertificateEntry> entries) throws JemException;

}