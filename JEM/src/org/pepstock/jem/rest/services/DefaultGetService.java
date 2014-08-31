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

import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.ReturnedObject;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

/**
 * Service for REST calls, which contains the HTTP client, the service and subservice paths.<br>
 * It performs ONLY GET http method, using XML as protocol to de-serialize objects.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * @param <T> Returned object
 * @param <S>
 * @param <S> parameter for REST call
 */
class DefaultGetService<T extends ReturnedObject, S> extends AbstractRestService<T, S> {

	/**
	 * Constructs the REST service, storing HTTP client, service and subservice paths
	 * 
	 * @param client HTTP client 
	 * @param service service path
	 * @param subService subservice path
	 * 
	 */
	public DefaultGetService(RestClient client, String service, String subService) {
		super(client, service, subService);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.rest.services.AbstractRestService#run(com.sun.jersey.api.client.GenericType, java.lang.Object)
	 */
	@Override
	public JAXBElement<T> run(GenericType<JAXBElement<T>> type, S parameter) throws Exception {
		WebResource resource = getClient().getBaseWebResource();
		return resource.path(getService()).path(getSubService()).accept(MediaType.APPLICATION_XML).get(type);
	}
}
