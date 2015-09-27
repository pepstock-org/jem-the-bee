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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.entities.Message;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class ResponseBuilder {
	
	public static final ResponseBuilder JSON = ResponseBuilder.media(MediaType.APPLICATION_JSON);
	
	public static final ResponseBuilder PLAIN = ResponseBuilder.media(MediaType.TEXT_PLAIN);
	
	public static final ResponseBuilder OCTET_STREAM = ResponseBuilder.media(MediaType.APPLICATION_OCTET_STREAM);

	private String mediaType = null;

	/**
	 * @param path
	 */
	private ResponseBuilder(String mediaType) {
		super();
		this.mediaType = mediaType;
	}

	static ResponseBuilder media(String mediaType){
		ResponseBuilder replacer = new ResponseBuilder(mediaType);
		return replacer;
	}
	
	/**
	 * Creates a bad request response if there are some invalid parameters
	 * @param obj object to return to outline the issue
	 * @return HTTP response with error
	 */
	Response badRequest(String parm){
		return Response.status(Status.BAD_REQUEST).entity(envelop(UserInterfaceMessage.JEMG071E.toMessage().getFormattedMessage(parm))).build();
	}
	
	/**
	 * Creates a not found response.
	 * @param obj key that is missing
	 * @return response with error
	 */
	Response notFound(Object obj){
		return Response.status(Status.NOT_FOUND).entity(envelop(obj)).build();
	}
	
	/**
	 * Creates a successful response with the object to return 
	 * @param obj object to return
	 * @return response to return
	 */
	Response ok(Object obj){
		if (obj != null){
			if (obj instanceof String || obj instanceof Boolean){
				return Response.ok().entity(envelop(obj)).build();
			}
		}
		return Response.ok().entity(obj).build();		
	}

	/**
	 * Creates the response when the user is not authorized to call the service
	 * @param e exception to include on the response
	 * @return HTTP response with error
	 */
	Response unauthorized(Exception e){
		return Response.status(Status.UNAUTHORIZED).entity(envelop(e.getMessage())).build();
	}
	
	/**
	 * Creates the response when a severe error occurs
	 * @param e exception to include on the response
	 * @return HTTP response with error
	 */
	Response severError(Exception e){
		return Response.serverError().entity(envelop(e.getMessage())).build();
	}
	
	/**
	 * Creates the response when JEm is not available
	 * @return HTTP response with error
	 */
	Response unableExcepton(){
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		return Response.status(Status.SERVICE_UNAVAILABLE).entity(envelop(msg)).build();
	}
	
	/**
	 * Creates a message with the value on JSON format for client
	 * @param obj object to send back
	 * @return a message to return on HTTP body
	 */
	private Object envelop(Object obj){
		if (MediaType.APPLICATION_JSON.equalsIgnoreCase(mediaType)){
			Message msg = new Message();
			msg.setValue(obj);
			return msg;
		} else {
			return obj;
		}
	}
}
