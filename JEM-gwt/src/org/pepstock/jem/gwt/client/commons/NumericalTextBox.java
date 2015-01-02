/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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
package org.pepstock.jem.gwt.client.commons;


import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is an extension of {@link TextBox} useful for numerical
 * fields in the forms.
 * It allows you to enter only numbers.
 * 
 * @see #TITLE
 * @see TextBox
 * @author Alessandro Zambrini
 *
 */
public class NumericalTextBox extends TextBox{
	
	private static final String PASTE_DISABLED_MESSAGE = "Only numbers are permitted. Paste disabled here!";

	/**
	 * Constructor that sets the  <code>required</code>  the title to {@link #TITLE} and
	 * the <code>KeyPressHandler</code> to {@link NumericalCheckHandler}. 
	 * 
	 * @see KeyPressHandler
	 */
	public NumericalTextBox(){
		super();
		setTitle(PASTE_DISABLED_MESSAGE);
		super.sinkEvents(Event.ONPASTE);
	    super.addKeyPressHandler(new NumericalCheckHandler());
	}
	
	
	/**
	 * Handler to check if the text of the <code>TextBox</code>
	 * is numerical while it is typed.
	 * 
	 * @see KeyPressHandler
	 * @author Alessandro Zambrini
	 */
	private class NumericalCheckHandler implements KeyPressHandler{
		@Override
		public void onKeyPress(KeyPressEvent event) {
			if(!Character.isDigit(event.getCharCode())){
				cancelKey();
			}
		}
	}
	
	
	/**
	 * Handler that disables paste action inside the <code>TextBox</code>.
	 * 
	 * @see Event
	 * @see Event#ONPASTE
	 */
	public void onBrowserEvent(Event event) {
	    super.onBrowserEvent(event);
	    if (event.getTypeInt() == Event.ONPASTE) {
            event.stopPropagation();
            event.preventDefault();	        	
	    }
	}
}