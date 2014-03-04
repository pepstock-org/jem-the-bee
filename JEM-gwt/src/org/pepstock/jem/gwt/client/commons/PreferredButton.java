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
package org.pepstock.jem.gwt.client.commons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * Contains a set of predefined buttons, used also by {@link MessageBox}
 * @author Andrea "Stock" Stocchero
 */
public class PreferredButton extends Button {
	
	/**
	 * "OK" action number
	 */
	public static final int OK_ACTION = 0;
	
	/**
	 * "Cancel" action number
	 */
	public static final int CANCEL_ACTION = 1;
	
	/**
	 * "Yes" action number
	 */
	public static final int YES_ACTION = 2;
	
	/**
	 * "No" action number
	 */
	public static final int NO_ACTION = 3;

	/**
	 * "Relaod" action number
	 */
	public static final int RELOAD_ACTION = 4;

	
	/**
	 * "OK" button
	 */
	public static final PreferredButton OK = new PreferredButton("Ok", OK_ACTION);
	
	/**
	 * "CANCEL" button
	 */
	public static final PreferredButton CANCEL = new PreferredButton("Cancel", CANCEL_ACTION);
	
	/**
	 * "YES" button
	 */
	public static final PreferredButton YES = new PreferredButton("Yes", YES_ACTION);
	
	/**
	 * "NO" button
	 */
	public static final PreferredButton NO = new PreferredButton("No", NO_ACTION);

	/**
	 * "Reload" button
	 */
	public static final PreferredButton RELOAD = new PreferredButton("Reload", RELOAD_ACTION);

	private int action = OK_ACTION;
	
	private MessageBox messageBox = null;
	
	/**
	 * Creates button with text and action
	 * @param text text of button
	 * @param action action fired 
	 */
	private PreferredButton(String text, int action) {
		super(text);
		this.action = action;
		addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// checks if message box is not null
				if (messageBox != null){
					// hides message box
					messageBox.hide();
					// fired event on hide handler
				    if (messageBox.getHideHandler() != null){
				    	messageBox.getHideHandler().onHide((PreferredButton)event.getSource());
				    }
				}
			}
		});
	}
	
	/**
	 * Returns action
	 * @return the action
	 */
	public int getAction() {
		return action;
	}

	/**
	 * Sets action
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}

	/**
	 * Returns message box
	 * @return the messageBox
	 */
	public MessageBox getMessageBox() {
		return messageBox;
	}

	/**
	 * Sets Message box
	 * @param messageBox the messageBox to set
	 */
	public void setMessageBox(MessageBox messageBox) {
		this.messageBox = messageBox;
	}

}