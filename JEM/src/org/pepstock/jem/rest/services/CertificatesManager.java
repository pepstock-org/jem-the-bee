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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
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
	    	RequestBuilder builder = RequestBuilder.media(this);
			// creates the returned object
			ClientResponse response = builder.filter(filterParm).get(CertificatesManagerPaths.GET);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<CertificateEntry>)JsonUtil.getInstance().deserializeList(response, CertificateEntry.class);
			} else {
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
	    } catch (IOException e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Adds a new certificate for a specific alias. It returns <code>true</code> if it has been able to add it, otherwise <code>false</code>.
	 * @param certificate a set of bytes which represents the certificates
	 * @param alias userid associated to certificate
	 * @return returns <code>true</code> if it has been able to add it, otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public Boolean addCertificates(byte[] certificate, String alias) throws RestException {
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM);
		String path = PathReplacer.path(CertificatesManagerPaths.ADD).replace(CertificatesManagerPaths.ALIAS_PATH_PARAM, alias).build();
		// creates the returned object
		ClientResponse response = builder.post(path, certificate);
		String value = response.getEntity(String.class);
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(value);
		} else {
			throw new RestException(response.getStatus(), value);
		}
	}
	
	/**
	 * Removes a certificate by associated alias.
	 * @param alias alias of certificate to be removed
	 * @return returns <code>true</code> if it has been able to remove the entries, otherwise <code>false</code>
	 * @throws RestException if any exception occurs
	 */
	public Boolean removeCertificates(String alias) throws RestException {
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		String path = PathReplacer.path(CertificatesManagerPaths.REMOVE).replace(CertificatesManagerPaths.ALIAS_PATH_PARAM, alias).build();
		// creates the returned object
		ClientResponse response = builder.delete(path);
		String value = response.getEntity(String.class);
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(value);
		} else {
			throw new RestException(response.getStatus(), value);
		}
	}
}