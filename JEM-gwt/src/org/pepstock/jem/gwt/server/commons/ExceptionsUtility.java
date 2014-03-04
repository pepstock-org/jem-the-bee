/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.server.commons;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Create an exception to inform when JEM group is not available
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class ExceptionsUtility {
	
	/**
	 * To avoid any instantiation
	 */
    private ExceptionsUtility() {
    }

	/**
	 * Create an exception to inform when JEM group is not available 
	 * 
	 * @return an exception to inform when JEM group is not available
	 */
	public static final JemException throwGroupNotAvailableException(){
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
		return new JemException(msg);
	}

}