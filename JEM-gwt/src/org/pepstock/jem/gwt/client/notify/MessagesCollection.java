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
package org.pepstock.jem.gwt.client.notify;

import java.util.LinkedList;
import java.util.List;

/**
 * Static container which collects all toasts. It can maintain maximum 100 messages.
 * When container has more then 100, FIFO policy is used
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class MessagesCollection {

	private static final int MAXIMUM_ITEMS = 100;
	
	private static final List<ToastMessage> MESSAGES = new LinkedList<ToastMessage>();
	
	/**
	 * To avoid any instantiation
	 */
    private MessagesCollection() {
    }

	/**
	 * Adds a new toast in the list. Checks if it has more than 100 messages. If yes, remove the first (the oldest one) in the list.
	 * 
	 * @param message toast to add
	 */
	public static final void add(ToastMessage message){
		MESSAGES.add(0, message);
		if (MESSAGES.size() > MAXIMUM_ITEMS){
			MESSAGES.remove(MESSAGES.size()-1);
		}
	}
	
	/**
	 * Returns the complete list to show in a table
	 * @return the complete list to show in a table
	 */
	public static final List<ToastMessage> getMessages(){
		return MESSAGES;
	}

}
