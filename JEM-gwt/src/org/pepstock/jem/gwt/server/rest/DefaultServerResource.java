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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;

/**
 * Abstract REST server resource, which provides a helpful method to check if the
 * JEM group is available or not.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class DefaultServerResource {
	
	final AtomicBoolean managerLoaded = new AtomicBoolean(false);
	
	final Response check(){
		if (isEnable()){
			try {
				if (!managerLoaded.get()){
					synchronized (managerLoaded) {
						managerLoaded.set(init());
                    }
				}
	            return null;
            } catch (Exception e) {
            	LogAppl.getInstance().ignore(e.getMessage(), e);
            	return severError(e);
            }
		} else {
			return unableExcepton();
		}
	}

	boolean init() throws Exception{
		return true;
	}
	
	/**
	 * Returns <code>true</code> if JEM group is available (at least one member up and running).
	 * 
	 * @return <code>true</code> if JEM group is available (at least one member up and running), otherwise <code>false</code>
	 */
	final boolean isEnable(){
		return SharedObjects.getInstance().isDataClusterAvailable();
	}
	
	final Response ok(){
		return Response.ok().build();
	}
	
	final Response badRequest(Object obj){
		return Response.status(Status.BAD_REQUEST).entity(obj).build();
	}
	
	final Response ok(Object obj){
		return Response.ok().entity(obj).build();
	}
	
	final Response unauthorized(Exception e){
		return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();
	}
	
	final Response severError(Exception e){
		return Response.serverError().entity(e.getMessage()).build();
	}
	
	final Response unableExcepton(){
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		return Response.status(Status.SERVICE_UNAVAILABLE).entity(msg).build();
	}
}