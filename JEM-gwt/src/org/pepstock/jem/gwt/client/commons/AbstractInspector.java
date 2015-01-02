/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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


import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is an main class to extend for any inspector. This calculates automatically the right size of itself,
 * assuming to have a HEADER, CONTENT and ACTIONS (like a footer).<br>
 * ACTIONS could be optionally set.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class AbstractInspector extends PopupPanel {
	
	private int width = Window.getClientWidth()*3/4;
	
	private int height = Window.getClientHeight()*3/4;
	
	private int availableHeight = height - Sizes.INSPECTOR_HEADER_HEIGHT_PX;
	
	private int availableWidth = width;
	
	private VerticalPanel mainContainer = new VerticalPanel();
	
	private boolean hasActions = false;

	/**
	 * Empty constructor, without ACTIONS
	 */
	public AbstractInspector() {
		this(false);
	}
	
	/**
	 * Constructs the object take care if there will be ACTIONS panel or not.
	 * 
	 * @param hasActions if <code>true</code> ACTIONS is included
	 */
	public AbstractInspector(boolean hasActions) {
		// Popup panel constructor
		super(true, true);
		setGlassEnabled(true);
		// sets size
		this.hasActions = hasActions;
	}

	/**
	 * @return the width
	 */
	public final int getWidth() {
		return width;
	}

	
	/**
	 * @param width the width to set
	 */
	public final void setWidth(int width) {
		super.setWidth(Sizes.toString(width));
		this.width = width;
		this.availableWidth = width;
	}

	/**
	 * @param height the height to set
	 */
	public final void setHeight(int height) {
		super.setHeight(Sizes.toString(height));
		this.height = height;
		this.availableHeight = height - Sizes.INSPECTOR_HEADER_HEIGHT_PX;
	}

	/**
	 * @return the height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * @return the availableHeight
	 */
	public int getAvailableHeight() {
		return availableHeight;
	}

	/**
	 * @return the availableWidth
	 */
	public int getAvailableWidth() {
		return availableWidth;
	}

	/**
	 * Returns the header to add to PopupPanel.
	 * 
	 * @return flextable used like a header 
	 */
	public abstract FlexTable getHeader();
	
	/**
	 * Returns the content to add to PopupPanel.
	 * 
	 * @return every kind of Panel 
	 */
	public abstract Panel getContent();
	
	/**
	 * Returns the actions (a set of buttons) to add to PopupPanel.
	 * 
	 * @return every kind of Panel 
	 */
	public abstract Panel getActions();
	
	/**
	 * Before to show the PopupPanel, adds all components, setting the right size.
	 */
	@Override
	public void show(){
		setSize(Sizes.toString(width), Sizes.toString(height));
		// if Actions, reduce the amount of available height
		if (hasActions){
			// size of Actions is already fixed
			availableHeight -= Sizes.INSPECTOR_FOOTER_HEIGHT_PX;
		}
		
		// gets header, sets size and adds to popup panel
		FlexTable h = getHeader();
		h.setHeight(Sizes.toString(Sizes.INSPECTOR_HEADER_HEIGHT_PX));
		mainContainer.add(h);
		
		// gets contents, sets size and adds to popup panel
		Panel c = getContent();
		
		if (c instanceof ResizeCapable){
			ResizeCapable rc = (ResizeCapable)c;
			rc.onResize(getAvailableWidth(), getAvailableHeight());
		} else {
			c.setHeight(Sizes.toString(getAvailableHeight()));
		}
		mainContainer.add(c);

		// if has actions, gets actions, sets size and adds to popup panel
		if (hasActions){
			Panel a = getActions();
			a.setHeight(Sizes.toString(Sizes.INSPECTOR_FOOTER_HEIGHT_PX));
			mainContainer.add(a);
		}
		// sets main container
		setWidget(mainContainer);
		// and then shows it
		super.show();
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#onPreviewNativeEvent(com.google.gwt.user.client.NativePreviewEvent)
	 */
	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		if (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
			hide();
		}
	}

}