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
package org.pepstock.jem.rest.services;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.CertificateEntry;
import org.pepstock.jem.rest.JsonUtil;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.paths.CertificatesManagerPaths;

import com.sun.jersey.api.client.ClientResponse;

/**
 * REST service to manage certificates for users.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CertificatesManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient REST client instance
	 */
	public CertificatesManager(RestClient restClient) {
		super(restClient, CertificatesManagerPaths.MAIN);
	}

	/**
	 * Returns all certificates for all users, using the filter 
	 * @param filterParm filter to have a subset of certificates
	 * @return a list of certificates
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<CertificateEntry> getCertificates(String filterParm) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = post(CertificatesManagerPaths.GET, filterParm);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<CertificateEntry>)JsonUtil.getInstance().deserializeList(response, CertificateEntry.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
//		CertificatesPostService<CertificatesRequest, String> service = new CertificatesPostService<CertificatesRequest, String>(CertificatesManagerPaths.GET);
//		GenericType<JAXBElement<CertificatesRequest>> generic = new GenericType<JAXBElement<CertificatesRequest>>() {
//
//		};
//		CertificatesRequest result = service.execute(generic, filterParm);
//		return result.getEntries();
	}
	
	/**
	 * Adds a new certificate for a specific alias. It returns <code>true</code> if it has been able to add it, otherwise <code>false</code>.
	 * @param certificate a set of bytes which represents the certificates
	 * @param alias userid associated to certificate
	 * @return returns <code>true</code> if it has been able to add it, otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public Boolean addCertificates(byte[] certificate, String alias) throws RestException {
	    try {
	    	String path = PathReplacer.path(CertificatesManagerPaths.ADD).replace(CertificatesManagerPaths.ALIAS_PATH_PARAM, alias).build();
			// creates the returned object
			ClientResponse response = post(path, certificate);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(Boolean.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return false;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Removes a certificate by associated alias.
	 * @param alias alias of certificate to be removed
	 * @return returns <code>true</code> if it has been able to remove the entries, otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public Boolean removeCertificates(String alias) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = delete(CertificatesManagerPaths.REMOVE.replace(CertificatesManagerPaths.ALIAS_PATH_PARAM, alias));
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(Boolean.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return false;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }	
	}
}