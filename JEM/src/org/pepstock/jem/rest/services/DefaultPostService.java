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

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.ReturnedObject;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * @param <T> Returned object
 * @param <S> parameter for REST call
 */
public class DefaultPostService<T extends ReturnedObject, S> {
	
	private RestClient client = null;
	
	private String service = null;
	
	private String subService = null;

	/**
	 * @param client 
	 * @param service 
	 * @param subService 
	 * 
	 */
	public DefaultPostService(RestClient client, String service, String subService) {
		this.client = client;
		this.service = service;
		this.subService = subService;
	}

	/**
	 * @return the client
	 */
	public RestClient getClient() {
		return client;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @return the subService
	 */
	public String getSubService() {
		return subService;
	}
	
	/**
	 * @param type 
	 * @param parameter
	 * @return
	 * @throws JemException
	 */
	public T execute(GenericType<JAXBElement<T>> type, S parameter) throws JemException{
		WebResource resource = getClient().getBaseWebResource();
		try {
			JAXBElement<T> jaxbContact = resource.path(getService()).path(getSubService()).accept(MediaType.APPLICATION_XML).post(type, parameter);
			T object = jaxbContact.getValue();
			ReturnedObject ro = (ReturnedObject) object;
			if (ro.hasException()) {
				throw new JemException(ro.getExceptionMessage());
			}
			return object;
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		}
	}
}
