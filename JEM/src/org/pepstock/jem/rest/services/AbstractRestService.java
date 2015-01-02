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

import javax.xml.bind.JAXBElement;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.ReturnedObject;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Abstract service for REST calls, which contains the HTTP client, the service and subservice paths.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * @param <T> Returned object
 * @param <S> parameter for REST call
 */
abstract class AbstractRestService<T extends ReturnedObject, S> {
	
	private RestClient client = null;
	
	private String service = null;
	
	private String subService = null;

	/**
	 * Constructs the REST service, storing HTTP client, service and subservice paths
	 * 
	 * @param client HTTP client 
	 * @param service service path
	 * @param subService subservice path
	 * 
	 */
	public AbstractRestService(RestClient client, String service, String subService) {
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
	 * Executes the REST calls, delegating the real call to run method. In this way is possible to extend it for different HTTP methods.
	 * 
	 * @param type returned generic type
	 * @param parameter parms for REST call
	 * @return Returned object
	 * @throws JemException if any exception occurs
	 */
	public final T execute(GenericType<JAXBElement<T>> type, S parameter) throws JemException{
		try {
			// calls run method. Extending and implementing it you can call a GET or POST methods
			JAXBElement<T> jaxbContact = run(type, parameter);
			T object = jaxbContact.getValue();
			// checks if there is an excpetion
			ReturnedObject ro = (ReturnedObject) object;
			if (ro.hasException()) {
				throw new JemException(ro.getExceptionMessage());
			}
			return object;
		} catch (UniformInterfaceException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			// if rc = 204, return null instead of exception
			if (e.getResponse().getStatus() != 204) {
				throw new JemException(e.getMessage(), e);
			}
			return null;
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new JemException(e);
		}
	}
	
	/**
	 * Executes the REST call, extending it is possible to have it for different HTTP methods.
	 * @param type returned generic type
	 * @param parameter parms for REST call
	 * @return Returned object
	 * @throws JemException if any exception occurs
	 */
	public abstract JAXBElement<T> run(GenericType<JAXBElement<T>> type, S parameter) throws JemException;
}
