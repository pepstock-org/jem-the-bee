/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.gwt.client.events.EventBus;
import org.pepstock.jem.gwt.client.events.FilterEvent;
import org.pepstock.jem.util.filters.FilterToken;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * A popup that let user to set a filter value
 * 
 * +-------------+
 * | name: value |
 * | instruct.   |
 * +-------------+
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class FilterInputPanel extends DelayedPopupPanel {

	private static Label description = new Label("ENTER = confirm; ESC = exit", false);
	static {
		Styles.INSTANCE.common().ensureInjected();
		description.addStyleName(Styles.INSTANCE.common().smallGreyDescription());
	}
 	
	private VerticalPanel popupContent = new VerticalPanel();
	
	private HorizontalPanel filterLine = new HorizontalPanel();
	private String filterName = null;
	private TextBox filterValue = new TextBox();
	
	/**
	 * Builds the panel
	 * @param filterToken the filter name token to be displayed
	 */
	public FilterInputPanel(String filterToken) {
		this(filterToken, null);
	}
	
	/**
	 * Builds the panel
	 * @param filterToken the filter name token to be displayed
	 * @param helpPattern an help pattern that the user should follow to set a valid filter
	 */
	public FilterInputPanel(String filterToken, final String helpPattern) {
		super(true, false, 1000);
		
		// container
		popupContent.setSpacing(1);
		popupContent.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		popupContent.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		// first line (input)
		
		filterName = filterToken;
		
		filterLine.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		filterLine.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		filterLine.setSpacing(5);
		
		Label filterLabel = new Label(filterToken + FilterToken.FILTER_TOKEN_SEPARATOR);
		
		filterLine.add(filterLabel);
		if (helpPattern != null && !helpPattern.trim().isEmpty()) {
			filterValue.setText(helpPattern);
		}
		filterLine.add(filterValue);

		// ESC key is not handled by onKeyPress
		filterValue.addKeyDownHandler(new FilterKeyDownHandler(helpPattern));
		
		popupContent.add(filterLine);
		
		// second line (description)
		popupContent.add(description);
		
		setWidget(popupContent);
	}

	private class FilterKeyDownHandler implements KeyDownHandler {
		
		private final String helpPattern;
		
		private FilterKeyDownHandler(String helpPattern) {
			this.helpPattern = helpPattern;
		}
		
		@Override
		public void onKeyDown(KeyDownEvent event) {
			switch (event.getNativeKeyCode()) {
			case KeyCodes.KEY_ENTER:
				if (!filterValue.getText().trim().isEmpty() && !filterValue.getText().equalsIgnoreCase(helpPattern)) {
					fireFilterEvent();
				}
				break;
			case KeyCodes.KEY_ESCAPE:
				stopProcessing();
				break;
			default:
				break;
			}
		}

		private void fireFilterEvent() {
			// build and send the event to eventbus
			FilterToken filterToken = new FilterToken(filterName, filterValue.getText());
			FilterEvent filterEvent = new FilterEvent(filterToken);
			EventBus.INSTANCE.fireEvent(filterEvent);
			stopProcessing();
		}
	}
	
	/**
	 * Set the focus on the filter value input box
	 */
	public void setFocus() {
		filterValue.setFocus(true);
		int valueLength = filterValue.getText().length();
		if (valueLength > 0) {
			filterValue.setSelectionRange(0, valueLength);
		}
	}
	
	/**
	 * Called when the remote call is ended. Sets show to false, because if the timer is still running,
	 * timer doesn't show the panel. 
	 */
	public void stopProcessing() {
		if (getDelay() != NO_DELAY && getTimer() != null){
			getTimer().cancel();
			setTimer(null);
		}
		hide();
	}

	/**
	 * Called before the remote call starts. Start a timer to avoid to show itself for quick requests.
	 */
	public void startProcessing() {
		if (getDelay() != NO_DELAY){
			// fixes 5 seconds to wait
			setTimer(new Timer() {
				@Override
				public void run() {
					show();
					setFocus();
				}
			});
			getTimer().schedule(getDelay());
		} else {
			show();	
			setFocus();
		}
	}

}