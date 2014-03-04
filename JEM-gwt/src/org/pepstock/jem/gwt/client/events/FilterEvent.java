/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.util.filters.FilterToken;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Event;

/**
 * An {@link Event} that holds a {@link FilterToken}
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class FilterEvent extends GwtEvent<FilterEventHandler> {

	/**
	 * The associated event {@link Type}
	 */
	public static final Type<FilterEventHandler> TYPE = new Type<FilterEventHandler>();
	
	private FilterToken filterToken = null; 
	
	/**
	 * Empty contructor
	 */
	public FilterEvent() {
	}
	
	/**
	 * Builds the Event
	 * @param token the {@link FilterToken} wrapped by the event
	 */
	public FilterEvent(FilterToken token) {
		filterToken = token;
	}

	/**
	 * @return the {@link FilterToken} wrapped by the event
	 */
	public FilterToken getFilterToken() {
		return filterToken;
	}

	/**
	 * Set the {@link FilterToken} wrapped by this event
	 * @param filterToken
	 */
	public void setFilterToken(FilterToken filterToken) {
		this.filterToken = filterToken;
	}

	@Override
	protected void dispatch(FilterEventHandler handler) {
		handler.onFilter(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FilterEventHandler> getAssociatedType() {
		return TYPE;
	}

}