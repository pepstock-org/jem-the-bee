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

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.entities.ReturnedObject;

/**
 * Abstract REST server resource, which provides a helpful method to check if the
 * JEM group is available or not.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class DefaultServerResource {

	/**
	 * Returns <code>true</code> if JEM group is available (at least one member up and running).
	 * 
	 * @return <code>true</code> if JEM group is available (at least one member up and running), otherwise <code>false</code>
	 */
	protected boolean isEnable(){
		return SharedObjects.getInstance().isDataClusterAvailable();
	}
	
	/**
	 * Sets the exception for JEM cluster not available to returned object of REST call.
	 * 
	 * @param object returned object of REST call
	 */
	void setUnableExcepton(ReturnedObject object){
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		object.setExceptionMessage(msg);
	}
}