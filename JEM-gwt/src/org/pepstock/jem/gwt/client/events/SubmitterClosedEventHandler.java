/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler of {@link SubmitterClosedEvent} 
 * @author Marco "Fuzzo" Cuccato
 */
public interface SubmitterClosedEventHandler extends EventHandler {

	/**
	 * Triggered when a {@link SubmitterClosedEvent} occours
	 * @param event the {@link SubmitterClosedEvent}
	 */
	void onSubmitterClosed(SubmitterClosedEvent event);
	
}