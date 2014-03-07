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
package org.pepstock.jem.node.events;

import java.util.*;

import javax.swing.event.*;

/**
 * Is a container of EventListener. Useful when you have a list of listeners to
 * engage by Class for some actions.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ListenersSystem {

	private EventListenerList listeners = null;

	/**
	 * Constructs the object, initializing a list for the event-listeners
	 */
	public ListenersSystem() {
		listeners = new EventListenerList();
	}

	/**
	 * Adds a new listener to the list
	 * 
	 * @param class1 class of listener
	 * @param listener listener instance
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addListener(Class class1, EventListener listener) {
		listeners.add(class1, listener);
	}

	/**
	 * Removes a listener from the list
	 * 
	 * @param clazz class of listener
	 * @param listener listener instance
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void removeListener(Class clazz, EventListener listener) {
		listeners.remove(clazz, listener);
	}

	/**
	 * Returns if there is some listeners of Class
	 * 
	 * @param clazz class of listener
	 * @return <code>true</code> if there is some listeners of Class
	 */
	@SuppressWarnings({ "rawtypes" })
	public boolean hasListener(Class clazz) {
		return listeners.getListenerCount(clazz) > 0;
	}

	/**
	 * Returns all listeners by a passed Class
	 * 
	 * @param clazz class of listener
	 * @return arrays of Listener
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EventListener[] getAllListeners(Class clazz) {
		return listeners.getListeners(clazz);
	}
}