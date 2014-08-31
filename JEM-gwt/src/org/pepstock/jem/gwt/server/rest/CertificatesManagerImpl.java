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
package org.pepstock.jem.gwt.server.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.gwt.server.services.CertificatesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.entities.BooleanReturnedObject;
import org.pepstock.jem.rest.entities.Certificates;
import org.pepstock.jem.rest.paths.CertificatesManagerPaths;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@Path(CertificatesManagerPaths.MAIN)
public class CertificatesManagerImpl extends DefaultServerResource {

	private CertificatesManager certificatesManager = null;

	/**
	 * REST service which returns list of collected sample in the JEM cluster
	 * @param filterParm 
	 * 
	 * @return a list of statistics from all nodes
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@POST
	@Path(CertificatesManagerPaths.GET)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Certificates getCertificates(String filterParm) throws JemException {
		Certificates result = new Certificates();
		if (isEnable()) {
			if (certificatesManager == null) {
				initManager();
			}
			try {
				result.setEntries(certificatesManager.getCertificates(filterParm));
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				result.setExceptionMessage(e.getMessage());
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}

	/**
	 * 
	 * @param parm
	 * @return
	 * @throws JemException
	 */
	@POST
	@Path(CertificatesManagerPaths.ADD)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public BooleanReturnedObject addCertificate(Certificates parm) throws JemException {
		BooleanReturnedObject result = new BooleanReturnedObject();
		result.setValue(false);
		if (isEnable()) {
			if (certificatesManager == null) {
				initManager();
			}
			if (parm.getCertificate() != null && parm.getAlias() != null){
				try {
					result.setValue(certificatesManager.addCertificate(parm.getCertificate(), parm.getAlias()));
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					result.setExceptionMessage(e.getMessage());
				}
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}
	/**
	 * 
	 * @param parm
	 * @return
	 * @throws JemException
	 */
	@POST
	@Path(CertificatesManagerPaths.REMOVE)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public BooleanReturnedObject removeCertificate(Certificates parm) throws JemException {
		BooleanReturnedObject result = new BooleanReturnedObject();
		result.setValue(false);
		if (isEnable()) {
			if (certificatesManager == null) {
				initManager();
			}
			if (parm.getEntries() != null && !parm.getEntries().isEmpty()){
				try {
					result.setValue(certificatesManager.removeCertificate(parm.getEntries()));
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					result.setExceptionMessage(e.getMessage());
				}
			}
		} else {
			setUnableExcepton(result);
		}
		return result;
	}

	/**
	 * Initialize the manager
	 */
	private synchronized void initManager() {
		if (certificatesManager == null) {
			certificatesManager = new CertificatesManager();
		}
	}
}
