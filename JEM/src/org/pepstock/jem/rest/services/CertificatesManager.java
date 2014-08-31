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
package org.pepstock.jem.rest.services;

import java.util.Collection;

import javax.xml.bind.JAXBElement;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.security.CertificateEntry;
import org.pepstock.jem.rest.AbstractRestManager;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.BooleanReturnedObject;
import org.pepstock.jem.rest.entities.Certificates;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.paths.CertificatesManagerPaths;

import com.sun.jersey.api.client.GenericType;

/**
 * Client side of NODES service.
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
		super(restClient);
	}

	/**
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Collection<CertificateEntry> getCertificates(String filterParm) throws JemException {
		CertificatesPostService<Certificates, String> service = new CertificatesPostService<Certificates, String>(CertificatesManagerPaths.GET);
		GenericType<JAXBElement<Certificates>> generic = new GenericType<JAXBElement<Certificates>>() {

		};
		Certificates result = service.execute(generic, filterParm);
		return result.getEntries();
	}
	
	/**
	 * 
	 * @param certificate 
	 * @param alias 
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Boolean addCertificates(byte[] certificate, String alias) throws JemException {
		CertificatesPostService<BooleanReturnedObject, Certificates> service = new CertificatesPostService<BooleanReturnedObject, Certificates>(CertificatesManagerPaths.ADD);
		GenericType<JAXBElement<BooleanReturnedObject>> generic = new GenericType<JAXBElement<BooleanReturnedObject>>() {

		};
		if (certificate != null && alias != null){
			Certificates c = new Certificates();
			c.setCertificate(certificate);
			c.setAlias(alias);
			
			BooleanReturnedObject result = service.execute(generic, c);
			return result.isValue();
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param entries 
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Boolean removeCertificates(Collection<CertificateEntry> entries) throws JemException {
		CertificatesPostService<BooleanReturnedObject, Certificates> service = new CertificatesPostService<BooleanReturnedObject, Certificates>(CertificatesManagerPaths.REMOVE);
		GenericType<JAXBElement<BooleanReturnedObject>> generic = new GenericType<JAXBElement<BooleanReturnedObject>>() {

		};
		if (entries != null && !entries.isEmpty()){
			Certificates c = new Certificates();
			c.setEntries(entries);
		
			BooleanReturnedObject result = service.execute(generic, c);
			return result.isValue();
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class CertificatesPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public CertificatesPostService(String subService) {
			super(CertificatesManager.this.getClient(), CertificatesManagerPaths.MAIN, subService);
		}

	}

}