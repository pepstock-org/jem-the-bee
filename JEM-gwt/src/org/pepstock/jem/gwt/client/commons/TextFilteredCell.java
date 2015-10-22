/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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

import java.util.HashSet;
import java.util.Set;

import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.Window;

/**
 * {@link TextCell} extension used to renderer {@link TextFilterableHeader} 
 * @author Marco "Fuzzo" Cuccato
 */
public class TextFilteredCell extends TextCell {

	private static final int CURSOR_SIZE = 20;
	private static final int X_DELTA = 275;
	
	private TextFilterableHeader header = null;
	private FilterInputPanel filterInputPanel = null;
	private String helpPattern = null;
	
	private boolean showable = true;
	
	/**
	 * Builds the object
	 */
	public TextFilteredCell() {
		this((String)null);
	}

	/**
	 * Builds the object
	 * @param helpPattern an help string to let user write input correctly
	 */
	public TextFilteredCell(String helpPattern) {
		super();
		this.helpPattern = helpPattern;
	}

	/**
	 * Builds the object
	 * @param renderer the renderer to be used
	 */
	public TextFilteredCell(SafeHtmlRenderer<String> renderer) {
		this(renderer, null);
	}
	
	/**
	 * Builds the object
	 * @param renderer the renderer to be used
	 * @param helpPattern an help string to let user write input correctly
	 */
	public TextFilteredCell(SafeHtmlRenderer<String> renderer, String helpPattern) {
		super(renderer);
		this.helpPattern = helpPattern;
	}

	/**
	 * @return the {@link TextFilterableHeader}
	 */
	public TextFilterableHeader getHeader() {
		return header;
	}

	/**
	 * Set the header
	 * @param header a {@link TextFilterableHeader}
	 */
	public void setHeader(TextFilterableHeader header) {
		this.header = header;
	}
	
	/**
	 * @return the showable flag
	 */
	public boolean isShowable() {
		return showable;
	}

	/**
	 * @param showable set the showable flag value
	 */
	public void setShowable(boolean showable) {
		this.showable = showable;
	}

	@Override
	public Set<String> getConsumedEvents() {
		// get consumed events from super
		Set<String> consumedEvents = super.getConsumedEvents();
		if (consumedEvents == null) {
			consumedEvents = new HashSet<String>();
		}
		// add my custom consumed event
		consumedEvents.add(BrowserEvents.MOUSEOVER);
		consumedEvents.add(BrowserEvents.MOUSEOUT);
		consumedEvents.add(BrowserEvents.CLICK);
		// return all
		return consumedEvents;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		try {
			// show filter panel only if there is a right-click event 
			if (BrowserEvents.MOUSEOVER.equals(event.getType())) {
				// does NOT show the browser context menu
				if (showable){
					showFilterPanel(event);
					showable = false;
				}
			} else if (BrowserEvents.MOUSEOUT.equals(event.getType())) {
				hideFilterPanel();
				showable = true;
			} else if (BrowserEvents.CLICK.equals(event.getType())) {
				hideFilterPanel();
				showable = false;				
			}
		} catch (Exception e) {
			LogClient.getInstance().warning(e.getMessage(), e);
			new Toast(MessageLevel.ERROR, "An error has occoured while displaying FilterPopupPanel", "UI Error!").show();
		}
	}
	
	/**
	 * Show a {@link FilterInputPanel}, checking if it is not too much right
	 * @param event
	 */
	protected void showFilterPanel(NativeEvent event) {
		filterInputPanel = new FilterInputPanel(header.getFilterName(), helpPattern);
		int x = event.getClientX() + CURSOR_SIZE;
		int y = event.getClientY() - CURSOR_SIZE;
		
		if (Window.getClientWidth() - x <= X_DELTA) {
			x -= X_DELTA; 
		}
		
		filterInputPanel.setPopupPosition(x, y);
		filterInputPanel.startProcessing();
	}

	/**
	 * @param event hides the {@link FilterInputPanel}
	 */
	protected void hideFilterPanel() {
		if (filterInputPanel != null) {
			filterInputPanel.stopProcessing();
		}
	}

}