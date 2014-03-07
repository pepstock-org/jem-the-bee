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
package org.pepstock.jem.gwt.client;

import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Is the component with all panels to show that has an error during the request
 * for checking if the session is already authenticated and retrieving teh
 * logged users.<br>
 * Remember when the JEM nodes are all down, receives the message id error
 * JEM0064.
 * 
 * @author Andrea "Stock" Stocchero
 * @see org.pepstock.jem.log.Messages#JEM0064E
 */
public class LoginErrorBox extends VerticalPanel {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.loginBox().ensureInjected();
	}

	private String exception = null;

	private final InlineHTML exceptionMessage = new InlineHTML();

	/**
	 * Constructs all components
	 */
	public LoginErrorBox() {
		setSpacing(10);
		addStyleName(Styles.INSTANCE.loginBox().grid());
		
		// for exception, sets empty string and red color, to outline error
		exceptionMessage.setText(" ");
		exceptionMessage.addStyleName(Styles.INSTANCE.common().red());

		// adds a link to try again to login, reloading the page (is enough!)
		Anchor reload = new Anchor("Click here to try again");
		reload.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.Location.reload();
			}
		});

		add(new HTML("Unexpected error during the login phase:"));
		add(exceptionMessage);
		add(reload);
	}

	/**
	 * @return the exception
	 */
	public String getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(String exception) {
		this.exception = exception;
		// sets the string to label component too
		exceptionMessage.setText(exception);
	}

}