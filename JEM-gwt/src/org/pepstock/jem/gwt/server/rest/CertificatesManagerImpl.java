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
package org.pepstock.jem.gwt.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pepstock.jem.gwt.server.services.CertificatesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.paths.CertificatesManagerPaths;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.spi.resource.Singleton;

/**
 * REST services published in the web part, to manage certificates.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Singleton
@Path(CertificatesManagerPaths.MAIN)
public class CertificatesManagerImpl extends DefaultServerResource {

	private CertificatesManager certificatesManager = null;

	/**
	 * REST service which returns list of published certificates
	 * 
	 * @param filterParm filter for aliases
	 * 
	 * @return a list of published certificates
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@POST
	@Path(CertificatesManagerPaths.GET)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCertificates(String filterParm) {
		Response resp = check();
		if (resp == null){
			try{
				return ok(certificatesManager.getCertificates(filterParm));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * REST service which adds a new certificate
	 * @param alias alias of certificate
	 * @param certificate certificate to be added 
	 * 
	 * @return returns <code>true</code> if added correctly, otherwise <code>false</code>
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@POST
	@Path(CertificatesManagerPaths.ADD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCertificate(@PathParam(CertificatesManagerPaths.ALIAS) String alias, byte[] certificate) {
		Response resp = check();
		if (resp == null){
			try{
				if (certificate != null && alias != null) {
					return ok(certificatesManager.addCertificate(certificate, alias));
				} else {
					return Response.status(Status.BAD_REQUEST).build();
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return severError(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * REST service which removes a certificate by its alias
	 * 
	 * @param alias certificate's alias to be removed
	 * @return returns <code>true</code> if removed correctly, otherwise <code>false</code>
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@DELETE
	@Path(CertificatesManagerPaths.REMOVE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeCertificate(@PathParam(CertificatesManagerPaths.ALIAS) String alias) throws JemException {
		Response resp = check();
		if (resp == null){
			try{
				if (alias != null) {
					return ok(certificatesManager.removeCertificate(alias));
				} else {
					return Response.status(Status.BAD_REQUEST).build();
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return severError(e);
			}
		} else {
			return resp;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#initManager()
	 */
    @Override
    boolean init() throws Exception {
		if (certificatesManager == null) {
			certificatesManager = new CertificatesManager();
		}
	    return true;
    }
}
