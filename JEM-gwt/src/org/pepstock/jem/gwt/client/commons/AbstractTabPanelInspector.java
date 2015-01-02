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

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for Inspectors that contains a {@link TabPanel}
 * @author Andrea "Stock" Stocchero
 */
public abstract class AbstractTabPanelInspector extends AbstractInspector {
	
	private int availableHeight = 0;
	
	private int availableWidth = 0;

	/**
	 * Constructor for object, setting no actions
	 */
	public AbstractTabPanelInspector() {
		this(false);
	}

	/**
	 * Constructor for object
	 * @param hasActions if <code>true</code>, reserves a part for buttons on action panel
	 */
	public AbstractTabPanelInspector(boolean hasActions) {
		super(hasActions);
		
		// calculates height and width
		// tabpanel has got 6px padding on top, left and right
		// and 1px padding bottom
		availableHeight = super.getAvailableHeight() - Sizes.TABBAR_HEIGHT_PX - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_PADDING_BOTTOM - 
				Sizes.MAIN_TAB_PANEL_BORDER;
		availableWidth = super.getAvailableWidth() - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT - 
				Sizes.MAIN_TAB_PANEL_BORDER  - 
				Sizes.MAIN_TAB_PANEL_BORDER;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbtstractInspector#getAvailableHeight()
	 */
	@Override
	public final int getAvailableHeight() {
		return availableHeight;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbtstractInspector#getAvailableWidth()
	 */
	@Override
	public int getAvailableWidth() {
		return availableWidth;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbtstractInspector#getContent()
	 */
	@Override
	public final Panel getContent() {
		// gets tabpanel
		TabPanel panel = getTabPanel();
		panel.setWidth(Sizes.toString(super.getAvailableWidth()));
		
		// calls all ResizeCapable with the right width and height
		for (int i=0; i<panel.getWidgetCount(); i++){
			Widget w = panel.getWidget(i);
			if (w instanceof ResizeCapable){
				ResizeCapable rc = (ResizeCapable)w;
				rc.onResize(getAvailableWidth(), getAvailableHeight());
			}
		}
		
		//adds tab panel
    	VerticalPanel mainContainer = new VerticalPanel();
    	mainContainer.add(panel);
	    return mainContainer;
	}

	/**
	 * Returns the tab panel to add to popup panel
	 * @return the contained {@link TabPanel}
	 */
	public abstract TabPanel getTabPanel();

}