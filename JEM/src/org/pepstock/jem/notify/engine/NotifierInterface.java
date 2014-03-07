/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.notify.engine;

import org.pepstock.jem.notify.JemEmail;
import org.pepstock.jem.notify.NotifyObject;
import org.pepstock.jem.notify.exception.NotifyException;

/**
 * <code>NotifierInterface</code> is the base interface to make notification. For the moment 
 * is implemented by: <br>
 * - {@link EmailNotifier} : it makes notify by Email <br>
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 */
public interface NotifierInterface {
	
	/**
	 * This method makes the notify. It must be implemented by
	 * the concrete class. For example:
	 * {@link EmailNotifier#doNotify(NotifyObject)} 
	 * 
	 * @param notifyObject general object of notify, for example the email to 
	 * send. 
	 * 
	 * @see JemEmail
	 * @throws NotifyException if an error occurs.
	 */
	void doNotify(NotifyObject notifyObject) throws NotifyException;

}