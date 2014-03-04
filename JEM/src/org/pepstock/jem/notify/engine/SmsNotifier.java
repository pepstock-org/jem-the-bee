/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.notify.NotifyObject;
import org.pepstock.jem.notify.exception.NotifyException;
import org.pepstock.jem.notify.exception.SendMailException;

/**
 * Not implemented for now.
 * 
 * @author Alessandro Zambrini
 * 
 * if necessary it must be implemented.
 * @version 1.0	
 *
 */
public class SmsNotifier implements NotifierInterface{
	
	/**
	 * This method make the notification. It overrides {@link NotifierInterface#doNotify(NotifyObject)}
	 * In this case it sends a sms. It calls {@link #doSmsNotify(NotifyObject)}.
	 * 
	 * @param notifyObject general object of notify, 
	 * @throws NotifyException if an error occurs
	 */
	public void doNotify(NotifyObject notifyObject) throws NotifyException{
		this.doSmsNotify(notifyObject);
	}
	
	/**
	 * Sends a sms. To be implemented.
	 * 
	 * @param notifyObject
	 * @throws SendMailException
	 */
	private void doSmsNotify(NotifyObject notifyObject) throws SendMailException{
		LogAppl.getInstance().debug(notifyObject.toString());
	}
}