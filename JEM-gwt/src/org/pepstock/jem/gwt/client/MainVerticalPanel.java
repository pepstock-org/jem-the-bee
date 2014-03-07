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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the main panel of JEM home, it contains the header, the main area and the footer 
 * @author Andrea "Stock" Stocchero
 */
public class MainVerticalPanel extends VerticalPanel implements ProvidesResize, RequiresResize, ResizeCapable {

	private final SmallHeader header = new SmallHeader();
	private final MainTabPanel main = new MainTabPanel();
	private final Footer footer = new Footer();

	/**
	 * 
	 */
	public MainVerticalPanel() {
		main.addStyleName(Styles.INSTANCE.common().padding4424());
		add(header);
		add(main);
		add(footer);
	}
	
	/**
	 * @see RequiresResize#onResize()
	 */
	public void onResize() {
		int fullHeight = Window.getClientHeight();
		int fullWidth = Window.getClientWidth();

		onResize(fullWidth, fullHeight);
	}

	/**
	 * @see ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	/* HEIGHT */
    	int availableHeightInternal = availableHeight - Sizes.HEADER - Sizes.FOOTER
			- Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT
			- Sizes.MAIN_VERTICAL_PANEL_PADDING_BOTTOM;

		/* WIDTH */
		int availableWidthInternal = availableWidth 
			- Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT 
			- Sizes.MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT;
		
		header.setHeight(Sizes.toString(Sizes.HEADER));
		footer.setHeight(Sizes.toString(Sizes.FOOTER));
		footer.onResize(availableWidthInternal, Sizes.FOOTER);
		
		int mainTabPanelHeight = availableHeightInternal;
		main.setSize(Sizes.toString(availableWidthInternal), Sizes.toString(mainTabPanelHeight));
		main.onResize(availableWidthInternal, mainTabPanelHeight);
    }

}