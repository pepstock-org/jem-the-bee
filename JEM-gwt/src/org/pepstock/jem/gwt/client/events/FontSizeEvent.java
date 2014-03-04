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
package org.pepstock.jem.gwt.client.events;

import org.pepstock.jem.gwt.client.editor.FontSize;

import com.google.gwt.event.shared.GwtEvent;


/**
 * Event which will be fired when in a editor you change the font size.
 * This event notifies all other editor to change font size;
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class FontSizeEvent extends GwtEvent<FontSizeEventHandler> {
	
	/**
	 * The associated event {@link Type}
	 */
	public static final Type<FontSizeEventHandler> TYPE = new Type<FontSizeEventHandler>();

	private FontSize fontSize = null;
	
	/**
	 * Build the event with new font size.
	 * @param fontSize new font size to change
	 */
    public FontSizeEvent(FontSize fontSize) {
	    super();
	    this.fontSize = fontSize;
    }

	/**
	 * Returns font size
	 * @return the fontSize
	 */
	public FontSize getFontSize() {
		return fontSize;
	}

	/**
	 * Sets new fotn size
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(FontSize fontSize) {
		this.fontSize = fontSize;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FontSizeEventHandler> getAssociatedType() {
    	return TYPE;
    }

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
    @Override
    protected void dispatch(FontSizeEventHandler handler) {
    	handler.onChange(this);
    }

}
