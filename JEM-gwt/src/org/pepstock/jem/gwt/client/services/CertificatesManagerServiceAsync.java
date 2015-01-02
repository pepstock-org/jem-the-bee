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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.node.security.CertificateEntry;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public interface CertificatesManagerServiceAsync {

	/**
	 * 
	 * @param entries 
	 * @param callback 
	 * @see org.pepstock.jem.gwt.client.services.CertificatesManagerService#removeCertificates(java.util.Collection)
	 */
	void removeCertificates(Collection<CertificateEntry> entries, AsyncCallback<Boolean> callback);

	/**
	 * 
	 * @param filter 
	 * @param callback 
	 * @see org.pepstock.jem.gwt.client.services.CertificatesManagerService#getCertificates(java.lang.String)
	 */
	void getCertificates(String filter, AsyncCallback<Collection<CertificateEntry>> callback);

}
