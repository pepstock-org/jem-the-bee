/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Event;

/**
 * An {@link Event} triggered when a submitter is closed
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class SubmitterClosedEvent extends GwtEvent<SubmitterClosedEventHandler> {

	/**
	 * The associated event {@link Type}
	 */
	public static final Type<SubmitterClosedEventHandler> TYPE = new Type<SubmitterClosedEventHandler>();
	
	private boolean switchSubmitter = false; 
	
	/**
	 * Empty contructor
	 */
	public SubmitterClosedEvent() {
	}
	
	/**
	 * Build the event
	 * @param switchSubmitter <code>true</code> if the user want switch submitter, <code>false</code> if user want simply close it  
	 */
	public SubmitterClosedEvent(boolean switchSubmitter) {
		this.switchSubmitter = switchSubmitter;
	}

	/**
	 * @return <code>true</code> if user want to switch the submitter, <code>false</code> if just want close it
	 */
	public boolean isSwitchSubmitter() {
		return switchSubmitter;
	}

	@Override
	protected void dispatch(SubmitterClosedEventHandler handler) {
		handler.onSubmitterClosed(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SubmitterClosedEventHandler> getAssociatedType() {
		return TYPE;
	}

}