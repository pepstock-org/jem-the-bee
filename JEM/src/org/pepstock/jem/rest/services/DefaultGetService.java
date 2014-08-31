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
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * @param <T> Returned object
 * @param <S>
 * @param <S> parameter for REST call
 */
class DefaultGetService<T extends ReturnedObject, S> extends AbstractRestService<T, S> {
	

	
//	/**
//	 * @param type 
//	 * @param parameter
//	 * @return
//	 * @throws JemException
//	 */
//	public T execute(GenericType<JAXBElement<T>> type, S parameter) throws JemException{
//		WebResource resource = getClient().getBaseWebResource();
//		try {
//			JAXBElement<T> jaxbContact = resource.path(getService()).path(getSubService()).accept(MediaType.APPLICATION_XML).post(type, parameter);
//			T object = jaxbContact.getValue();
//			ReturnedObject ro = (ReturnedObject) object;
//			if (ro.hasException()) {
//				throw new JemException(ro.getExceptionMessage());
//			}
//			return object;
//		} catch (UniformInterfaceException e) {
//			LogAppl.getInstance().debug(e.getMessage(), e);
//			if (e.getResponse().getStatus() != 204) {
//				throw new JemException(e.getMessage(), e);
//			}
//			return null;
//		}
//	}

	/**
	 * @param client
	 * @param service
	 * @param subService
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
